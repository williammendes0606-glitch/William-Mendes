package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.model.QuestionItem
import com.example.model.SimulatedExamMode
import com.example.model.StudyMode
import com.example.ui.components.CfoTopBar
import com.example.ui.components.OriginAndDifficultyBadges
import com.example.ui.components.QuestionInteractiveCard
import com.example.ui.components.SmoothProgressBar
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.StudyDashboardData
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ActiveSimuladoSession(
    val title: String,
    val mode: SimulatedExamMode,
    val subjectId: String,
    val questions: List<QuestionItem>,
    val timeLimitSeconds: Long
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimuladosScreen(
    data: StudyDashboardData,
    onBack: (() -> Unit)? = null,
    onAnswerQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit,
    onSaveSimulatedExam: (
        title: String,
        mode: SimulatedExamMode,
        subjectId: String,
        totalQuestions: Long,
        correctCount: Long,
        durationSeconds: Long,
        breakdownSummary: String
    ) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    var selectedMode by remember { mutableStateOf(SimulatedExamMode.MISTO) }
    var selectedSubjectId by remember { mutableStateOf("portugues") }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var activeSession by remember { mutableStateOf<ActiveSimuladoSession?>(null) }
    var finishedAnswers by remember { mutableStateOf<Map<String, Int>?>(null) }
    var finishedElapsedSec by remember { mutableLongStateOf(0L) }

    if (activeSession != null && finishedAnswers != null) {
        BackHandler {
            activeSession = null
            finishedAnswers = null
        }
        SimuladoGabaritoScreen(
            session = activeSession!!,
            answers = finishedAnswers!!,
            elapsedSeconds = finishedElapsedSec,
            onAskTutor = onAskTutor,
            onClose = {
                activeSession = null
                finishedAnswers = null
            }
        )
        return
    }

    if (activeSession != null) {
        BackHandler { activeSession = null }
        SimuladoRunnerScreen(
            session = activeSession!!,
            onCancel = { activeSession = null },
            onFinish = { answersMap, elapsedSec ->
                val session = activeSession!!
                var correct = 0L
                val bySubject = mutableMapOf<String, Pair<Int, Int>>()
                session.questions.forEach { q ->
                    val chosen = answersMap[q.id]
                    if (chosen != null) {
                        onAnswerQuestion(q, chosen, StudyMode.SIMULADO, 1L)
                        val isRight = chosen == q.correctIndex
                        if (isRight) correct++
                        val prev = bySubject[q.subjectName] ?: (0 to 0)
                        bySubject[q.subjectName] = (prev.first + if (isRight) 1 else 0) to (prev.second + 1)
                    } else {
                        val prev = bySubject[q.subjectName] ?: (0 to 0)
                        bySubject[q.subjectName] = prev.first to (prev.second + 1)
                    }
                }
                val summary = bySubject.entries.joinToString(" | ") { (subj, pair) ->
                    "$subj: ${pair.first}/${pair.second}"
                }
                onSaveSimulatedExam(
                    session.title,
                    session.mode,
                    session.subjectId,
                    session.questions.size.toLong(),
                    correct,
                    elapsedSec,
                    summary
                )
                finishedElapsedSec = elapsedSec
                finishedAnswers = answersMap
            }
        )
        return
    }

    val chosenSubject = CurriculumCatalog.subjectById(selectedSubjectId) ?: CurriculumCatalog.allSubjects.first()

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Simulados PAES/UEMA",
                subtitle = "Cronômetro real, correção automática e gabarito comentado",
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
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Configurar Novo Simulado",
                                style = MaterialTheme.typography.titleLarge,
                                color = cfo.titleGreen
                            )

                            Text(
                                text = "1. Modalidade",
                                style = MaterialTheme.typography.labelLarge,
                                color = cfo.titleGreen
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SimulatedExamMode.entries.forEach { mode ->
                                    FilterChip(
                                        selected = selectedMode == mode,
                                        onClick = { selectedMode = mode },
                                        label = { Text(mode.label) },
                                        modifier = Modifier.testTag("simulado_mode_${mode.name}")
                                    )
                                }
                            }

                            if (selectedMode == SimulatedExamMode.DISCIPLINA || selectedMode == SimulatedExamMode.ASSUNTO) {
                                Text(
                                    text = "2. Escolha a Disciplina",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = cfo.titleGreen
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    CurriculumCatalog.allSubjects.forEach { subj ->
                                        FilterChip(
                                            selected = selectedSubjectId == subj.id,
                                            onClick = {
                                                selectedSubjectId = subj.id
                                                selectedTopicId = subj.topics.firstOrNull()?.id
                                            },
                                            label = { Text(subj.shortName) }
                                        )
                                    }
                                }
                            }

                            if (selectedMode == SimulatedExamMode.ASSUNTO) {
                                Text(
                                    text = "3. Escolha o Assunto",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = cfo.titleGreen
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    chosenSubject.topics.forEach { t ->
                                        FilterChip(
                                            selected = selectedTopicId == t.id,
                                            onClick = { selectedTopicId = t.id },
                                            label = { Text(t.title, maxLines = 1) }
                                        )
                                    }
                                }
                            }

                            val previewQuestions = remember(selectedMode, selectedSubjectId, selectedTopicId) {
                                buildSimuladoQuestions(selectedMode, selectedSubjectId, selectedTopicId)
                            }
                            val minutesLimit = (previewQuestions.size * 3).coerceIn(10, 90)

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cfo.softGreenSurface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${previewQuestions.size} questões inéditas (Padrão PAES/UEMA)",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = cfo.titleGreen
                                        )
                                        Text(
                                            text = "Tempo sugerido: $minutesLimit minutos",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = cfo.mutedText
                                        )
                                    }
                                    Icon(Icons.Default.Timer, "Cronômetro", tint = cfo.titleGreen)
                                }
                            }

                            Button(
                                onClick = {
                                    val title = when (selectedMode) {
                                        SimulatedExamMode.DISCIPLINA -> "Simulado • ${chosenSubject.shortName}"
                                        SimulatedExamMode.ASSUNTO -> {
                                            val topicName = chosenSubject.topics.firstOrNull { it.id == selectedTopicId }?.title ?: chosenSubject.shortName
                                            "Simulado • $topicName"
                                        }
                                        SimulatedExamMode.MISTO -> "Simulado Misto CFO MA"
                                        SimulatedExamMode.COMPLETO -> "Simulado Completo PAES/UEMA"
                                    }
                                    activeSession = ActiveSimuladoSession(
                                        title = title,
                                        mode = selectedMode,
                                        subjectId = if (selectedMode == SimulatedExamMode.MISTO || selectedMode == SimulatedExamMode.COMPLETO) "all" else selectedSubjectId,
                                        questions = previewQuestions,
                                        timeLimitSeconds = minutesLimit * 60L
                                    )
                                    finishedAnswers = null
                                },
                                enabled = previewQuestions.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp)
                                    .testTag("start_simulado_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Iniciar Simulado Cronometrado")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Histórico de Simulados (${data.simulatedExams.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = cfo.titleGreen
                    )
                }

                if (data.simulatedExams.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cfo.slateBlueSurface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Quiz, null, tint = cfo.titleGreen)
                                Text(
                                    text = "Você ainda não concluiu simulados. Comece um acima para registrar sua evolução percentual.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.bodyText
                                )
                            }
                        }
                    }
                } else {
                    items(data.simulatedExams, key = { it.id.ifBlank { "${it.completedAtMillis}_${it.title}" } }) { exam ->
                        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(exam.completedAtMillis))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                            border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = exam.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = cfo.titleGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (exam.scorePercent >= 70L) cfo.correctBg else cfo.goldSoftBg
                                    ) {
                                        Text(
                                            text = "${exam.scorePercent}% (${exam.correctCount}/${exam.totalQuestions})",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = cfo.bodyText,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                val mins = (exam.durationSeconds / 60L).coerceAtLeast(1L)
                                Text(
                                    text = "$dateStr • Duração: ${mins} min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = cfo.mutedText
                                )
                                if (exam.breakdownSummary.isNotBlank()) {
                                    Text(
                                        text = exam.breakdownSummary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = cfo.bodyText
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

private fun buildSimuladoQuestions(
    mode: SimulatedExamMode,
    subjectId: String,
    topicId: String?
): List<QuestionItem> {
    return when (mode) {
        SimulatedExamMode.DISCIPLINA -> {
            CurriculumCatalog.questionsForSubject(subjectId).take(10)
        }
        SimulatedExamMode.ASSUNTO -> {
            val byTopic = topicId?.let { CurriculumCatalog.questionsForTopic(it) }.orEmpty()
            if (byTopic.isNotEmpty()) byTopic else CurriculumCatalog.questionsForSubject(subjectId).take(5)
        }
        SimulatedExamMode.MISTO -> {
            val pt = CurriculumCatalog.questionsForSubject("portugues").take(3)
            val mat = CurriculumCatalog.questionsForSubject("matematica").take(3)
            val others = CurriculumCatalog.allQuestions.filter { it.subjectId !in setOf("portugues", "matematica") }.take(4)
            (pt + mat + others).distinctBy { it.id }
        }
        SimulatedExamMode.COMPLETO -> {
            CurriculumCatalog.allQuestions.take(20)
        }
    }
}

@Composable
private fun SimuladoRunnerScreen(
    session: ActiveSimuladoSession,
    onCancel: () -> Unit,
    onFinish: (Map<String, Int>, Long) -> Unit
) {
    val cfo = CfoTheme.colors
    var currentIndex by remember { mutableIntStateOf(0) }
    val selectedMap = remember { mutableStateMapOf<String, Int>() }
    var elapsedSec by remember { mutableLongStateOf(0L) }

    LaunchedEffect(session.title) {
        while (true) {
            delay(1000L)
            elapsedSec++
        }
    }

    val remaining = (session.timeLimitSeconds - elapsedSec).coerceAtLeast(0L)
    val remMin = remaining / 60L
    val remSec = remaining % 60L
    val timerLabel = String.format(Locale.US, "%02d:%02d", remMin, remSec)
    val q = session.questions[currentIndex]

    Scaffold(
        topBar = {
            CfoTopBar(
                title = session.title,
                subtitle = "Questão ${currentIndex + 1}/${session.questions.size} • Restam $timerLabel",
                onBack = onCancel,
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (remaining < 120L) cfo.wrongBg else cfo.softGreenSurface,
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Timer, null, tint = cfo.titleGreen, modifier = Modifier.size(16.dp))
                            Text(timerLabel, style = MaterialTheme.typography.labelLarge, color = cfo.bodyText)
                        }
                    }
                }
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
                item {
                    SmoothProgressBar(progress = selectedMap.size.toFloat() / session.questions.size.coerceAtLeast(1).toFloat())
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        session.questions.forEachIndexed { idx, item ->
                            val answered = selectedMap.containsKey(item.id)
                            val isCurrent = idx == currentIndex
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { currentIndex = idx },
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isCurrent -> cfo.buttonPrimary
                                    answered -> cfo.softGreenSurface
                                    else -> cfo.cardSurface
                                },
                                border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isCurrent) cfo.onButtonPrimary else cfo.titleGreen
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    val letters = listOf("A", "B", "C", "D", "E")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OriginAndDifficultyBadges(origin = q.origin, difficulty = q.difficulty)
                            Text(
                                text = "${q.subjectName} • ${q.topicName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = cfo.mutedText
                            )
                            Text(
                                text = q.statement,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = cfo.bodyText
                            )
                            q.options.forEachIndexed { optIdx, optText ->
                                val isSel = selectedMap[q.id] == optIdx
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 50.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedMap[q.id] = optIdx }
                                        .testTag("simulado_option_$optIdx"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSel) cfo.slateBlueSurface else MaterialTheme.colorScheme.background,
                                    border = BorderStroke(
                                        if (isSel) 2.dp else 1.dp,
                                        if (isSel) cfo.buttonPrimary else cfo.borderSubtle.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "${letters.getOrElse(optIdx) { "${optIdx + 1}" }})",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = cfo.titleGreen
                                        )
                                        Text(optText, style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (currentIndex > 0) currentIndex-- },
                            enabled = currentIndex > 0,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) {
                            Text("Anterior")
                        }
                        if (currentIndex < session.questions.lastIndex) {
                            Button(
                                onClick = { currentIndex++ },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text("Próxima")
                            }
                        } else {
                            Button(
                                onClick = { onFinish(selectedMap.toMap(), elapsedSec) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("finish_simulado_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.correctText,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text("Finalizar e Corrigir")
                            }
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { onFinish(selectedMap.toMap(), elapsedSec) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text("Encerrar Agora (${selectedMap.size}/${session.questions.size} marcadas)")
                    }
                }
            }
        }
    }
}

@Composable
private fun SimuladoGabaritoScreen(
    session: ActiveSimuladoSession,
    answers: Map<String, Int>,
    elapsedSeconds: Long,
    onAskTutor: (QuestionItem) -> Unit,
    onClose: () -> Unit
) {
    val cfo = CfoTheme.colors
    val correctCount = session.questions.count { answers[it.id] == it.correctIndex }
    val pct = if (session.questions.isNotEmpty()) (correctCount * 100) / session.questions.size else 0
    val mins = (elapsedSeconds / 60L).coerceAtLeast(1L)

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Gabarito Comentado",
                subtitle = session.title,
                onBack = onClose
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
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (pct >= 70) cfo.correctBg else cfo.goldSoftBg
                        ),
                        border = BorderStroke(1.dp, cfo.borderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = cfo.titleGreen, modifier = Modifier.size(40.dp))
                            Text(
                                text = "Desempenho: $pct% ($correctCount de ${session.questions.size})",
                                style = MaterialTheme.typography.headlineSmall,
                                color = cfo.titleGreen
                            )
                            Text(
                                text = "Tempo total: $mins min • Questões incorretas foram salvas no Caderno de Erros.",
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.bodyText
                            )
                            Button(
                                onClick = onClose,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text("Concluir Revisão do Gabarito")
                            }
                        }
                    }
                }

                items(session.questions, key = { "gab_${it.id}" }) { q ->
                    QuestionInteractiveCard(
                        question = q,
                        selectedIndex = answers[q.id],
                        isSubmitted = true,
                        onSelectOption = {},
                        onSubmit = {},
                        onAskTutor = { onAskTutor(q) }
                    )
                }
            }
        }
    }
}
