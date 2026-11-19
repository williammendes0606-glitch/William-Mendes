package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.repository.SpacedRepetitionEngine
import com.example.model.ConstancyTier
import com.example.model.FatigueLevel
import com.example.model.FlashcardConfidence
import com.example.model.MasteryStatus
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.DayBackground
import com.example.ui.theme.DayBodyText
import com.example.ui.theme.DayCardSurface
import com.example.ui.theme.DayCorrectContainer
import com.example.ui.theme.DayCorrectGreen
import com.example.ui.theme.DaySageSurface
import com.example.ui.theme.DaySlateBlue
import com.example.ui.theme.DayWrongContainer
import com.example.ui.theme.DayWrongTerracotta
import com.example.ui.theme.NightBackground
import com.example.ui.theme.NightCorrectContainer
import com.example.ui.theme.NightCorrectGreen
import com.example.ui.theme.NightMutedText
import com.example.ui.theme.NightOnSurface
import com.example.ui.theme.NightSageSurface
import com.example.ui.theme.NightSlateBlueSurface
import com.example.ui.theme.NightSurface
import com.example.ui.theme.NightWrongContainer
import com.example.ui.theme.NightWrongTerracotta
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SandBeige
import com.example.ui.theme.SoftGold
import com.example.ui.theme.TextOliveGreen
import com.example.ui.theme.WarmWhite
import com.example.ui.theme.resolveIsDarkTheme
import kotlin.math.max
import kotlin.math.min
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = fg.luminance().toDouble()
        val l2 = bg.luminance().toDouble()
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun `spaced repetition cycles follow same-day 1 2 7 15 30 days and reset on error`() {
        assertEquals(listOf(0, 1, 2, 7, 15, 30), SpacedRepetitionEngine.intervalDays)

        val now = 1_700_000_000_000L
        val (stage1, next1, status1) = SpacedRepetitionEngine.nextStageAndTime(
            currentStage = 0,
            consecutiveHits = 0,
            isCorrect = true,
            nowMillis = now
        )
        assertEquals(1, stage1)
        assertEquals(now + 24L * 60L * 60L * 1000L, next1)
        assertEquals(MasteryStatus.EM_ANDAMENTO, status1)

        val (stageErr, nextErr, statusErr) = SpacedRepetitionEngine.nextStageAndTime(
            currentStage = 3,
            consecutiveHits = 3,
            isCorrect = false,
            nowMillis = now
        )
        assertEquals(0, stageErr)
        assertEquals(MasteryStatus.REFORCO, statusErr)
        assertTrue(nextErr < now + 24L * 60L * 60L * 1000L)
    }

    @Test
    fun `flashcard ratings schedule shorter intervals for unknown cards`() {
        val now = 1_700_000_000_000L
        val unknown = SpacedRepetitionEngine.nextFlashcardReviewMillis(FlashcardConfidence.NAO_SEI, now)
        val doubt = SpacedRepetitionEngine.nextFlashcardReviewMillis(FlashcardConfidence.TENHO_DUVIDA, now)
        val known = SpacedRepetitionEngine.nextFlashcardReviewMillis(FlashcardConfidence.SEI_RESPONDER, now)
        assertTrue(unknown < doubt)
        assertTrue(doubt < known)
    }

    @Test
    fun `constancy recommendation protects sleep when exhausted`() {
        val rec = SpacedRepetitionEngine.recommendConstancy(
            fatigue = FatigueLevel.EXAUSTO,
            sleepHours = 4,
            dailyGoalMinutes = 60,
            pendingReviewsCount = 5
        )
        assertEquals(ConstancyTier.LEVE, rec.recommendedTier)
        assertNotNull(rec.sleepProtectionNotice)
    }

    @Test
    fun `curriculum catalog has valid lessons, questions, and origins`() {
        assertTrue(CurriculumCatalog.allSubjects.size >= 8)
        assertTrue(CurriculumCatalog.allLessons.isNotEmpty())
        assertTrue(CurriculumCatalog.allQuestions.size >= 20)

        CurriculumCatalog.allQuestions.forEach { q ->
            assertTrue("Question ${q.id} options", q.options.size == 4)
            assertTrue("Question ${q.id} correctIndex", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} explanation", q.generalExplanation.isNotBlank())
        }

        CurriculumCatalog.allLessons.forEach { l ->
            l.practiceQuestionIds.forEach { qid ->
                assertNotNull("Missing practice question $qid in lesson ${l.id}", CurriculumCatalog.questionById(qid))
            }
            l.finalExamQuestionIds.forEach { qid ->
                assertNotNull("Missing final question $qid in lesson ${l.id}", CurriculumCatalog.questionById(qid))
            }
        }
    }

    @Test
    fun `theme mode resolves day night and automatic modes accurately`() {
        assertFalse(resolveIsDarkTheme(AppThemeMode.AUTO, systemInDarkTheme = false))
        assertTrue(resolveIsDarkTheme(AppThemeMode.AUTO, systemInDarkTheme = true))

        assertFalse(resolveIsDarkTheme(AppThemeMode.DAY, systemInDarkTheme = false))
        assertFalse(resolveIsDarkTheme(AppThemeMode.DAY, systemInDarkTheme = true))

        assertTrue(resolveIsDarkTheme(AppThemeMode.NIGHT, systemInDarkTheme = false))
        assertTrue(resolveIsDarkTheme(AppThemeMode.NIGHT, systemInDarkTheme = true))

        assertEquals(AppThemeMode.AUTO, AppThemeMode.fromStored(null))
        assertEquals(AppThemeMode.AUTO, AppThemeMode.fromStored("UNKNOWN"))
        assertEquals(AppThemeMode.DAY, AppThemeMode.fromStored("DAY"))
        assertEquals(AppThemeMode.NIGHT, AppThemeMode.fromStored("NIGHT"))
    }

    @Test
    fun `new visual identity palette matches specification and maintains accessible contrast`() {
        assertEquals(Color(0xFF718675), OliveGreen)
        assertEquals(Color(0xFFA8B8A5), SageGreen)
        assertEquals(Color(0xFFE9E2D2), SandBeige)
        assertEquals(Color(0xFFD1B477), SoftGold)
        assertEquals(Color(0xFFF7F6F0), WarmWhite)
        assertEquals(Color(0xFF46564B), TextOliveGreen)

        // Modo Dia: contraste >= 4.5:1 em fundos, cartões e botões
        assertTrue(contrastRatio(DayBodyText, DayBackground) >= 4.5)
        assertTrue(contrastRatio(DayBodyText, DayCardSurface) >= 4.5)
        assertTrue(contrastRatio(DayBodyText, DaySageSurface) >= 4.5)
        assertTrue(contrastRatio(DayBodyText, DaySlateBlue) >= 4.5)
        assertTrue(contrastRatio(DayBodyText, SandBeige) >= 4.5)
        assertTrue(contrastRatio(TextOliveGreen, DayCardSurface) >= 4.5)
        assertTrue(contrastRatio(TextOliveGreen, DaySageSurface) >= 4.5)
        assertTrue(contrastRatio(WarmWhite, TextOliveGreen) >= 4.5)
        assertTrue(contrastRatio(DayCorrectGreen, DayCorrectContainer) >= 4.5)
        assertTrue(contrastRatio(DayWrongTerracotta, DayWrongContainer) >= 4.5)

        // Modo Noite: sem preto puro e contraste >= 4.5:1
        assertFalse(NightBackground == Color.Black)
        assertTrue(contrastRatio(NightOnSurface, NightBackground) >= 4.5)
        assertTrue(contrastRatio(NightOnSurface, NightSurface) >= 4.5)
        assertTrue(contrastRatio(NightOnSurface, NightSageSurface) >= 4.5)
        assertTrue(contrastRatio(NightOnSurface, NightSlateBlueSurface) >= 4.5)
        assertTrue(contrastRatio(NightMutedText, NightSurface) >= 4.5)
        assertTrue(contrastRatio(SageGreen, NightSurface) >= 4.5)
        assertTrue(contrastRatio(SageGreen, NightSageSurface) >= 4.5)
        assertTrue(contrastRatio(NightCorrectGreen, NightCorrectContainer) >= 4.5)
        assertTrue(contrastRatio(NightWrongTerracotta, NightWrongContainer) >= 4.5)
    }
}
