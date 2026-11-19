package com.example.data.repository

import com.example.model.ConstancyTier
import com.example.model.FatigueLevel
import com.example.model.FlashcardConfidence
import com.example.model.MasteryStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class StudyRecommendation(
    val recommendedTier: ConstancyTier,
    val sleepProtectionNotice: String?,
    val headline: String,
    val details: String
)

object SpacedRepetitionEngine {
    // Intervalos solicitados: mesmo dia (0), 1 dia, 2 dias, 7 dias, 15 dias, 30 dias
    val intervalDays = listOf(0, 1, 2, 7, 15, 30)

    private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    private const val MILLIS_SAME_DAY_RETRY = 4L * 60L * 60L * 1000L // 4h no mesmo dia

    fun nextStageAndTime(
        currentStage: Int,
        consecutiveHits: Int,
        isCorrect: Boolean,
        nowMillis: Long = System.currentTimeMillis()
    ): Triple<Int, Long, MasteryStatus> {
        return if (!isCorrect) {
            // Questão errada retorna mais cedo (mesmo dia)
            val newStage = 0
            val nextMillis = nowMillis + MILLIS_SAME_DAY_RETRY
            Triple(newStage, nextMillis, MasteryStatus.REFORCO)
        } else {
            val nextStage = (currentStage + 1).coerceAtMost(intervalDays.lastIndex)
            val days = intervalDays[nextStage]
            val nextMillis = if (days == 0) {
                nowMillis + MILLIS_SAME_DAY_RETRY
            } else {
                nowMillis + days * MILLIS_PER_DAY
            }
            val newHits = consecutiveHits + 1
            val status = when {
                nextStage >= 4 || newHits >= 4 -> MasteryStatus.DOMINADO
                else -> MasteryStatus.EM_ANDAMENTO
            }
            Triple(nextStage, nextMillis, status)
        }
    }

    fun nextFlashcardReviewMillis(
        confidence: FlashcardConfidence,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        return when (confidence) {
            FlashcardConfidence.NAO_SEI -> nowMillis + 2L * 60L * 60L * 1000L // volta no mesmo dia
            FlashcardConfidence.TENHO_DUVIDA -> nowMillis + 1L * MILLIS_PER_DAY
            FlashcardConfidence.SEI_RESPONDER -> nowMillis + 7L * MILLIS_PER_DAY
        }
    }

    fun levelFromXp(xp: Long): Long {
        // Progressão clara: 120 XP por nível com leve curva crescente
        var remaining = xp.coerceAtLeast(0L)
        var lvl = 1L
        var needed = 120L
        while (remaining >= needed) {
            remaining -= needed
            lvl++
            needed += 40L
        }
        return lvl
    }

    fun xpProgressInCurrentLevel(xp: Long): Pair<Long, Long> {
        var remaining = xp.coerceAtLeast(0L)
        var needed = 120L
        while (remaining >= needed) {
            remaining -= needed
            needed += 40L
        }
        return remaining to needed
    }

    fun dayKey(nowMillis: Long = System.currentTimeMillis()): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return fmt.format(Date(nowMillis))
    }

    fun updatedStreak(lastDay: String, currentStreak: Long, nowMillis: Long = System.currentTimeMillis()): Pair<Long, String> {
        val today = dayKey(nowMillis)
        if (lastDay == today) return currentStreak.coerceAtLeast(1L) to today

        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val yesterday = dayKey(cal.timeInMillis)
        val newStreak = if (lastDay == yesterday) (currentStreak + 1L) else 1L
        return newStreak to today
    }

    fun recommendConstancy(
        fatigue: FatigueLevel,
        sleepHours: Int,
        dailyGoalMinutes: Int,
        pendingReviewsCount: Int
    ): StudyRecommendation {
        val sleepLow = sleepHours in 0..5
        return when {
            fatigue == FatigueLevel.EXAUSTO || (fatigue == FatigueLevel.CANSADO && sleepLow) -> StudyRecommendation(
                recommendedTier = ConstancyTier.LEVE,
                sleepProtectionNotice = "Sinal de fadiga detectado ($sleepHours h de sono). Não sacrifique seu sono: faça apenas uma revisão leve de 5 min ou descanse hoje para consolidar a memória.",
                headline = "Prioridade: Recuperação + Constância Leve",
                details = "Seu cérebro consolida conteúdos durante o sono. Sugerimos 3 flashcards rápidos ou descanso imediato."
            )
            fatigue == FatigueLevel.CANSADO || sleepLow -> StudyRecommendation(
                recommendedTier = ConstancyTier.LEVE,
                sleepProtectionNotice = "Você está cansado. Mantenha a chama acesa sem sobrecarga com uma missão leve de 5 a 10 minutos.",
                headline = "Modo Leve Recomendado (5–10 min)",
                details = "3 a 5 questões fáceis e revisão rápida para manter sua sequência sem desgastar sua energia."
            )
            fatigue == FatigueLevel.MODERADO || dailyGoalMinutes < 40 -> StudyRecommendation(
                recommendedTier = ConstancyTier.MODERADO,
                sleepProtectionNotice = null,
                headline = "Modo Moderado Recomendado (15–25 min)",
                details = if (pendingReviewsCount > 0) {
                    "Você tem $pendingReviewsCount revisões pendentes. Combine mini-aula + revisão de erros."
                } else {
                    "Mini-aula objetiva + 5 a 10 questões de fixação para avançar com equilíbrio."
                }
            )
            else -> StudyRecommendation(
                recommendedTier = ConstancyTier.COMPLETO,
                sleepProtectionNotice = null,
                headline = "Modo Completo Liberado (45–90 min)",
                details = "Energia alta! Ideal para aula aprofundada, questões nos 3 níveis e simulado focado."
            )
        }
    }
}
