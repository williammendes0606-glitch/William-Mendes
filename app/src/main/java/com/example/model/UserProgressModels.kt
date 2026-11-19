package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class WorkSchedule(val label: String, val hint: String) {
    COMERCIAL("Horário Comercial", "Blocos matinais curtos e estudo estruturado à noite."),
    ESCALA_12X36("Escala 12x36", "Alterna dias leves no plantão com dias completos na folga."),
    NOTURNO("Turno Noturno", "Prioriza descanso pós-plantão e sessões após sono reparador."),
    TEMPO_INTEGRAL("Dedicação Integral", "Permite blocos completos com revisões programadas.")
}

enum class FatigueLevel(val label: String, val description: String) {
    DISPOSTO("Disposto", "Energia alta para aulas profundas e simulados."),
    MODERADO("Moderado", "Ritmo equilibrado com foco em prática."),
    CANSADO("Cansado", "Melhor focar em revisão leve e flashcards."),
    EXAUSTO("Exausto", "Priorize descanso e sono; faça no máximo 3–5 min leves se desejar.")
}

enum class ConstancyTier(
    val label: String,
    val durationLabel: String,
    val summary: String
) {
    LEVE("LEVE", "5 a 10 min", "3 a 5 questões fáceis, revisão curta e flashcards de memorização."),
    MODERADO("MODERADO", "15 a 25 min", "Mini-aula focada, 5 a 10 questões e revisão de erros."),
    COMPLETO("COMPLETO", "45 a 90 min", "Aula aprofundada, questões em 3 níveis e revisões programadas.")
}

enum class StudyMode {
    AULA,
    RAPIDO,
    CONSTANCIA,
    REVISAO,
    SIMULADO,
    DIAGNOSTICO,
    CADERNO_ERROS
}

enum class FlashcardConfidence(val label: String, val nextDays: Int) {
    NAO_SEI("Não sei", 0),
    TENHO_DUVIDA("Tenho dúvida", 1),
    SEI_RESPONDER("Sei responder", 7)
}

enum class MasteryStatus(val label: String) {
    REFORCO("Precisa de Reforço"),
    EM_ANDAMENTO("Em Consolidação"),
    DOMINADO("Assunto Dominado")
}

enum class SimulatedExamMode(val label: String) {
    DISCIPLINA("Por Disciplina"),
    ASSUNTO("Por Assunto"),
    MISTO("Simulado Misto"),
    COMPLETO("Simulado Completo PAES")
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Candidato CFO",
    val dailyGoalMinutes: Long = 30L,
    val weeklyGoalDays: Long = 6L,
    val workSchedule: String = WorkSchedule.COMERCIAL.name,
    val fatigueLevel: String = FatigueLevel.DISPOSTO.name,
    val sleepHours: Long = 7L,
    val xp: Long = 0L,
    val level: Long = 1L,
    val streakDays: Long = 0L,
    val lastStudyDay: String = "",
    val totalStudyMinutes: Long = 0L,
    val totalQuestionsAnswered: Long = 0L,
    val totalQuestionsCorrect: Long = 0L,
    val soundEnabled: Boolean = true,
    val soundVolume: Double = 0.7,
    val diagnosticCompleted: Boolean = false,
    val focusSubjects: List<String> = listOf("portugues", "matematica"),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun workScheduleEnum(): WorkSchedule =
        enumValues<WorkSchedule>().firstOrNull { it.name == workSchedule } ?: WorkSchedule.COMERCIAL

    fun fatigueEnum(): FatigueLevel =
        enumValues<FatigueLevel>().firstOrNull { it.name == fatigueLevel } ?: FatigueLevel.DISPOSTO

    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "displayName" to displayName.take(80).ifBlank { "Candidato CFO" },
        "dailyGoalMinutes" to dailyGoalMinutes.coerceIn(5L, 360L),
        "weeklyGoalDays" to weeklyGoalDays.coerceIn(1L, 7L),
        "workSchedule" to workScheduleEnum().name,
        "fatigueLevel" to fatigueEnum().name,
        "sleepHours" to sleepHours.coerceIn(0L, 16L),
        "xp" to xp.coerceAtLeast(0L),
        "level" to level.coerceAtLeast(1L),
        "streakDays" to streakDays.coerceAtLeast(0L),
        "lastStudyDay" to lastStudyDay.take(20),
        "totalStudyMinutes" to totalStudyMinutes.coerceAtLeast(0L),
        "totalQuestionsAnswered" to totalQuestionsAnswered.coerceAtLeast(0L),
        "totalQuestionsCorrect" to totalQuestionsCorrect.coerceAtLeast(0L),
        "soundEnabled" to soundEnabled,
        "soundVolume" to soundVolume.coerceIn(0.0, 1.0),
        "diagnosticCompleted" to diagnosticCompleted,
        "focusSubjects" to focusSubjects.take(15),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class LessonProgressRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val lessonId: String = "",
    val subjectId: String = "",
    val completed: Boolean = false,
    val currentStage: Long = 0L,
    val finalScorePercent: Long = 0L,
    val minutesSpent: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ErrorNotebookEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val questionId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val topicName: String = "",
    val statement: String = "",
    val options: List<String> = emptyList(),
    val chosenIndex: Long = 0L,
    val correctIndex: Long = 0L,
    val explanation: String = "",
    val sourceLabel: String = "",
    val retryCount: Long = 0L,
    val mastered: Boolean = false,
    val recordedAtMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SpacedReviewItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val topicId: String = "",
    val topicTitle: String = "",
    val itemType: String = "TOPICO",
    val referenceId: String = "",
    val stageIndex: Long = 0L,
    val consecutiveHits: Long = 0L,
    val errorCount: Long = 0L,
    val nextReviewAtMillis: Long = 0L,
    val lastReviewedAtMillis: Long = 0L,
    val masteryStatus: String = MasteryStatus.EM_ANDAMENTO.name,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun statusEnum(): MasteryStatus =
        enumValues<MasteryStatus>().firstOrNull { it.name == masteryStatus } ?: MasteryStatus.EM_ANDAMENTO
}

@Keep
@IgnoreExtraProperties
data class FlashcardProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val topicTitle: String = "",
    val front: String = "",
    val back: String = "",
    val memoryTip: String = "",
    val confidence: String = FlashcardConfidence.TENHO_DUVIDA.name,
    val nextReviewAtMillis: Long = 0L,
    val reviewCount: Long = 0L,
    val customCreated: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun confidenceEnum(): FlashcardConfidence =
        enumValues<FlashcardConfidence>().firstOrNull { it.name == confidence }
            ?: FlashcardConfidence.TENHO_DUVIDA
}

@Keep
@IgnoreExtraProperties
data class SimulatedExamRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val mode: String = SimulatedExamMode.MISTO.name,
    val subjectId: String = "",
    val totalQuestions: Long = 0L,
    val correctCount: Long = 0L,
    val scorePercent: Long = 0L,
    val durationSeconds: Long = 0L,
    val completedAtMillis: Long = 0L,
    val breakdownSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class EssayRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val themeTitle: String = "",
    val draftText: String = "",
    val rewrittenText: String = "",
    val grammarErrors: List<String> = emptyList(),
    val cohesionFeedback: String = "",
    val improvementSuggestions: List<String> = emptyList(),
    val formativeScore: Long = 0L,
    val aiSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class DiagnosticSubjectResult(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val totalQuestions: Long = 0L,
    val correctCount: Long = 0L,
    val accuracyPercent: Long = 0L,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val unknownTopics: List<String> = emptyList(),
    val needsReview: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuestionAttemptLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val topicId: String = "",
    val questionId: String = "",
    val mode: String = StudyMode.AULA.name,
    val correct: Boolean = false,
    val dayKey: String = "",
    val minutesSpent: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class UnlockedAchievement(
    @DocumentId val id: String = "",
    val userId: String = "",
    val badgeId: String = "",
    val title: String = "",
    val description: String = "",
    val xpBonus: Long = 0L,
    val unlockedAtMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)
