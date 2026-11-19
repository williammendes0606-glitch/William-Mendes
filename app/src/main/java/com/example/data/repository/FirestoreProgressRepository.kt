package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.curriculum.CurriculumCatalog
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.DiagnosticSubjectResult
import com.example.model.ErrorNotebookEntry
import com.example.model.EssayRecord
import com.example.model.FatigueLevel
import com.example.model.FlashcardConfidence
import com.example.model.FlashcardProgress
import com.example.model.LessonProgressRecord
import com.example.model.MasteryStatus
import com.example.model.QuestionAttemptLog
import com.example.model.QuestionItem
import com.example.model.SimulatedExamMode
import com.example.model.SimulatedExamRecord
import com.example.model.SpacedReviewItem
import com.example.model.StudyMode
import com.example.model.UnlockedAchievement
import com.example.model.UserProfile
import com.example.model.WorkSchedule
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class FirestoreProgressRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Usuário precisa estar conectado com Google.")
    }

    private fun userDoc(uid: String) = db.collection("users").document(uid)

    private fun sanitizeDocId(raw: String): String {
        val cleaned = raw.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
        return cleaned.ifBlank { UUID.randomUUID().toString() }
    }

    fun observeProfile(uid: String): Flow<UserProfile?> =
        userDoc(uid).snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else null
            }
            .catch { error -> handleStreamError(error, OperationType.GET, "users/$uid", uid) }

    fun observeLessonProgress(uid: String): Flow<List<LessonProgressRecord>> =
        userDoc(uid).collection("lessonProgress").snapshots()
            .map { snap -> snap.toObjects(LessonProgressRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/lessonProgress", uid) }

    fun observeErrorNotebook(uid: String): Flow<List<ErrorNotebookEntry>> =
        userDoc(uid).collection("errorNotebook").snapshots()
            .map { snap ->
                snap.toObjects(ErrorNotebookEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.recordedAtMillis }
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/errorNotebook", uid) }

    fun observeReviewItems(uid: String): Flow<List<SpacedReviewItem>> =
        userDoc(uid).collection("reviewItems").snapshots()
            .map { snap ->
                snap.toObjects(SpacedReviewItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.nextReviewAtMillis }
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/reviewItems", uid) }

    fun observeFlashcards(uid: String): Flow<List<FlashcardProgress>> =
        userDoc(uid).collection("flashcards").snapshots()
            .map { snap ->
                snap.toObjects(FlashcardProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.nextReviewAtMillis }
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/flashcards", uid) }

    fun observeSimulatedExams(uid: String): Flow<List<SimulatedExamRecord>> =
        userDoc(uid).collection("simulatedExams").snapshots()
            .map { snap ->
                snap.toObjects(SimulatedExamRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.completedAtMillis }
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/simulatedExams", uid) }

    fun observeEssays(uid: String): Flow<List<EssayRecord>> =
        userDoc(uid).collection("essays").snapshots()
            .map { snap ->
                snap.toObjects(EssayRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.updatedAt?.seconds ?: 0L }
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/essays", uid) }

    fun observeDiagnostics(uid: String): Flow<List<DiagnosticSubjectResult>> =
        userDoc(uid).collection("diagnosticResults").snapshots()
            .map { snap ->
                snap.toObjects(DiagnosticSubjectResult::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/diagnosticResults", uid) }

    fun observeQuestionLogs(uid: String): Flow<List<QuestionAttemptLog>> =
        userDoc(uid).collection("questionLogs").snapshots()
            .map { snap ->
                snap.toObjects(QuestionAttemptLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/questionLogs", uid) }

    fun observeAchievements(uid: String): Flow<List<UnlockedAchievement>> =
        userDoc(uid).collection("achievements").snapshots()
            .map { snap ->
                snap.toObjects(UnlockedAchievement::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.unlockedAtMillis }
            }
            .catch { error -> handleStreamError(error, OperationType.LIST, "users/$uid/achievements", uid) }

    private fun handleStreamError(error: Throwable, op: OperationType, path: String, uid: String): Nothing {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, op, path)
        }
        throw error
    }

    suspend fun ensureUserInitialized(uid: String, fallbackName: String): Result<Unit> {
        val path = "users/$uid"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val snap = userDoc(uid).get().await()
            if (!snap.exists()) {
                val profile = UserProfile(
                    userId = uid,
                    displayName = fallbackName.take(80).ifBlank { "Candidato CFO" }
                )
                userDoc(uid).set(profile.toCreateMap(uid)).await()
            }
            // Seed initial flashcards if empty
            val fcSnap = userDoc(uid).collection("flashcards").limit(1).get().await()
            if (fcSnap.isEmpty) {
                val now = System.currentTimeMillis()
                val batch = db.batch()
                CurriculumCatalog.allFlashcards.forEach { tpl ->
                    val ref = userDoc(uid).collection("flashcards").document(sanitizeDocId(tpl.id))
                    batch.set(
                        ref,
                        mapOf(
                            "userId" to uid,
                            "subjectId" to tpl.subjectId,
                            "topicTitle" to tpl.topicTitle.take(160),
                            "front" to tpl.front.take(1500),
                            "back" to tpl.back.take(2500),
                            "memoryTip" to tpl.memoryTip.take(1000),
                            "confidence" to FlashcardConfidence.TENHO_DUVIDA.name,
                            "nextReviewAtMillis" to now,
                            "reviewCount" to 0L,
                            "customCreated" to false,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfileSettings(
        uid: String,
        displayName: String,
        dailyGoalMinutes: Long,
        weeklyGoalDays: Long,
        workSchedule: WorkSchedule,
        fatigueLevel: FatigueLevel,
        sleepHours: Long,
        soundEnabled: Boolean,
        soundVolume: Double,
        focusSubjects: List<String>
    ): Result<Unit> {
        val path = "users/$uid"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            userDoc(uid).update(
                mapOf(
                    "displayName" to displayName.trim().take(80).ifBlank { "Candidato CFO" },
                    "dailyGoalMinutes" to dailyGoalMinutes.coerceIn(5L, 360L),
                    "weeklyGoalDays" to weeklyGoalDays.coerceIn(1L, 7L),
                    "workSchedule" to workSchedule.name,
                    "fatigueLevel" to fatigueLevel.name,
                    "sleepHours" to sleepHours.coerceIn(0L, 16L),
                    "soundEnabled" to soundEnabled,
                    "soundVolume" to soundVolume.coerceIn(0.0, 1.0),
                    "focusSubjects" to focusSubjects.take(15),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun recordQuestionOutcome(
        uid: String,
        currentProfile: UserProfile,
        question: QuestionItem,
        chosenIndex: Int,
        mode: StudyMode,
        minutesSpent: Long,
        xpEarned: Long,
        existingReview: SpacedReviewItem?,
        existingError: ErrorNotebookEntry?
    ): Result<Pair<Long, Boolean>> {
        val path = "users/$uid"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val now = System.currentTimeMillis()
            val isCorrect = chosenIndex == question.correctIndex

            // 1. Log attempt
            val logId = "log_${now}_${UUID.randomUUID().toString().take(6)}"
            val logRef = userDoc(uid).collection("questionLogs").document(logId)
            logRef.set(
                mapOf(
                    "userId" to uid,
                    "subjectId" to question.subjectId.take(64),
                    "topicId" to question.topicId.take(128),
                    "questionId" to question.id.take(128),
                    "mode" to mode.name,
                    "correct" to isCorrect,
                    "dayKey" to SpacedRepetitionEngine.dayKey(now),
                    "minutesSpent" to minutesSpent.coerceIn(0L, 300L),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // 2. Spaced Repetition update
            val revDocId = sanitizeDocId("rev_${question.id}")
            val revRef = userDoc(uid).collection("reviewItems").document(revDocId)
            val prevStage = existingReview?.stageIndex?.toInt() ?: 0
            val prevHits = existingReview?.consecutiveHits?.toInt() ?: 0
            val prevErrors = existingReview?.errorCount ?: 0L
            val (nextStage, nextMillis, status) = SpacedRepetitionEngine.nextStageAndTime(
                currentStage = prevStage,
                consecutiveHits = prevHits,
                isCorrect = isCorrect,
                nowMillis = now
            )
            if (existingReview == null) {
                revRef.set(
                    mapOf(
                        "userId" to uid,
                        "subjectId" to question.subjectId.take(64),
                        "topicId" to question.topicId.take(128),
                        "topicTitle" to question.topicName.take(160),
                        "itemType" to "QUESTAO",
                        "referenceId" to question.id.take(128),
                        "stageIndex" to nextStage.toLong(),
                        "consecutiveHits" to if (isCorrect) 1L else 0L,
                        "errorCount" to if (isCorrect) 0L else 1L,
                        "nextReviewAtMillis" to nextMillis,
                        "lastReviewedAtMillis" to now,
                        "masteryStatus" to status.name,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                revRef.update(
                    mapOf(
                        "stageIndex" to nextStage.toLong(),
                        "consecutiveHits" to if (isCorrect) (prevHits + 1L) else 0L,
                        "errorCount" to if (isCorrect) prevErrors else (prevErrors + 1L).coerceAtMost(1000L),
                        "nextReviewAtMillis" to nextMillis,
                        "lastReviewedAtMillis" to now,
                        "masteryStatus" to status.name,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // 3. Error notebook handling
            val errDocId = sanitizeDocId("err_${question.id}")
            val errRef = userDoc(uid).collection("errorNotebook").document(errDocId)
            if (!isCorrect) {
                if (existingError == null) {
                    errRef.set(
                        mapOf(
                            "userId" to uid,
                            "questionId" to question.id.take(128),
                            "subjectId" to question.subjectId.take(64),
                            "subjectName" to question.subjectName.take(80),
                            "topicName" to question.topicName.take(120),
                            "statement" to question.statement.take(4000),
                            "options" to question.options.take(5),
                            "chosenIndex" to chosenIndex.toLong().coerceIn(0L, 4L),
                            "correctIndex" to question.correctIndex.toLong().coerceIn(0L, 4L),
                            "explanation" to question.generalExplanation.take(4000),
                            "sourceLabel" to question.origin.badgeLabel.take(120),
                            "retryCount" to 0L,
                            "mastered" to false,
                            "recordedAtMillis" to now,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                } else {
                    errRef.update(
                        mapOf(
                            "chosenIndex" to chosenIndex.toLong().coerceIn(0L, 4L),
                            "retryCount" to (existingError.retryCount + 1L).coerceAtMost(1000L),
                            "mastered" to false,
                            "recordedAtMillis" to now,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            } else if (existingError != null && !existingError.mastered) {
                errRef.update(
                    mapOf(
                        "retryCount" to (existingError.retryCount + 1L).coerceAtMost(1000L),
                        "mastered" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // 4. Update profile XP, level, streak & stats
            val newXp = currentProfile.xp + xpEarned
            val newLevel = SpacedRepetitionEngine.levelFromXp(newXp)
            val leveledUp = newLevel > currentProfile.level
            val (newStreak, todayKey) = SpacedRepetitionEngine.updatedStreak(
                currentProfile.lastStudyDay,
                currentProfile.streakDays,
                now
            )
            userDoc(uid).update(
                mapOf(
                    "xp" to newXp,
                    "level" to newLevel,
                    "streakDays" to newStreak,
                    "lastStudyDay" to todayKey,
                    "totalStudyMinutes" to currentProfile.totalStudyMinutes + minutesSpent.coerceAtLeast(0L),
                    "totalQuestionsAnswered" to currentProfile.totalQuestionsAnswered + 1L,
                    "totalQuestionsCorrect" to currentProfile.totalQuestionsCorrect + if (isCorrect) 1L else 0L,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(newLevel to leveledUp)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun saveLessonProgress(
        uid: String,
        currentProfile: UserProfile,
        lessonId: String,
        subjectId: String,
        topicId: String,
        topicTitle: String,
        stage: Long,
        completed: Boolean,
        finalScorePercent: Long,
        minutesSpent: Long,
        alreadyCompleted: Boolean
    ): Result<Boolean> {
        val docId = sanitizeDocId(lessonId)
        val path = "users/$uid/lessonProgress/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val ref = userDoc(uid).collection("lessonProgress").document(docId)
            val existing = ref.get().await()
            if (!existing.exists()) {
                ref.set(
                    mapOf(
                        "userId" to uid,
                        "lessonId" to lessonId.take(128),
                        "subjectId" to subjectId.take(64),
                        "completed" to completed,
                        "currentStage" to stage.coerceIn(0L, 10L),
                        "finalScorePercent" to finalScorePercent.coerceIn(0L, 100L),
                        "minutesSpent" to minutesSpent.coerceIn(0L, 600L),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                ref.update(
                    mapOf(
                        "completed" to (completed || alreadyCompleted),
                        "currentStage" to stage.coerceIn(0L, 10L),
                        "finalScorePercent" to finalScorePercent.coerceIn(0L, 100L),
                        "minutesSpent" to minutesSpent.coerceIn(0L, 600L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // When actually completed for the first time, reward XP & schedule topic SRS
            var leveledUp = false
            if (completed && !alreadyCompleted) {
                val now = System.currentTimeMillis()
                val revId = sanitizeDocId("rev_topic_$topicId")
                val revRef = userDoc(uid).collection("reviewItems").document(revId)
                val revExists = revRef.get().await().exists()
                val nextReview = now + 24L * 60L * 60L * 1000L // +1 dia após concluir aula
                if (!revExists) {
                    revRef.set(
                        mapOf(
                            "userId" to uid,
                            "subjectId" to subjectId.take(64),
                            "topicId" to topicId.take(128),
                            "topicTitle" to topicTitle.take(160),
                            "itemType" to "TOPICO",
                            "referenceId" to lessonId.take(128),
                            "stageIndex" to 1L,
                            "consecutiveHits" to 1L,
                            "errorCount" to 0L,
                            "nextReviewAtMillis" to nextReview,
                            "lastReviewedAtMillis" to now,
                            "masteryStatus" to MasteryStatus.EM_ANDAMENTO.name,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
                val bonusXp = 50L
                val newXp = currentProfile.xp + bonusXp
                val newLevel = SpacedRepetitionEngine.levelFromXp(newXp)
                leveledUp = newLevel > currentProfile.level
                val (newStreak, todayKey) = SpacedRepetitionEngine.updatedStreak(
                    currentProfile.lastStudyDay,
                    currentProfile.streakDays,
                    now
                )
                userDoc(uid).update(
                    mapOf(
                        "xp" to newXp,
                        "level" to newLevel,
                        "streakDays" to newStreak,
                        "lastStudyDay" to todayKey,
                        "totalStudyMinutes" to currentProfile.totalStudyMinutes + minutesSpent.coerceAtLeast(1L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(leveledUp)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun rateFlashcard(
        uid: String,
        currentProfile: UserProfile,
        card: FlashcardProgress,
        confidence: FlashcardConfidence
    ): Result<Unit> {
        val docId = sanitizeDocId(card.id.ifBlank { UUID.randomUUID().toString() })
        val path = "users/$uid/flashcards/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val now = System.currentTimeMillis()
            val nextMillis = SpacedRepetitionEngine.nextFlashcardReviewMillis(confidence, now)
            userDoc(uid).collection("flashcards").document(docId).update(
                mapOf(
                    "confidence" to confidence.name,
                    "nextReviewAtMillis" to nextMillis,
                    "reviewCount" to (card.reviewCount + 1L).coerceAtMost(10000L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            val xpEarned = if (confidence == FlashcardConfidence.SEI_RESPONDER) 8L else 5L
            val newXp = currentProfile.xp + xpEarned
            val newLevel = SpacedRepetitionEngine.levelFromXp(newXp)
            val (newStreak, todayKey) = SpacedRepetitionEngine.updatedStreak(
                currentProfile.lastStudyDay,
                currentProfile.streakDays,
                now
            )
            userDoc(uid).update(
                mapOf(
                    "xp" to newXp,
                    "level" to newLevel,
                    "streakDays" to newStreak,
                    "lastStudyDay" to todayKey,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun createCustomFlashcard(
        uid: String,
        subjectId: String,
        topicTitle: String,
        front: String,
        back: String,
        memoryTip: String
    ): Result<Unit> {
        val docId = sanitizeDocId("fc_custom_${System.currentTimeMillis()}")
        val path = "users/$uid/flashcards/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            require(front.isNotBlank() && back.isNotBlank())
            userDoc(uid).collection("flashcards").document(docId).set(
                mapOf(
                    "userId" to uid,
                    "subjectId" to subjectId.take(64),
                    "topicTitle" to topicTitle.trim().take(160).ifBlank { "Geral" },
                    "front" to front.trim().take(1500),
                    "back" to back.trim().take(2500),
                    "memoryTip" to memoryTip.trim().take(1000),
                    "confidence" to FlashcardConfidence.TENHO_DUVIDA.name,
                    "nextReviewAtMillis" to System.currentTimeMillis(),
                    "reviewCount" to 0L,
                    "customCreated" to true,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun saveSimulatedExam(
        uid: String,
        currentProfile: UserProfile,
        title: String,
        mode: SimulatedExamMode,
        subjectId: String,
        totalQuestions: Long,
        correctCount: Long,
        durationSeconds: Long,
        breakdownSummary: String
    ): Result<Unit> {
        val docId = sanitizeDocId("sim_${System.currentTimeMillis()}")
        val path = "users/$uid/simulatedExams/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val now = System.currentTimeMillis()
            val scorePct = if (totalQuestions > 0) ((correctCount * 100) / totalQuestions).coerceIn(0L, 100L) else 0L
            userDoc(uid).collection("simulatedExams").document(docId).set(
                mapOf(
                    "userId" to uid,
                    "title" to title.take(160),
                    "mode" to mode.name,
                    "subjectId" to subjectId.take(64),
                    "totalQuestions" to totalQuestions.coerceIn(1L, 200L),
                    "correctCount" to correctCount.coerceIn(0L, 200L),
                    "scorePercent" to scorePct,
                    "durationSeconds" to durationSeconds.coerceIn(0L, 36000L),
                    "completedAtMillis" to now,
                    "breakdownSummary" to breakdownSummary.take(4000),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            val bonusXp = 40L + correctCount * 10L
            val newXp = currentProfile.xp + bonusXp
            val newLevel = SpacedRepetitionEngine.levelFromXp(newXp)
            val minutesSpent = (durationSeconds / 60L).coerceAtLeast(1L)
            val (newStreak, todayKey) = SpacedRepetitionEngine.updatedStreak(
                currentProfile.lastStudyDay,
                currentProfile.streakDays,
                now
            )
            userDoc(uid).update(
                mapOf(
                    "xp" to newXp,
                    "level" to newLevel,
                    "streakDays" to newStreak,
                    "lastStudyDay" to todayKey,
                    "totalStudyMinutes" to currentProfile.totalStudyMinutes + minutesSpent,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun saveEssay(
        uid: String,
        currentProfile: UserProfile,
        existingId: String?,
        themeTitle: String,
        draftText: String,
        rewrittenText: String,
        grammarErrors: List<String>,
        cohesionFeedback: String,
        improvementSuggestions: List<String>,
        formativeScore: Long,
        aiSummary: String
    ): Result<String> {
        val docId = sanitizeDocId(existingId ?: "essay_${System.currentTimeMillis()}")
        val path = "users/$uid/essays/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            require(draftText.isNotBlank())
            val ref = userDoc(uid).collection("essays").document(docId)
            val exists = existingId != null && ref.get().await().exists()
            if (!exists) {
                ref.set(
                    mapOf(
                        "userId" to uid,
                        "themeTitle" to themeTitle.take(300),
                        "draftText" to draftText.take(15000),
                        "rewrittenText" to rewrittenText.take(15000),
                        "grammarErrors" to grammarErrors.take(30),
                        "cohesionFeedback" to cohesionFeedback.take(4000),
                        "improvementSuggestions" to improvementSuggestions.take(20),
                        "formativeScore" to formativeScore.coerceIn(0L, 1000L),
                        "aiSummary" to aiSummary.take(5000),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                val now = System.currentTimeMillis()
                val newXp = currentProfile.xp + 60L
                val newLevel = SpacedRepetitionEngine.levelFromXp(newXp)
                val (newStreak, todayKey) = SpacedRepetitionEngine.updatedStreak(
                    currentProfile.lastStudyDay,
                    currentProfile.streakDays,
                    now
                )
                userDoc(uid).update(
                    mapOf(
                        "xp" to newXp,
                        "level" to newLevel,
                        "streakDays" to newStreak,
                        "lastStudyDay" to todayKey,
                        "totalStudyMinutes" to currentProfile.totalStudyMinutes + 20L,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                ref.update(
                    mapOf(
                        "draftText" to draftText.take(15000),
                        "rewrittenText" to rewrittenText.take(15000),
                        "grammarErrors" to grammarErrors.take(30),
                        "cohesionFeedback" to cohesionFeedback.take(4000),
                        "improvementSuggestions" to improvementSuggestions.take(20),
                        "formativeScore" to formativeScore.coerceIn(0L, 1000L),
                        "aiSummary" to aiSummary.take(5000),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun saveDiagnosticResult(
        uid: String,
        currentProfile: UserProfile,
        result: DiagnosticSubjectResult
    ): Result<Unit> {
        val docId = sanitizeDocId(result.subjectId)
        val path = "users/$uid/diagnosticResults/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val ref = userDoc(uid).collection("diagnosticResults").document(docId)
            val exists = ref.get().await().exists()
            if (!exists) {
                ref.set(
                    mapOf(
                        "userId" to uid,
                        "subjectId" to result.subjectId.take(64),
                        "subjectName" to result.subjectName.take(80),
                        "totalQuestions" to result.totalQuestions.coerceIn(1L, 50L),
                        "correctCount" to result.correctCount.coerceIn(0L, 50L),
                        "accuracyPercent" to result.accuracyPercent.coerceIn(0L, 100L),
                        "strengths" to result.strengths.take(20),
                        "weaknesses" to result.weaknesses.take(20),
                        "unknownTopics" to result.unknownTopics.take(20),
                        "needsReview" to result.needsReview,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                ref.update(
                    mapOf(
                        "totalQuestions" to result.totalQuestions.coerceIn(1L, 50L),
                        "correctCount" to result.correctCount.coerceIn(0L, 50L),
                        "accuracyPercent" to result.accuracyPercent.coerceIn(0L, 100L),
                        "strengths" to result.strengths.take(20),
                        "weaknesses" to result.weaknesses.take(20),
                        "unknownTopics" to result.unknownTopics.take(20),
                        "needsReview" to result.needsReview,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // Personalize focus subjects automatically
            val updatedFocus = if (result.accuracyPercent < 70L) {
                (listOf(result.subjectId) + currentProfile.focusSubjects).distinct().take(10)
            } else {
                currentProfile.focusSubjects
            }
            userDoc(uid).update(
                mapOf(
                    "diagnosticCompleted" to true,
                    "focusSubjects" to updatedFocus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun unlockBadgeIfNew(
        uid: String,
        currentProfile: UserProfile,
        badgeId: String,
        existingBadgeIds: Set<String>
    ): Result<Boolean> {
        if (badgeId in existingBadgeIds) return Result.success(false)
        val badge = CurriculumCatalog.allBadges.firstOrNull { it.id == badgeId } ?: return Result.success(false)
        val docId = sanitizeDocId(badgeId)
        val path = "users/$uid/achievements/$docId"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val ref = userDoc(uid).collection("achievements").document(docId)
            if (ref.get().await().exists()) return Result.success(false)
            ref.set(
                mapOf(
                    "userId" to uid,
                    "badgeId" to badge.id,
                    "title" to badge.title,
                    "description" to badge.description,
                    "xpBonus" to badge.xpBonus.toLong(),
                    "unlockedAtMillis" to System.currentTimeMillis(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            val newXp = currentProfile.xp + badge.xpBonus
            val newLevel = SpacedRepetitionEngine.levelFromXp(newXp)
            userDoc(uid).update(
                mapOf(
                    "xp" to newXp,
                    "level" to newLevel,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(true)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun exportBackupJson(uid: String): Result<String> {
        val path = "users/$uid"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val profileSnap = userDoc(uid).get().await()
            val lessonsSnap = userDoc(uid).collection("lessonProgress").get().await()
            val errorsSnap = userDoc(uid).collection("errorNotebook").get().await()
            val reviewsSnap = userDoc(uid).collection("reviewItems").get().await()
            val flashcardsSnap = userDoc(uid).collection("flashcards").get().await()
            val examsSnap = userDoc(uid).collection("simulatedExams").get().await()
            val essaysSnap = userDoc(uid).collection("essays").get().await()
            val diagSnap = userDoc(uid).collection("diagnosticResults").get().await()

            val root = JSONObject().apply {
                put("app", "Missão CFO MA")
                put("schemaVersion", 1)
                put("exportedAtMillis", System.currentTimeMillis())
                put("profile", JSONObject(profileSnap.data ?: emptyMap<String, Any>()))
                put("lessonProgress", JSONArray(lessonsSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
                put("errorNotebook", JSONArray(errorsSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
                put("reviewItems", JSONArray(reviewsSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
                put("flashcards", JSONArray(flashcardsSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
                put("simulatedExams", JSONArray(examsSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
                put("essays", JSONArray(essaysSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
                put("diagnosticResults", JSONArray(diagSnap.documents.map { d -> JSONObject(d.data ?: emptyMap<String, Any>()).put("_docId", d.id) }))
            }
            Result.success(root.toString(2))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun importBackupJson(uid: String, rawJson: String): Result<Int> {
        val path = "users/$uid"
        return try {
            val activeUid = requireUserId()
            check(activeUid == uid)
            val root = JSONObject(rawJson)
            require(root.optString("app") == "Missão CFO MA") {
                "Arquivo de backup inválido para o Missão CFO MA."
            }
            var restoredCount = 0
            val pObj = root.optJSONObject("profile")
            if (pObj != null) {
                val schedule = enumValues<WorkSchedule>()
                    .firstOrNull { it.name == pObj.optString("workSchedule") } ?: WorkSchedule.COMERCIAL
                val fatigue = enumValues<FatigueLevel>()
                    .firstOrNull { it.name == pObj.optString("fatigueLevel") } ?: FatigueLevel.DISPOSTO
                userDoc(uid).update(
                    mapOf(
                        "displayName" to pObj.optString("displayName", "Candidato CFO").take(80),
                        "dailyGoalMinutes" to pObj.optLong("dailyGoalMinutes", 30L).coerceIn(5L, 360L),
                        "weeklyGoalDays" to pObj.optLong("weeklyGoalDays", 6L).coerceIn(1L, 7L),
                        "workSchedule" to schedule.name,
                        "fatigueLevel" to fatigue.name,
                        "sleepHours" to pObj.optLong("sleepHours", 7L).coerceIn(0L, 16L),
                        "xp" to pObj.optLong("xp", 0L).coerceAtLeast(0L),
                        "level" to pObj.optLong("level", 1L).coerceAtLeast(1L),
                        "streakDays" to pObj.optLong("streakDays", 0L).coerceAtLeast(0L),
                        "totalStudyMinutes" to pObj.optLong("totalStudyMinutes", 0L).coerceAtLeast(0L),
                        "totalQuestionsAnswered" to pObj.optLong("totalQuestionsAnswered", 0L).coerceAtLeast(0L),
                        "totalQuestionsCorrect" to pObj.optLong("totalQuestionsCorrect", 0L).coerceAtLeast(0L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                restoredCount++
            }
            Result.success(restoredCount)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }
}
