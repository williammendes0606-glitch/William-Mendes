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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.ErrorNotebookEntry
import com.example.model.FlashcardConfidence
import com.example.model.FlashcardProgress
import com.example.model.MasteryStatus
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.QuestionOrigin
import com.example.model.StudyMode
import com.example.ui.components.CfoTopBar
import com.example.ui.components.QuestionInteractiveCard
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.StudyDashboardData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpacedReviewScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit,
    onOpenLesson: (String) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendentes", "Concluídas", "Dominados", "Reforço")
    val now = System.currentTimeMillis()

    var practicingQuestion by remember { mutableStateOf<QuestionItem?>(null) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }

    if (practicingQuestion != null) {
        BackHandler { practicingQuestion = null }
        Scaffold(
            topBar = {
                CfoTopBar(
                    title = "Revisão Espaçada",
                    subtitle = practicingQuestion!!.topicName,
                    onBack = { practicingQuestion = null }
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuestionInteractiveCard(
                        question = practicingQuestion!!,
                        selectedIndex = selectedOpt,
                        isSubmitted = submitted,
                        onSelectOption = { selectedOpt = it },
                        onSubmit = {
                            val c = selectedOpt ?: return@QuestionInteractiveCard
                            submitted = true
                            onAnswerQuestion(practicingQuestion!!, c, StudyMode.REVISAO, 1L)
                        },
                        onAskTutor = { onAskTutor(practicingQuestion!!) },
                        nextButtonLabel = "Voltar às Revisões",
                        onNext = {
                            practicingQuestion = null
                            selectedOpt = null
                            submitted = false
                        }
                    )
                }
            }
        }
        return
    }

    val pending = data.reviewItems.filter { it.nextReviewAtMillis <= now }
    val completed = data.reviewItems.filter { it.nextReviewAtMillis > now }
    val mastered = data.reviewItems.filter { it.statusEnum() == MasteryStatus.DOMINADO }
    val reinforce = data.reviewItems.filter { it.statusEnum() == MasteryStatus.REFORCO }

    val visibleList = when (selectedTab) {
        0 -> pending
        1 -> completed
        2 -> mastered
        else -> reinforce
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Revisão Espaçada Inteligente",
                subtitle = "Ciclos: Hoje • 1d • 2d • 7d • 15d • 30d",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = cfo.cardSurface,
                contentColor = cfo.titleGreen
            ) {
                tabs.forEachIndexed { idx, label ->
                    val count = when (idx) {
                        0 -> pending.size
                        1 -> completed.size
                        2 -> mastered.size
                        else -> reinforce.size
                    }
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text("$label ($count)", maxLines = 1) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (visibleList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Schedule, null, tint = cfo.titleGreen, modifier = Modifier.size(36.dp))
                                Text("Nenhum item nesta lista", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                Text(
                                    "À medida que você conclui aulas e responde questões, o algoritmo agenda automaticamente as revisões de 0, 1, 2, 7, 15 e 30 dias.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.mutedText
                                )
                            }
                        }
                    }
                } else {
                    items(visibleList, key = { it.id.ifBlank { it.referenceId } }) { item ->
                        val daysInterval = SpacedRepetitionEngine.intervalDays.getOrElse(item.stageIndex.toInt()) { 0 }
                        val dateStr = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                            .format(Date(item.nextReviewAtMillis))
                        val statusColor = when (item.statusEnum()) {
                            MasteryStatus.DOMINADO -> cfo.correctBg
                            MasteryStatus.REFORCO -> cfo.wrongBg
                            MasteryStatus.EM_ANDAMENTO -> cfo.slateBlueSurface
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                            border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.topicTitle, style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen, modifier = Modifier.weight(1f))
                                    Surface(shape = RoundedCornerShape(8.dp), color = statusColor) {
                                        Text(
                                            item.statusEnum().label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = cfo.bodyText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Ciclo atual: ${if (daysInterval == 0) "Mesmo dia" else "$daysInterval dias"} • Próxima: $dateStr • Acertos seguidos: ${item.consecutiveHits}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.mutedText
                                )
                                Button(
                                    onClick = {
                                        val q = CurriculumCatalog.questionById(item.referenceId)
                                            ?: CurriculumCatalog.questionsForTopic(item.topicId).firstOrNull()
                                        if (q != null) {
                                            selectedOpt = null
                                            submitted = false
                                            practicingQuestion = q
                                        } else {
                                            onOpenLesson(item.referenceId)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.Replay, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.size(6.dp))
                                    Text("Revisar Agora")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorNotebookScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onRetryAnswer: (QuestionItem, Int) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    var retryingEntry by remember { mutableStateOf<ErrorNotebookEntry?>(null) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }

    if (retryingEntry != null) {
        BackHandler { retryingEntry = null }
        val q = CurriculumCatalog.questionById(retryingEntry!!.questionId) ?: QuestionItem(
            id = retryingEntry!!.questionId,
            subjectId = retryingEntry!!.subjectId,
            subjectName = retryingEntry!!.subjectName,
            topicId = retryingEntry!!.subjectId,
            topicName = retryingEntry!!.topicName,
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = retryingEntry!!.sourceLabel,
            statement = retryingEntry!!.statement,
            options = retryingEntry!!.options,
            correctIndex = retryingEntry!!.correctIndex.toInt(),
            optionExplanations = emptyList(),
            generalExplanation = retryingEntry!!.explanation,
            examTrick = "Revise os pontos-chave da explicação acima."
        )
        Scaffold(
            topBar = {
                CfoTopBar(
                    title = "Refazer Questão do Caderno",
                    subtitle = retryingEntry!!.subjectName,
                    onBack = { retryingEntry = null }
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner).padding(16.dp)
            ) {
                item {
                    QuestionInteractiveCard(
                        question = q,
                        selectedIndex = selectedOpt,
                        isSubmitted = submitted,
                        onSelectOption = { selectedOpt = it },
                        onSubmit = {
                            val c = selectedOpt ?: return@QuestionInteractiveCard
                            submitted = true
                            onRetryAnswer(q, c)
                        },
                        onAskTutor = { onAskTutor(q) },
                        nextButtonLabel = "Voltar ao Caderno",
                        onNext = {
                            retryingEntry = null
                            selectedOpt = null
                            submitted = false
                        }
                    )
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Caderno de Erros",
                subtitle = "${data.activeErrors.size} pendentes • ${data.masteredErrorsCount} superados",
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
                if (data.errorNotebook.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.ErrorOutline, null, tint = cfo.titleGreen, modifier = Modifier.size(36.dp))
                                Text("Caderno de Erros vazio", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                Text(
                                    "Sempre que você errar uma questão em qualquer modo de estudo, ela será salva automaticamente aqui com a justificativa completa.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.mutedText
                                )
                            }
                        }
                    }
                } else {
                    items(data.errorNotebook, key = { it.id.ifBlank { it.questionId } }) { err ->
                        val dateText = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(err.recordedAtMillis))
                        val letters = listOf("A", "B", "C", "D", "E")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (err.mastered) cfo.correctBg else cfo.cardSurface
                            ),
                            border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${err.subjectName} • ${err.topicName}", style = MaterialTheme.typography.labelLarge, color = cfo.titleGreen)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (err.mastered) cfo.correctText else cfo.wrongText
                                    ) {
                                        Text(
                                            text = if (err.mastered) "Superada" else "Pendente",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = cfo.onButtonPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(err.statement, style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                Text(
                                    text = "Marcada: ${letters.getOrElse(err.chosenIndex.toInt()) { "?" }} • Correta: ${letters.getOrElse(err.correctIndex.toInt()) { "?" }} • Data: $dateText • Tentativas: ${err.retryCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = cfo.mutedText
                                )
                                Text("Explicação: ${err.explanation}", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                Button(
                                    onClick = {
                                        selectedOpt = null
                                        submitted = false
                                        retryingEntry = err
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Text("Refazer Questão")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlashcardsScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onRateFlashcard: (FlashcardProgress, FlashcardConfidence) -> Unit,
    onCreateFlashcard: (subjectId: String, topicTitle: String, front: String, back: String, tip: String) -> Unit
) {
    val cfo = CfoTheme.colors
    var selectedSubjectFilter by remember { mutableStateOf<String?>(null) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val cards = remember(data.flashcards, selectedSubjectFilter) {
        data.flashcards.filter { selectedSubjectFilter == null || it.subjectId == selectedSubjectFilter }
    }

    if (showCreateDialog) {
        var front by remember { mutableStateOf("") }
        var back by remember { mutableStateOf("") }
        var topic by remember { mutableStateOf("") }
        var tip by remember { mutableStateOf("") }
        var subjId by remember { mutableStateOf("portugues") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Novo Flashcard") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CurriculumCatalog.allSubjects.take(4).forEach { s ->
                            FilterChip(
                                selected = subjId == s.id,
                                onClick = { subjId = s.id },
                                label = { Text(s.shortName) }
                            )
                        }
                    }
                    OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Assunto") }, singleLine = true)
                    OutlinedTextField(value = front, onValueChange = { front = it }, label = { Text("Pergunta (Frente)") })
                    OutlinedTextField(value = back, onValueChange = { back = it }, label = { Text("Resposta (Verso)") })
                    OutlinedTextField(value = tip, onValueChange = { tip = it }, label = { Text("Macete (Opcional)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (front.isNotBlank() && back.isNotBlank()) {
                            onCreateFlashcard(subjId, topic, front, back, tip)
                            showCreateDialog = false
                        }
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Flashcards Inteligentes",
                subtitle = "${cards.size} cartões no baralho",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = cfo.buttonPrimary,
                contentColor = cfo.onButtonPrimary,
                modifier = Modifier.testTag("add_flashcard_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Flashcard")
            }
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
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = selectedSubjectFilter == null,
                            onClick = {
                                selectedSubjectFilter = null
                                currentIdx = 0
                                flipped = false
                            },
                            label = { Text("Todos") }
                        )
                        CurriculumCatalog.allSubjects.filter { s ->
                            data.flashcards.any { it.subjectId == s.id }
                        }.forEach { s ->
                            FilterChip(
                                selected = selectedSubjectFilter == s.id,
                                onClick = {
                                    selectedSubjectFilter = s.id
                                    currentIdx = 0
                                    flipped = false
                                },
                                label = { Text(s.shortName) }
                            )
                        }
                    }
                }

                if (cards.isNotEmpty()) {
                    val activeCard = cards[currentIdx % cards.size]
                    item {
                        Text(
                            text = "Cartão ${(currentIdx % cards.size) + 1} de ${cards.size} • Status: ${activeCard.confidenceEnum().label}",
                            style = MaterialTheme.typography.labelLarge,
                            color = cfo.titleGreen
                        )
                    }
                    item {
                        FlashcardInteractiveTile(
                            card = activeCard,
                            flipped = flipped,
                            onToggleFlip = { flipped = !flipped },
                            onRate = { confidence ->
                                onRateFlashcard(activeCard, confidence)
                                flipped = false
                                currentIdx = (currentIdx + 1) % cards.size
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardInteractiveTile(
    card: FlashcardProgress,
    flipped: Boolean,
    onToggleFlip: () -> Unit,
    onRate: (FlashcardConfidence) -> Unit
) {
    val cfo = CfoTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 210.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onToggleFlip() }
            .testTag("flashcard_tile"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (flipped) cfo.softGreenSurface else cfo.cardSurface
        ),
        border = BorderStroke(1.5.dp, cfo.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(shape = RoundedCornerShape(8.dp), color = cfo.slateBlueSurface) {
                    Text(
                        text = card.topicTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = cfo.bodyText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = if (flipped) "VERSO (Toque para virar)" else "FRENTE (Toque para ver resposta)",
                    style = MaterialTheme.typography.labelSmall,
                    color = cfo.mutedText
                )
            }

            if (!flipped) {
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.titleLarge,
                    color = cfo.titleGreen
                )
            } else {
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.bodyLarge,
                    color = cfo.bodyText
                )
                if (card.memoryTip.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(10.dp), color = cfo.goldSoftBg) {
                        Text(
                            text = "Dica: ${card.memoryTip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = cfo.bodyText,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }

    if (flipped) {
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onRate(FlashcardConfidence.NAO_SEI) },
                modifier = Modifier.weight(1f).heightIn(min = 50.dp).testTag("flashcard_nao_sei"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Não sei", color = cfo.wrongText, maxLines = 1)
            }
            OutlinedButton(
                onClick = { onRate(FlashcardConfidence.TENHO_DUVIDA) },
                modifier = Modifier.weight(1f).heightIn(min = 50.dp).testTag("flashcard_duvida"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Dúvida", color = cfo.titleGreen, maxLines = 1)
            }
            Button(
                onClick = { onRate(FlashcardConfidence.SEI_RESPONDER) },
                modifier = Modifier.weight(1f).heightIn(min = 50.dp).testTag("flashcard_sei"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cfo.correctText,
                    contentColor = cfo.onButtonPrimary
                )
            ) {
                Text("Sei!", maxLines = 1)
            }
        }
    }
}
