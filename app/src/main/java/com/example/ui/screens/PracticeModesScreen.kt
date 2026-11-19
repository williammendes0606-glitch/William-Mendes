package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.repository.SpacedRepetitionEngine
import com.example.model.ConstancyTier
import com.example.model.DiagnosticSubjectResult
import com.example.model.FatigueLevel
import com.example.model.FlashcardConfidence
import com.example.model.FlashcardProgress
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.StudyMode
import com.example.model.SubjectDefinition
import com.example.ui.components.CfoTopBar
import com.example.ui.components.QuestionInteractiveCard
import com.example.ui.components.SmoothProgressBar
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.StudyDashboardData
import kotlin.math.roundToInt

@Composable
fun QuickModeScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit,
    onRateFlashcard: (FlashcardProgress, FlashcardConfidence) -> Unit,
    onMissionFinished: (String) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    val sessionQuestions = remember(data.profile.focusSubjects) {
        val errorIds = data.activeErrors.map { it.questionId }
        val errorQs = errorIds.mapNotNull { CurriculumCatalog.questionById(it) }
        val priorityQs = CurriculumCatalog.allQuestions.filter { it.subjectId in data.profile.focusSubjects }
        val others = CurriculumCatalog.allQuestions
        (errorQs + priorityQs + others).distinctBy { it.id }.take(4)
    }
    val sessionFlashcards = remember { data.flashcards.take(2) }

    var idx by remember { mutableIntStateOf(0) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var inFlashcardPhase by remember { mutableStateOf(false) }
    var fcIndex by remember { mutableIntStateOf(0) }
    var fcFlipped by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Modo Rápido (2–5 min)",
                subtitle = "Recuperação ativa compartilhada com Modo Aula",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (finished) {
                    item {
                        MissionSummaryCard(
                            title = "Missão Rápida Concluída!",
                            subtitle = "Você acertou $correctCount de ${sessionQuestions.size} questões rápidas e revisou flashcards.",
                            onDone = onBack
                        )
                    }
                } else if (!inFlashcardPhase && sessionQuestions.isNotEmpty()) {
                    val q = sessionQuestions[idx]
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pergunta rápida ${idx + 1}/${sessionQuestions.size}", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            Surface(shape = RoundedCornerShape(8.dp), color = cfo.slateBlueSurface) {
                                Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, null, tint = cfo.titleGreen, modifier = Modifier.size(14.dp))
                                    Text("2–5 min", style = MaterialTheme.typography.labelSmall, color = cfo.bodyText)
                                }
                            }
                        }
                    }
                    item {
                        SmoothProgressBar(progress = (idx + 1).toFloat() / (sessionQuestions.size + sessionFlashcards.size).toFloat())
                    }
                    item {
                        QuestionInteractiveCard(
                            question = q,
                            selectedIndex = selectedOpt,
                            isSubmitted = submitted,
                            onSelectOption = { selectedOpt = it },
                            onSubmit = {
                                val chosen = selectedOpt ?: return@QuestionInteractiveCard
                                submitted = true
                                if (chosen == q.correctIndex) correctCount++
                                onAnswerQuestion(q, chosen, StudyMode.RAPIDO, 1L)
                            },
                            onAskTutor = { onAskTutor(q) },
                            nextButtonLabel = if (idx < sessionQuestions.lastIndex) "Próxima" else "Ir para Flashcards",
                            onNext = {
                                selectedOpt = null
                                submitted = false
                                if (idx < sessionQuestions.lastIndex) {
                                    idx++
                                } else if (sessionFlashcards.isNotEmpty()) {
                                    inFlashcardPhase = true
                                } else {
                                    finished = true
                                    onMissionFinished("Missão Rápida")
                                }
                            }
                        )
                    }
                } else if (inFlashcardPhase && fcIndex < sessionFlashcards.size) {
                    val card = sessionFlashcards[fcIndex]
                    item {
                        Text(
                            text = "Flashcard de Memorização ${fcIndex + 1}/${sessionFlashcards.size}",
                            style = MaterialTheme.typography.titleSmall,
                            color = cfo.titleGreen
                        )
                    }
                    item {
                        FlashcardInteractiveTile(
                            card = card,
                            flipped = fcFlipped,
                            onToggleFlip = { fcFlipped = !fcFlipped },
                            onRate = { confidence ->
                                onRateFlashcard(card, confidence)
                                fcFlipped = false
                                if (fcIndex < sessionFlashcards.lastIndex) {
                                    fcIndex++
                                } else {
                                    finished = true
                                    onMissionFinished("Missão Rápida")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConstancyModeScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit,
    onMissionFinished: (String) -> Unit,
    onOpenLesson: (String) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    var selectedFatigue by remember(data.profile.fatigueLevel) { mutableStateOf(data.profile.fatigueEnum()) }
    var sleepHours by remember(data.profile.sleepHours) { mutableIntStateOf(data.profile.sleepHours.toInt()) }
    var activeTier by remember { mutableStateOf<ConstancyTier?>(null) }
    var restChosen by remember { mutableStateOf(false) }

    val recommendation = remember(selectedFatigue, sleepHours, data.profile.dailyGoalMinutes, data.pendingReviews.size) {
        SpacedRepetitionEngine.recommendConstancy(
            fatigue = selectedFatigue,
            sleepHours = sleepHours,
            dailyGoalMinutes = data.profile.dailyGoalMinutes.toInt(),
            pendingReviewsCount = data.pendingReviews.size
        )
    }

    if (activeTier != null) {
        BackHandler { activeTier = null }
        ConstancySessionPlayer(
            tier = activeTier!!,
            data = data,
            onBack = { activeTier = null },
            onAnswerQuestion = onAnswerQuestion,
            onMissionFinished = {
                onMissionFinished("Modo Constância (${activeTier!!.label})")
                activeTier = null
            },
            onOpenLesson = onOpenLesson,
            onAskTutor = onAskTutor
        )
        return
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Modo Constância",
                subtitle = "Estude com consistência até nos dias difíceis",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Como você está hoje?", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FatigueLevel.entries.forEach { level ->
                                    FilterChip(
                                        selected = selectedFatigue == level,
                                        onClick = {
                                            selectedFatigue = level
                                            restChosen = false
                                        },
                                        label = { Text(level.label) }
                                    )
                                }
                            }
                            Text(
                                text = "Horas de sono recentes: ${sleepHours}h",
                                style = MaterialTheme.typography.labelLarge,
                                color = cfo.titleGreen
                            )
                            Slider(
                                value = sleepHours.toFloat(),
                                onValueChange = { sleepHours = it.roundToInt() },
                                valueRange = 2f..10f,
                                steps = 7
                            )
                        }
                    }
                }

                if (recommendation.sleepProtectionNotice != null) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = cfo.warningBg,
                            border = BorderStroke(1.dp, cfo.goldHighlight)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Bedtime, null, tint = cfo.titleGreen)
                                    Text("Proteção do Sono e Memória", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                                }
                                Text(recommendation.sleepProtectionNotice, style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                OutlinedButton(
                                    onClick = { restChosen = true },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text("Priorizar Descanso Hoje")
                                }
                                if (restChosen) {
                                    Text(
                                        text = "Excelente decisão! Dormir bem consolida sinapses de longo prazo. Amanhã retomamos com energia.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = cfo.correctText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                items(ConstancyTier.entries) { tier ->
                    val isRecommended = recommendation.recommendedTier == tier
                    val bg = when (tier) {
                        ConstancyTier.LEVE -> cfo.softGreenSurface
                        ConstancyTier.MODERADO -> cfo.slateBlueSurface
                        ConstancyTier.COMPLETO -> cfo.goldSoftBg
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { activeTier = tier }
                            .testTag("constancy_tier_${tier.name}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = bg),
                        border = BorderStroke(
                            if (isRecommended) 2.dp else 1.dp,
                            if (isRecommended) cfo.buttonPrimary else cfo.borderSubtle.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.FitnessCenter, null, tint = cfo.titleGreen)
                                    Text("${tier.label} • ${tier.durationLabel}", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                }
                                if (isRecommended) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = cfo.buttonPrimary) {
                                        Text(
                                            "Recomendado",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = cfo.onButtonPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Text(tier.summary, style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                            Button(
                                onClick = { activeTier = tier },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text("Iniciar Modo ${tier.label}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConstancySessionPlayer(
    tier: ConstancyTier,
    data: StudyDashboardData,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit,
    onMissionFinished: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    val questions = remember(tier) {
        when (tier) {
            ConstancyTier.LEVE -> CurriculumCatalog.questionsByDifficulty(QuestionDifficulty.FACIL).take(4)
                .ifEmpty { CurriculumCatalog.allQuestions.take(4) }
            ConstancyTier.MODERADO -> {
                val errQs = data.activeErrors.mapNotNull { CurriculumCatalog.questionById(it.questionId) }
                val focusQs = CurriculumCatalog.allQuestions.filter { it.subjectId in data.profile.focusSubjects }
                (errQs + focusQs + CurriculumCatalog.allQuestions).distinctBy { it.id }.take(7)
            }
            ConstancyTier.COMPLETO -> {
                val revQs = data.pendingReviews.mapNotNull { CurriculumCatalog.questionById(it.referenceId) }
                (revQs + CurriculumCatalog.allQuestions).distinctBy { it.id }.take(12)
            }
        }
    }
    val suggestedLesson = remember(data.profile.focusSubjects) {
        CurriculumCatalog.allLessons.firstOrNull { l ->
            l.subjectId in data.profile.focusSubjects && data.lessonProgress.none { it.lessonId == l.id && it.completed }
        } ?: CurriculumCatalog.allLessons.first()
    }

    var idx by remember { mutableIntStateOf(0) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Constância • ${tier.label}",
                subtitle = tier.durationLabel,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (tier != ConstancyTier.LEVE && !done) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Aula Sugerida para Hoje", style = MaterialTheme.typography.labelLarge, color = cfo.titleGreen)
                                    Text(suggestedLesson.title, style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                }
                                OutlinedButton(onClick = { onOpenLesson(suggestedLesson.id) }) {
                                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                                    Text("Abrir Aula")
                                }
                            }
                        }
                    }
                }

                if (done) {
                    item {
                        MissionSummaryCard(
                            title = "Constância Mantida!",
                            subtitle = "Você completou o bloco ${tier.label} com $correctCount/${questions.size} acertos.",
                            onDone = onMissionFinished
                        )
                    }
                } else if (questions.isNotEmpty()) {
                    val q = questions[idx]
                    item {
                        SmoothProgressBar(progress = (idx + 1).toFloat() / questions.size.toFloat())
                    }
                    item {
                        QuestionInteractiveCard(
                            question = q,
                            selectedIndex = selectedOpt,
                            isSubmitted = submitted,
                            onSelectOption = { selectedOpt = it },
                            onSubmit = {
                                val chosen = selectedOpt ?: return@QuestionInteractiveCard
                                submitted = true
                                if (chosen == q.correctIndex) correctCount++
                                onAnswerQuestion(q, chosen, StudyMode.CONSTANCIA, 2L)
                            },
                            onAskTutor = { onAskTutor(q) },
                            nextButtonLabel = if (idx < questions.lastIndex) "Próxima (${idx + 2}/${questions.size})" else "Finalizar Bloco",
                            onNext = {
                                selectedOpt = null
                                submitted = false
                                if (idx < questions.lastIndex) idx++ else done = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit,
    onSaveDiagnostic: (DiagnosticSubjectResult) -> Unit
) {
    val cfo = CfoTheme.colors
    var activeSubject by remember { mutableStateOf<SubjectDefinition?>(null) }
    var qIndex by remember { mutableIntStateOf(0) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }
    val strengths = remember { mutableStateListOf<String>() }
    val weaknesses = remember { mutableStateListOf<String>() }
    var correctTotal by remember { mutableIntStateOf(0) }
    var completedSubjectResult by remember { mutableStateOf<DiagnosticSubjectResult?>(null) }

    if (activeSubject != null) {
        BackHandler { activeSubject = null }
        val qs = remember(activeSubject!!.id) {
            CurriculumCatalog.questionsForSubject(activeSubject!!.id).take(4)
                .ifEmpty { CurriculumCatalog.allQuestions.take(3) }
        }
        Scaffold(
            topBar = {
                CfoTopBar(
                    title = "Diagnóstico • ${activeSubject!!.shortName}",
                    subtitle = "Identificando pontos fortes e lacunas",
                    onBack = { activeSubject = null }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (completedSubjectResult != null) {
                        val res = completedSubjectResult!!
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface)
                            ) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Resultado Diagnóstico: ${res.accuracyPercent}%", style = MaterialTheme.typography.headlineSmall, color = cfo.titleGreen)
                                    if (res.strengths.isNotEmpty()) {
                                        Text("Pontos fortes: ${res.strengths.joinToString()}", style = MaterialTheme.typography.bodyMedium, color = cfo.correctText)
                                    }
                                    if (res.weaknesses.isNotEmpty()) {
                                        Text("Precisam de reforço: ${res.weaknesses.joinToString()}", style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                    }
                                    if (res.unknownTopics.isNotEmpty()) {
                                        Text("Tópicos ainda não praticados: ${res.unknownTopics.joinToString()}", style = MaterialTheme.typography.bodySmall, color = cfo.mutedText)
                                    }
                                    Button(
                                        onClick = {
                                            completedSubjectResult = null
                                            activeSubject = null
                                        },
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = cfo.buttonPrimary,
                                            contentColor = cfo.onButtonPrimary
                                        )
                                    ) {
                                        Text("Voltar às Disciplinas")
                                    }
                                }
                            }
                        }
                    } else {
                        val q = qs[qIndex]
                        item { SmoothProgressBar(progress = (qIndex + 1).toFloat() / qs.size.toFloat()) }
                        item {
                            QuestionInteractiveCard(
                                question = q,
                                selectedIndex = selectedOpt,
                                isSubmitted = submitted,
                                onSelectOption = { selectedOpt = it },
                                onSubmit = {
                                    val c = selectedOpt ?: return@QuestionInteractiveCard
                                    submitted = true
                                    onAnswerQuestion(q, c, StudyMode.DIAGNOSTICO, 2L)
                                    if (c == q.correctIndex) {
                                        correctTotal++
                                        if (q.topicName !in strengths) strengths.add(q.topicName)
                                    } else {
                                        if (q.topicName !in weaknesses) weaknesses.add(q.topicName)
                                    }
                                },
                                nextButtonLabel = if (qIndex < qs.lastIndex) "Próxima" else "Gerar Diagnóstico",
                                onNext = {
                                    selectedOpt = null
                                    submitted = false
                                    if (qIndex < qs.lastIndex) {
                                        qIndex++
                                    } else {
                                        val pct = ((correctTotal * 100L) / qs.size).coerceIn(0L, 100L)
                                        val unknown = activeSubject!!.topics.map { it.title }
                                            .filter { t -> strengths.none { t.contains(it, true) } && weaknesses.none { t.contains(it, true) } }
                                            .take(4)
                                        val result = DiagnosticSubjectResult(
                                            subjectId = activeSubject!!.id,
                                            subjectName = activeSubject!!.name,
                                            totalQuestions = qs.size.toLong(),
                                            correctCount = correctTotal.toLong(),
                                            accuracyPercent = pct,
                                            strengths = strengths.toList(),
                                            weaknesses = weaknesses.toList(),
                                            unknownTopics = unknown,
                                            needsReview = pct < 75L
                                        )
                                        onSaveDiagnostic(result)
                                        completedSubjectResult = result
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Diagnóstico Inicial",
                subtitle = "Mapeie forças e dificuldades por disciplina",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CurriculumCatalog.allSubjects, key = { it.id }) { subject ->
                    val prev = data.diagnostics.firstOrNull { it.subjectId == subject.id }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                qIndex = 0
                                selectedOpt = null
                                submitted = false
                                strengths.clear()
                                weaknesses.clear()
                                correctTotal = 0
                                completedSubjectResult = null
                                activeSubject = subject
                            }
                            .testTag("diagnostic_subject_${subject.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (prev != null) cfo.softGreenSurface else cfo.cardSurface
                        ),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(subject.name, style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                if (prev != null) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = cfo.goldSoftBg) {
                                        Text(
                                            text = "${prev.accuracyPercent}% acerto",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = cfo.bodyText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (prev == null) "Avaliação rápida disponível para personalizar seu plano."
                                else if (prev.needsReview) "Revisão recomendada em: ${prev.weaknesses.joinToString().ifBlank { "tópicos base" }}"
                                else "Bom domínio inicial! Pontos fortes: ${prev.strengths.joinToString()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.mutedText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissionSummaryCard(
    title: String,
    subtitle: String,
    onDone: () -> Unit
) {
    val cfo = CfoTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cfo.correctBg),
        border = BorderStroke(1.dp, cfo.correctText)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = cfo.correctText, modifier = Modifier.size(48.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = cfo.titleGreen)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cfo.buttonPrimary,
                    contentColor = cfo.onButtonPrimary
                )
            ) {
                Text("Concluir e Salvar")
            }
        }
    }
}
