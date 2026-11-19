package com.example

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import com.example.data.curriculum.CurriculumCatalog
import com.example.model.UserProfile
import com.example.ui.components.QuestionInteractiveCard
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsBackupApkScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CfoTheme
import com.example.ui.theme.DayBackground
import com.example.ui.theme.DayCardSurface
import com.example.ui.theme.MissaoCfoTheme
import com.example.ui.theme.NightBackground
import com.example.ui.theme.NightSurface
import com.example.ui.theme.ThemePreferencesRepository
import com.example.ui.theme.resolveIsDarkTheme
import com.example.viewmodel.StudyDashboardData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun sampleDashboardData(): StudyDashboardData = StudyDashboardData(
        profile = UserProfile(displayName = "Cadete Silva", xp = 120L, level = 2L, streakDays = 4L),
        lessonProgress = emptyList(),
        errorNotebook = emptyList(),
        reviewItems = emptyList(),
        flashcards = emptyList(),
        simulatedExams = emptyList(),
        essays = emptyList(),
        diagnostics = emptyList(),
        questionLogs = emptyList(),
        achievements = emptyList()
    )

    @Test
    fun `app name and firestore database id resources are configured`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Missão CFO MA", appName)

        val dbId = context.getString(R.string.firestore_database_id)
        assertTrue(dbId.isNotBlank())
    }

    @Test
    fun `question interactive card shows feedback and tutor action after submit`() {
        val sampleQuestion = CurriculumCatalog.allQuestions.first()
        var selected by mutableStateOf<Int?>(null)
        var submitted by mutableStateOf(false)

        composeRule.setContent {
            MissaoCfoTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    QuestionInteractiveCard(
                        question = sampleQuestion,
                        selectedIndex = selected,
                        isSubmitted = submitted,
                        onSelectOption = { selected = it },
                        onSubmit = { submitted = true },
                        onAskTutor = {}
                    )
                }
            }
        }

        composeRule.onNodeWithTag("question_option_${sampleQuestion.correctIndex}")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("submit_answer_button")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Resposta correta! Excelente.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("ask_tutor_question_button")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun `theme preference persists across repository instances simulating close and reopen`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repoSession1 = ThemePreferencesRepository(context)

        repoSession1.setThemeMode(AppThemeMode.NIGHT)
        val repoSession2 = ThemePreferencesRepository(context)
        assertEquals(AppThemeMode.NIGHT, repoSession2.themeModeFlow.first())

        repoSession2.setThemeMode(AppThemeMode.DAY)
        val repoSession3 = ThemePreferencesRepository(context)
        assertEquals(AppThemeMode.DAY, repoSession3.themeModeFlow.first())

        repoSession3.setThemeMode(AppThemeMode.AUTO)
        val repoSession4 = ThemePreferencesRepository(context)
        assertEquals(AppThemeMode.AUTO, repoSession4.themeModeFlow.first())
    }

    @Test
    fun `manual theme switch in settings updates theme tokens across day night and auto modes`() {
        var currentMode by mutableStateOf(AppThemeMode.DAY)
        var systemDark by mutableStateOf(false)
        var observedIsDark = false
        var observedBg = Color.Unspecified
        var observedCard = Color.Unspecified

        composeRule.setContent {
            MissaoCfoTheme(
                themeMode = currentMode,
                darkTheme = resolveIsDarkTheme(currentMode, systemDark)
            ) {
                observedIsDark = CfoTheme.colors.isDark
                observedBg = MaterialTheme.colorScheme.background
                observedCard = CfoTheme.colors.cardSurface

                SettingsBackupApkScreen(
                    data = sampleDashboardData(),
                    themeMode = currentMode,
                    onSelectThemeMode = { currentMode = it },
                    onBack = {},
                    onSaveSettings = { _, _, _, _, _, _, _, _, _ -> },
                    onTestSound = {},
                    onPrepareBackupJson = {},
                    onImportBackupJson = {},
                    onSignOut = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("appearance_settings_card").assertIsDisplayed()
        assertFalse(observedIsDark)
        assertEquals(DayBackground, observedBg)
        assertEquals(DayCardSurface, observedCard)

        // Troca manual para Modo Noite
        composeRule.onNodeWithTag("theme_option_NIGHT").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(AppThemeMode.NIGHT, currentMode)
        assertTrue(observedIsDark)
        assertEquals(NightBackground, observedBg)
        assertEquals(NightSurface, observedCard)

        // Troca manual para Modo Dia
        composeRule.onNodeWithTag("theme_option_DAY").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(AppThemeMode.DAY, currentMode)
        assertFalse(observedIsDark)
        assertEquals(DayBackground, observedBg)

        // Troca manual para Automático e mudança de aparência do sistema
        composeRule.onNodeWithTag("theme_option_AUTO").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(AppThemeMode.AUTO, currentMode)
        assertFalse(observedIsDark)

        systemDark = true
        composeRule.waitForIdle()
        assertTrue(observedIsDark)
        assertEquals(NightBackground, observedBg)
    }

    @Test
    fun `home screen renders hero banner and primary modes in both day and night themes`() {
        var currentMode by mutableStateOf(AppThemeMode.DAY)
        var observedDark = false

        composeRule.setContent {
            MissaoCfoTheme(
                themeMode = currentMode,
                darkTheme = resolveIsDarkTheme(currentMode, systemInDarkTheme = false)
            ) {
                observedDark = CfoTheme.colors.isDark
                HomeScreen(
                    data = sampleDashboardData(),
                    onOpenModoAula = {},
                    onOpenModoRapido = {},
                    onOpenModoConstancia = {},
                    onOpenRevisoes = {},
                    onOpenDiagnostico = {},
                    onOpenSimulados = {},
                    onOpenCadernoErros = {},
                    onOpenFlashcards = {},
                    onOpenEstatisticas = {},
                    onOpenTutorIa = {},
                    onOpenRedacao = {},
                    onOpenPlanoEstudos = {},
                    onOpenConfiguracoes = {}
                )
            }
        }

        composeRule.waitForIdle()
        assertFalse(observedDark)
        composeRule.onNodeWithText("Rumo ao Oficialato PMMA").assertIsDisplayed()
        composeRule.onNodeWithTag("home_settings_button").assertIsDisplayed()

        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(2)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("home_constancy_button").assertIsDisplayed()

        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(4)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("home_modo_aula_button").assertIsDisplayed()

        // Alterna para Modo Noite e valida renderização do Hero Banner e Modo Constância
        currentMode = AppThemeMode.NIGHT
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(1)
        composeRule.waitForIdle()
        assertTrue(observedDark)
        composeRule.onNodeWithText("Rumo ao Oficialato PMMA").assertIsDisplayed()

        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(2)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("home_constancy_button").assertIsDisplayed()
    }
}
