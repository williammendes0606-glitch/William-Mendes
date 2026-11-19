package com.example.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.audio.SoundEffectsManager
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.repository.FirestoreProgressRepository
import com.example.firebase.authStateFlow
import com.example.firebase.signOut
import com.example.model.QuestionItem
import com.example.model.StudyMode
import com.example.model.UiState
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ConstancyModeScreen
import com.example.ui.screens.DiagnosticScreen
import com.example.ui.screens.ErrorNotebookScreen
import com.example.ui.screens.EssayScreen
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuickModeScreen
import com.example.ui.screens.SettingsBackupApkScreen
import com.example.ui.screens.SimuladosScreen
import com.example.ui.screens.SpacedReviewScreen
import com.example.ui.screens.StatisticsAndBadgesScreen
import com.example.ui.screens.StudyPlanScreen
import com.example.ui.screens.SubjectsAndLessonsScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.AiViewModel
import com.example.viewmodel.StudyViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object Routes {
    const val HOME = "home"
    const val AULAS = "aulas"
    const val SIMULADOS = "simulados"
    const val TUTOR_IA = "tutor_ia"
    const val ESTATISTICAS = "estatisticas"

    const val MODO_RAPIDO = "modo_rapido"
    const val MODO_CONSTANCIA = "modo_constancia"
    const val REVISOES = "revisoes"
    const val DIAGNOSTICO = "diagnostico"
    const val CADERNO_ERROS = "caderno_erros"
    const val FLASHCARDS = "flashcards"
    const val REDACAO = "redacao"
    const val PLANO_ESTUDOS = "plano_estudos"
    const val CONFIGURACOES = "configuracoes"
}

private data class TopTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

private val TopTabs = listOf(
    TopTab(Routes.HOME, "Início", Icons.Default.Home, "nav_home"),
    TopTab(Routes.AULAS, "Aulas", Icons.AutoMirrored.Filled.MenuBook, "nav_aulas"),
    TopTab(Routes.SIMULADOS, "Simulados", Icons.Default.Quiz, "nav_simulados"),
    TopTab(Routes.TUTOR_IA, "Tutor IA", Icons.Default.AutoAwesome, "nav_tutor"),
    TopTab(Routes.ESTATISTICAS, "Estatísticas", Icons.Default.Analytics, "nav_stats")
)

@Composable
fun AppNavigation(
    themeMode: AppThemeMode,
    onSelectThemeMode: (AppThemeMode) -> Unit
) {
    val authUser by Firebase.auth.authStateFlow().collectAsStateWithLifecycle(
        initialValue = Firebase.auth.currentUser
    )

    val user = authUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
        return
    }

    key(user.uid) {
        AuthenticatedAppShell(
            uid = user.uid,
            displayName = user.displayName ?: "Candidato CFO",
            themeMode = themeMode,
            onSelectThemeMode = onSelectThemeMode
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun AuthenticatedAppShell(
    uid: String,
    displayName: String,
    themeMode: AppThemeMode,
    onSelectThemeMode: (AppThemeMode) -> Unit
) {
    val cfo = CfoTheme.colors
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val studyViewModel: StudyViewModel = viewModel(
        key = "study_$uid",
        factory = remember(uid) {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudyViewModel(
                        repository = FirestoreProgressRepository(appContext),
                        soundManager = SoundEffectsManager(),
                        currentUserId = uid,
                        initialDisplayName = displayName
                    ) as T
                }
            }
        }
    )

    val aiViewModel: AiViewModel = viewModel(key = "ai_$uid")
    val dashboardState by studyViewModel.dashboardState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(studyViewModel) {
        studyViewModel.events.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = backStackEntry?.destination

    var pendingLessonId by remember { mutableStateOf<String?>(null) }
    var pendingTutorPrompt by remember { mutableStateOf<String?>(null) }

    fun navigateTopTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun askTutorAboutQuestion(q: QuestionItem) {
        pendingTutorPrompt =
            "Estou estudando ${q.subjectName} (${q.topicName}) para o CFO PMMA. " +
                "Explique passo a passo e me dê um macete para esta questão:\n\"${q.statement}\""
        navigateTopTab(Routes.TUTOR_IA)
    }

    val isTopLevelRoute = TopTabs.any { tab ->
        currentDest?.hierarchy?.any { it.route == tab.route } == true
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!useRail && isTopLevelRoute) {
                    NavigationBar(containerColor = cfo.cardSurface) {
                        TopTabs.forEach { tab ->
                            val selected = currentDest?.hierarchy?.any { it.route == tab.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopTab(tab.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label, maxLines = 1) },
                                modifier = Modifier.testTag(tab.testTag),
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = cfo.titleGreen,
                                    selectedTextColor = cfo.titleGreen,
                                    indicatorColor = cfo.softGreenSurface,
                                    unselectedIconColor = cfo.mutedText,
                                    unselectedTextColor = cfo.mutedText
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (useRail && isTopLevelRoute) {
                    NavigationRail(
                        containerColor = cfo.cardSurface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .statusBarsPadding()
                    ) {
                        TopTabs.forEach { tab ->
                            val selected = currentDest?.hierarchy?.any { it.route == tab.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopTab(tab.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label, maxLines = 1) },
                                modifier = Modifier.testTag(tab.testTag),
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = cfo.titleGreen,
                                    selectedTextColor = cfo.titleGreen,
                                    indicatorColor = cfo.softGreenSurface,
                                    unselectedIconColor = cfo.mutedText,
                                    unselectedTextColor = cfo.mutedText
                                )
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    when (val state = dashboardState) {
                        is UiState.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = cfo.titleGreen, modifier = Modifier.size(40.dp))
                                Text(
                                    text = "Carregando sua missão CFO MA...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = cfo.titleGreen,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }

                        is UiState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Não foi possível carregar seu progresso",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = cfo.titleGreen
                                )
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.bodyText,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Button(
                                    onClick = { signOut(credentialManager, scope) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Text("Trocar Conta Google")
                                }
                            }
                        }

                        is UiState.Success -> {
                            val data = state.data
                            NavHost(
                                navController = navController,
                                startDestination = Routes.HOME,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable(Routes.HOME) {
                                    HomeScreen(
                                        data = data,
                                        onOpenModoAula = {
                                            pendingLessonId = null
                                            navigateTopTab(Routes.AULAS)
                                        },
                                        onOpenModoRapido = { navController.navigate(Routes.MODO_RAPIDO) },
                                        onOpenModoConstancia = { navController.navigate(Routes.MODO_CONSTANCIA) },
                                        onOpenRevisoes = { navController.navigate(Routes.REVISOES) },
                                        onOpenDiagnostico = { navController.navigate(Routes.DIAGNOSTICO) },
                                        onOpenSimulados = { navigateTopTab(Routes.SIMULADOS) },
                                        onOpenCadernoErros = { navController.navigate(Routes.CADERNO_ERROS) },
                                        onOpenFlashcards = { navController.navigate(Routes.FLASHCARDS) },
                                        onOpenEstatisticas = { navigateTopTab(Routes.ESTATISTICAS) },
                                        onOpenTutorIa = { navigateTopTab(Routes.TUTOR_IA) },
                                        onOpenRedacao = { navController.navigate(Routes.REDACAO) },
                                        onOpenPlanoEstudos = { navController.navigate(Routes.PLANO_ESTUDOS) },
                                        onOpenConfiguracoes = { navController.navigate(Routes.CONFIGURACOES) }
                                    )
                                }

                                composable(Routes.AULAS) {
                                    SubjectsAndLessonsScreen(
                                        data = data,
                                        initialLessonId = pendingLessonId,
                                        onBack = { navigateTopTab(Routes.HOME) },
                                        onAnswerQuestion = { q, chosen, mode ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode)
                                        },
                                        onCompleteLesson = { lesson, stage, completed, scorePct, mins ->
                                            val topicTitle = CurriculumCatalog.subjectById(lesson.subjectId)
                                                ?.topics?.firstOrNull { it.id == lesson.topicId }?.title
                                                ?: lesson.title
                                            studyViewModel.completeLesson(
                                                lessonId = lesson.id,
                                                subjectId = lesson.subjectId,
                                                topicId = lesson.topicId,
                                                topicTitle = topicTitle,
                                                stage = stage,
                                                completed = completed,
                                                finalScorePercent = scorePct,
                                                minutesSpent = mins
                                            )
                                        },
                                        onAskTutorAboutQuestion = { askTutorAboutQuestion(it) }
                                    )
                                }

                                composable(Routes.SIMULADOS) {
                                    SimuladosScreen(
                                        data = data,
                                        onBack = { navigateTopTab(Routes.HOME) },
                                        onAnswerQuestion = { q, chosen, mode, mins ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode, mins)
                                        },
                                        onSaveSimulatedExam = { title, mode, subjectId, total, correct, durationSec, summary ->
                                            studyViewModel.saveSimulatedExam(
                                                title = title,
                                                mode = mode,
                                                subjectId = subjectId,
                                                totalQuestions = total,
                                                correctCount = correct,
                                                durationSeconds = durationSec,
                                                breakdownSummary = summary
                                            )
                                        },
                                        onAskTutor = { askTutorAboutQuestion(it) }
                                    )
                                }

                                composable(Routes.TUTOR_IA) {
                                    AiTutorScreen(
                                        aiViewModel = aiViewModel,
                                        initialPrompt = pendingTutorPrompt,
                                        onConsumeInitialPrompt = { pendingTutorPrompt = null },
                                        onBack = { navigateTopTab(Routes.HOME) },
                                        onAnswerGeneratedQuestion = { q, chosen, mode, mins ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode, mins)
                                        }
                                    )
                                }

                                composable(Routes.ESTATISTICAS) {
                                    StatisticsAndBadgesScreen(
                                        data = data,
                                        onBack = { navigateTopTab(Routes.HOME) }
                                    )
                                }

                                composable(Routes.MODO_RAPIDO) {
                                    QuickModeScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onAnswerQuestion = { q, chosen, mode, mins ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode, mins)
                                        },
                                        onRateFlashcard = { card, conf ->
                                            studyViewModel.rateFlashcard(card, conf)
                                        },
                                        onMissionFinished = { label ->
                                            studyViewModel.notifyMissionCompleted(label)
                                        },
                                        onAskTutor = { askTutorAboutQuestion(it) }
                                    )
                                }

                                composable(Routes.MODO_CONSTANCIA) {
                                    ConstancyModeScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onAnswerQuestion = { q, chosen, mode, mins ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode, mins)
                                        },
                                        onMissionFinished = { label ->
                                            studyViewModel.notifyMissionCompleted(label)
                                        },
                                        onOpenLesson = { lessonId ->
                                            pendingLessonId = lessonId
                                            navigateTopTab(Routes.AULAS)
                                        },
                                        onAskTutor = { askTutorAboutQuestion(it) }
                                    )
                                }

                                composable(Routes.REVISOES) {
                                    SpacedReviewScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onAnswerQuestion = { q, chosen, mode, mins ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode, mins)
                                        },
                                        onOpenLesson = { lessonId ->
                                            pendingLessonId = lessonId
                                            navigateTopTab(Routes.AULAS)
                                        },
                                        onAskTutor = { askTutorAboutQuestion(it) }
                                    )
                                }

                                composable(Routes.DIAGNOSTICO) {
                                    DiagnosticScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onAnswerQuestion = { q, chosen, mode, mins ->
                                            studyViewModel.recordQuestionAnswer(q, chosen, mode, mins)
                                        },
                                        onSaveDiagnostic = { result ->
                                            studyViewModel.saveDiagnostic(result)
                                        }
                                    )
                                }

                                composable(Routes.CADERNO_ERROS) {
                                    ErrorNotebookScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onRetryAnswer = { q, chosen ->
                                            studyViewModel.recordQuestionAnswer(
                                                q,
                                                chosen,
                                                StudyMode.CADERNO_ERROS,
                                                2L
                                            )
                                        },
                                        onAskTutor = { askTutorAboutQuestion(it) }
                                    )
                                }

                                composable(Routes.FLASHCARDS) {
                                    FlashcardsScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onRateFlashcard = { card, conf ->
                                            studyViewModel.rateFlashcard(card, conf)
                                        },
                                        onCreateFlashcard = { subjId, topicTitle, front, back, tip ->
                                            studyViewModel.createFlashcard(subjId, topicTitle, front, back, tip)
                                        }
                                    )
                                }

                                composable(Routes.REDACAO) {
                                    EssayScreen(
                                        data = data,
                                        aiViewModel = aiViewModel,
                                        onBack = { navController.popBackStack() },
                                        onSaveEssay = { existingId, title, draft, rewrite, errors, cohesion, sugs, score, summary ->
                                            studyViewModel.saveEssay(
                                                existingId = existingId,
                                                themeTitle = title,
                                                draftText = draft,
                                                rewrittenText = rewrite,
                                                grammarErrors = errors,
                                                cohesionFeedback = cohesion,
                                                improvementSuggestions = sugs,
                                                formativeScore = score,
                                                aiSummary = summary
                                            )
                                        }
                                    )
                                }

                                composable(Routes.PLANO_ESTUDOS) {
                                    StudyPlanScreen(
                                        data = data,
                                        onBack = { navController.popBackStack() },
                                        onSavePlan = { name, daily, weekly, ws, fatigue, sleep, sEnabled, sVol, focus ->
                                            studyViewModel.updateSettings(
                                                displayName = name,
                                                dailyGoalMinutes = daily,
                                                weeklyGoalDays = weekly,
                                                workSchedule = ws,
                                                fatigueLevel = fatigue,
                                                sleepHours = sleep,
                                                soundEnabled = sEnabled,
                                                soundVolume = sVol,
                                                focusSubjects = focus
                                            )
                                        }
                                    )
                                }

                                composable(Routes.CONFIGURACOES) {
                                    SettingsBackupApkScreen(
                                        data = data,
                                        themeMode = themeMode,
                                        onSelectThemeMode = onSelectThemeMode,
                                        onBack = { navController.popBackStack() },
                                        onSaveSettings = { name, daily, weekly, ws, fatigue, sleep, sEnabled, sVol, focus ->
                                            studyViewModel.updateSettings(
                                                displayName = name,
                                                dailyGoalMinutes = daily,
                                                weeklyGoalDays = weekly,
                                                workSchedule = ws,
                                                fatigueLevel = fatigue,
                                                sleepHours = sleep,
                                                soundEnabled = sEnabled,
                                                soundVolume = sVol,
                                                focusSubjects = focus
                                            )
                                        },
                                        onTestSound = { studyViewModel.testSound() },
                                        onPrepareBackupJson = { cb -> studyViewModel.prepareBackupExport(cb) },
                                        onImportBackupJson = { json -> studyViewModel.importBackupJson(json) },
                                        onSignOut = { signOut(credentialManager, scope) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
