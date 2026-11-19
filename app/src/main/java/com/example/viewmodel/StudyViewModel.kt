package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundCue
import com.example.audio.SoundEffectsManager
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.repository.FirestoreProgressRepository
import com.example.data.repository.SpacedRepetitionEngine
import com.example.data.repository.StudyRecommendation
import com.example.model.DiagnosticSubjectResult
import com.example.model.ErrorNotebookEntry
import com.example.model.EssayRecord
import com.example.model.FatigueLevel
import com.example.model.FlashcardConfidence
import com.example.model.FlashcardProgress
import com.example.model.LessonProgressRecord
import com.example.model.QuestionAttemptLog
import com.example.model.QuestionItem
import com.example.model.SimulatedExamMode
import com.example.model.SimulatedExamRecord
import com.example.model.SpacedReviewItem
import com.example.model.StudyMode
import com.example.model.UiState
import com.example.model.UnlockedAchievement
import com.example.model.UserProfile
import com.example.model.WorkSchedule
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StudyDashboardData(
    val profile: UserProfile,
    val lessonProgress: List<LessonProgressRecord>,
    val errorNotebook: List<ErrorNotebookEntry>,
    val reviewItems: List<SpacedReviewItem>,
    val flashcards: List<FlashcardProgress>,
    val simulatedExams: List<SimulatedExamRecord>,
    val essays: List<EssayRecord>,
    val diagnostics: List<DiagnosticSubjectResult>,
    val questionLogs: List<QuestionAttemptLog>,
    val achievements: List<UnlockedAchievement>
) {
    val pendingReviews: List<SpacedReviewItem>
        get() {
            val now = System.currentTimeMillis()
            return reviewItems.filter { it.nextReviewAtMillis <= now }
        }

    val pendingFlashcards: List<FlashcardProgress>
        get() {
            val now = System.currentTimeMillis()
            return flashcards.filter { it.nextReviewAtMillis <= now }
        }

    val activeErrors: List<ErrorNotebookEntry>
        get() = errorNotebook.filter { !it.mastered }

    val masteredErrorsCount: Int
        get() = errorNotebook.count { it.mastered }

    val completedLessonsCount: Int
        get() = lessonProgress.count { it.completed }

    val overallEditalProgressPercent: Int
        get() {
            val totalLessons = CurriculumCatalog.allLessons.size.coerceAtLeast(1)
            val lessonFactor = (completedLessonsCount.toFloat() / totalLessons.toFloat()) * 0.55f
            val totalTopics = CurriculumCatalog.allSubjects.sumOf { it.topics.size }.coerceAtLeast(1)
            val reviewedTopics = reviewItems.map { it.topicId }.distinct().size
            val topicFactor = (reviewedTopics.toFloat() / totalTopics.toFloat()) * 0.45f
            return ((lessonFactor + topicFactor) * 100f).toInt().coerceIn(0, 100)
        }

    val todayMinutesStudied: Int
        get() {
            val today = SpacedRepetitionEngine.dayKey()
            return questionLogs.filter { it.dayKey == today }.sumOf { it.minutesSpent.toInt() }
        }

    val constancyRecommendation: StudyRecommendation
        get() = SpacedRepetitionEngine.recommendConstancy(
            fatigue = profile.fatigueEnum(),
            sleepHours = profile.sleepHours.toInt(),
            dailyGoalMinutes = profile.dailyGoalMinutes.toInt(),
            pendingReviewsCount = pendingReviews.size
        )
}

class StudyViewModel(
    private val repository: FirestoreProgressRepository,
    val soundManager: SoundEffectsManager,
    private val currentUserId: String,
    initialDisplayName: String
) : ViewModel() {

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val events: SharedFlow<String> = _events.asSharedFlow()

    private val _backupJsonPreview = MutableStateFlow<String?>(null)
    val backupJsonPreview: StateFlow<String?> = _backupJsonPreview.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserInitialized(currentUserId, initialDisplayName)
                .onFailure { e ->
                    Log.e(TAG, "Failed to initialize user profile", e)
                    _events.tryEmit("Erro ao inicializar perfil: ${e.localizedMessage ?: "verifique a conexão"}")
                }
        }
    }

    private fun <T> Flow<T>.unwrapFirestoreErrors(): Flow<Result<T>> =
        this.combine(MutableStateFlow(Unit)) { data, _ -> Result.success(data) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Stream error", rootError)
                emit(Result.failure(rootError))
            }

    private val coreStreams = combine(
        repository.observeProfile(currentUserId).unwrapFirestoreErrors(),
        repository.observeLessonProgress(currentUserId).unwrapFirestoreErrors(),
        repository.observeErrorNotebook(currentUserId).unwrapFirestoreErrors(),
        repository.observeReviewItems(currentUserId).unwrapFirestoreErrors(),
        repository.observeFlashcards(currentUserId).unwrapFirestoreErrors()
    ) { profRes, lessonRes, errRes, revRes, fcRes ->
        val firstFailure = listOf(profRes, lessonRes, errRes, revRes, fcRes).firstOrNull { it.isFailure }
        if (firstFailure != null) {
            Result.failure(firstFailure.exceptionOrNull()!!)
        } else {
            Result.success(
                Quint(
                    profRes.getOrNull() ?: UserProfile(userId = currentUserId),
                    lessonRes.getOrNull().orEmpty(),
                    errRes.getOrNull().orEmpty(),
                    revRes.getOrNull().orEmpty(),
                    fcRes.getOrNull().orEmpty()
                )
            )
        }
    }

    private val extraStreams = combine(
        repository.observeSimulatedExams(currentUserId).unwrapFirestoreErrors(),
        repository.observeEssays(currentUserId).unwrapFirestoreErrors(),
        repository.observeDiagnostics(currentUserId).unwrapFirestoreErrors(),
        repository.observeQuestionLogs(currentUserId).unwrapFirestoreErrors(),
        repository.observeAchievements(currentUserId).unwrapFirestoreErrors()
    ) { simRes, essayRes, diagRes, logRes, achRes ->
        val firstFailure = listOf(simRes, essayRes, diagRes, logRes, achRes).firstOrNull { it.isFailure }
        if (firstFailure != null) {
            Result.failure(firstFailure.exceptionOrNull()!!)
        } else {
            Result.success(
                Quint(
                    simRes.getOrNull().orEmpty(),
                    essayRes.getOrNull().orEmpty(),
                    diagRes.getOrNull().orEmpty(),
                    logRes.getOrNull().orEmpty(),
                    achRes.getOrNull().orEmpty()
                )
            )
        }
    }

    val dashboardState: StateFlow<UiState<StudyDashboardData>> =
        combine(coreStreams, extraStreams) { coreRes, extraRes ->
            val err = coreRes.exceptionOrNull() ?: extraRes.exceptionOrNull()
            if (err != null) {
                UiState.Error(err.localizedMessage ?: "Falha ao carregar progresso.")
            } else {
                val c = coreRes.getOrThrow()
                val e = extraRes.getOrThrow()
                soundManager.enabled = c.a.soundEnabled
                soundManager.volume = c.a.soundVolume.toFloat()
                UiState.Success(
                    StudyDashboardData(
                        profile = c.a,
                        lessonProgress = c.b,
                        errorNotebook = c.c,
                        reviewItems = c.d,
                        flashcards = c.e,
                        simulatedExams = e.a,
                        essays = e.b,
                        diagnostics = e.c,
                        questionLogs = e.d,
                        achievements = e.e
                    )
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    private fun currentData(): StudyDashboardData? =
        (dashboardState.value as? UiState.Success)?.data

    fun recordQuestionAnswer(
        question: QuestionItem,
        chosenIndex: Int,
        mode: StudyMode,
        minutesSpent: Long = 2L
    ) {
        val data = currentData() ?: return
        val isCorrect = chosenIndex == question.correctIndex
        soundManager.play(if (isCorrect) SoundCue.CORRECT else SoundCue.WRONG)

        val xpEarned = when {
            isCorrect && mode == StudyMode.CADERNO_ERROS -> 20L
            isCorrect -> 15L
            else -> 4L
        }
        val existingRev = data.reviewItems.firstOrNull { it.referenceId == question.id }
        val existingErr = data.errorNotebook.firstOrNull { it.questionId == question.id }

        viewModelScope.launch {
            val res = repository.recordQuestionOutcome(
                uid = currentUserId,
                currentProfile = data.profile,
                question = question,
                chosenIndex = chosenIndex,
                mode = mode,
                minutesSpent = minutesSpent,
                xpEarned = xpEarned,
                existingReview = existingRev,
                existingError = existingErr
            )
            res.onSuccess { (_, leveledUp) ->
                if (leveledUp) {
                    soundManager.play(SoundCue.LEVEL_UP)
                    _events.tryEmit("Você subiu de nível! (+${xpEarned} XP)")
                } else if (isCorrect) {
                    soundManager.play(SoundCue.XP_GAIN)
                }
                checkAndUnlockBadges(data)
            }.onFailure { e ->
                _events.tryEmit("Erro ao salvar questão: ${e.localizedMessage ?: "tente novamente"}")
            }
        }
    }

    fun completeLesson(
        lessonId: String,
        subjectId: String,
        topicId: String,
        topicTitle: String,
        stage: Long,
        completed: Boolean,
        finalScorePercent: Long,
        minutesSpent: Long
    ) {
        val data = currentData() ?: return
        val alreadyCompleted = data.lessonProgress.any { it.lessonId == lessonId && it.completed }
        viewModelScope.launch {
            repository.saveLessonProgress(
                uid = currentUserId,
                currentProfile = data.profile,
                lessonId = lessonId,
                subjectId = subjectId,
                topicId = topicId,
                topicTitle = topicTitle,
                stage = stage,
                completed = completed,
                finalScorePercent = finalScorePercent,
                minutesSpent = minutesSpent,
                alreadyCompleted = alreadyCompleted
            ).onSuccess { leveledUp ->
                if (completed && !alreadyCompleted) {
                    soundManager.play(if (leveledUp) SoundCue.LEVEL_UP else SoundCue.MISSION_COMPLETE)
                    _events.tryEmit("Aula concluída! +50 XP e revisão programada para amanhã.")
                    unlockBadge("badge_primeira_aula", data)
                    unlockBadge("badge_primeira_missao", data)
                }
            }.onFailure { e ->
                _events.tryEmit("Erro ao salvar aula: ${e.localizedMessage}")
            }
        }
    }

    fun notifyMissionCompleted(label: String) {
        soundManager.play(SoundCue.MISSION_COMPLETE)
        val data = currentData() ?: return
        viewModelScope.launch {
            unlockBadge("badge_primeira_missao", data)
            if (data.profile.streakDays >= 3L) {
                unlockBadge("badge_constancia_3", data)
            }
        }
        _events.tryEmit("$label concluída com sucesso!")
    }

    fun rateFlashcard(card: FlashcardProgress, confidence: FlashcardConfidence) {
        val data = currentData() ?: return
        soundManager.play(
            when (confidence) {
                FlashcardConfidence.SEI_RESPONDER -> SoundCue.CORRECT
                FlashcardConfidence.TENHO_DUVIDA -> SoundCue.XP_GAIN
                FlashcardConfidence.NAO_SEI -> SoundCue.WRONG
            }
        )
        viewModelScope.launch {
            repository.rateFlashcard(currentUserId, data.profile, card, confidence)
                .onSuccess { unlockBadge("badge_primeira_missao", data) }
                .onFailure { _events.tryEmit("Erro ao salvar flashcard.") }
        }
    }

    fun createFlashcard(subjectId: String, topicTitle: String, front: String, back: String, tip: String) {
        viewModelScope.launch {
            repository.createCustomFlashcard(currentUserId, subjectId, topicTitle, front, back, tip)
                .onSuccess { _events.tryEmit("Flashcard adicionado ao seu baralho!") }
                .onFailure { _events.tryEmit("Não foi possível criar o flashcard.") }
        }
    }

    fun saveSimulatedExam(
        title: String,
        mode: SimulatedExamMode,
        subjectId: String,
        totalQuestions: Long,
        correctCount: Long,
        durationSeconds: Long,
        breakdownSummary: String
    ) {
        val data = currentData() ?: return
        soundManager.play(SoundCue.MISSION_COMPLETE)
        viewModelScope.launch {
            repository.saveSimulatedExam(
                uid = currentUserId,
                currentProfile = data.profile,
                title = title,
                mode = mode,
                subjectId = subjectId,
                totalQuestions = totalQuestions,
                correctCount = correctCount,
                durationSeconds = durationSeconds,
                breakdownSummary = breakdownSummary
            ).onSuccess {
                unlockBadge("badge_simulado", data)
                unlockBadge("badge_primeira_missao", data)
                _events.tryEmit("Simulado salvo no histórico!")
            }.onFailure {
                _events.tryEmit("Erro ao registrar simulado.")
            }
        }
    }

    fun saveDiagnostic(result: DiagnosticSubjectResult) {
        val data = currentData() ?: return
        soundManager.play(SoundCue.MISSION_COMPLETE)
        viewModelScope.launch {
            repository.saveDiagnosticResult(currentUserId, data.profile, result)
                .onSuccess {
                    unlockBadge("badge_diagnostico", data)
                    _events.tryEmit("Diagnóstico salvo! Seu plano foi ajustado.")
                }
                .onFailure { _events.tryEmit("Erro ao salvar diagnóstico.") }
        }
    }

    fun saveEssay(
        existingId: String?,
        themeTitle: String,
        draftText: String,
        rewrittenText: String,
        grammarErrors: List<String>,
        cohesionFeedback: String,
        improvementSuggestions: List<String>,
        formativeScore: Long,
        aiSummary: String
    ) {
        val data = currentData() ?: return
        soundManager.play(SoundCue.MISSION_COMPLETE)
        viewModelScope.launch {
            repository.saveEssay(
                uid = currentUserId,
                currentProfile = data.profile,
                existingId = existingId,
                themeTitle = themeTitle,
                draftText = draftText,
                rewrittenText = rewrittenText,
                grammarErrors = grammarErrors,
                cohesionFeedback = cohesionFeedback,
                improvementSuggestions = improvementSuggestions,
                formativeScore = formativeScore,
                aiSummary = aiSummary
            ).onSuccess {
                unlockBadge("badge_redacao", data)
                _events.tryEmit("Redação salva com feedback!")
            }.onFailure {
                _events.tryEmit("Erro ao salvar redação.")
            }
        }
    }

    fun updateSettings(
        displayName: String,
        dailyGoalMinutes: Long,
        weeklyGoalDays: Long,
        workSchedule: WorkSchedule,
        fatigueLevel: FatigueLevel,
        sleepHours: Long,
        soundEnabled: Boolean,
        soundVolume: Double,
        focusSubjects: List<String>
    ) {
        soundManager.enabled = soundEnabled
        soundManager.volume = soundVolume.toFloat()
        viewModelScope.launch {
            repository.updateProfileSettings(
                uid = currentUserId,
                displayName = displayName,
                dailyGoalMinutes = dailyGoalMinutes,
                weeklyGoalDays = weeklyGoalDays,
                workSchedule = workSchedule,
                fatigueLevel = fatigueLevel,
                sleepHours = sleepHours,
                soundEnabled = soundEnabled,
                soundVolume = soundVolume,
                focusSubjects = focusSubjects
            ).onSuccess {
                _events.tryEmit("Configurações e plano atualizados!")
            }.onFailure {
                _events.tryEmit("Falha ao atualizar configurações.")
            }
        }
    }

    fun testSound() {
        soundManager.play(SoundCue.MEDAL_UNLOCKED)
    }

    fun prepareBackupExport(onJsonReady: (String) -> Unit) {
        viewModelScope.launch {
            repository.exportBackupJson(currentUserId)
                .onSuccess { json ->
                    _backupJsonPreview.value = json
                    onJsonReady(json)
                }
                .onFailure { e ->
                    _events.tryEmit("Erro ao gerar backup: ${e.localizedMessage}")
                }
        }
    }

    fun importBackupJson(rawJson: String) {
        viewModelScope.launch {
            repository.importBackupJson(currentUserId, rawJson)
                .onSuccess {
                    soundManager.play(SoundCue.MISSION_COMPLETE)
                    _events.tryEmit("Backup restaurado com sucesso!")
                }
                .onFailure { e ->
                    _events.tryEmit("Backup inválido: ${e.localizedMessage}")
                }
        }
    }

    private suspend fun checkAndUnlockBadges(data: StudyDashboardData) {
        unlockBadge("badge_primeira_missao", data)
        if (data.profile.streakDays >= 3L) {
            unlockBadge("badge_constancia_3", data)
        }
        if (data.masteredErrorsCount >= 3) {
            unlockBadge("badge_erros_superados", data)
        }
    }

    private suspend fun unlockBadge(badgeId: String, data: StudyDashboardData) {
        val existingIds = data.achievements.map { it.badgeId }.toSet()
        repository.unlockBadgeIfNew(currentUserId, data.profile, badgeId, existingIds)
            .onSuccess { newlyUnlocked ->
                if (newlyUnlocked) {
                    soundManager.play(SoundCue.MEDAL_UNLOCKED)
                    val title = CurriculumCatalog.allBadges.firstOrNull { it.id == badgeId }?.title ?: "Medalha"
                    _events.tryEmit("Conquista desbloqueada: $title!")
                }
            }
    }

    private data class Quint<A, B, C, D, E>(
        val a: A,
        val b: B,
        val c: C,
        val d: D,
        val e: E
    )

    private companion object {
        const val TAG = "StudyViewModel"
    }
}
