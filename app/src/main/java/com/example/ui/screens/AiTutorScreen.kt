package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.curriculum.CurriculumCatalog
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.StudyMode
import com.example.model.UiState
import com.example.ui.components.CfoTopBar
import com.example.ui.components.QuestionInteractiveCard
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.AiViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiTutorScreen(
    aiViewModel: AiViewModel,
    initialPrompt: String? = null,
    onConsumeInitialPrompt: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    onAnswerGeneratedQuestion: (QuestionItem, Int, StudyMode, Long) -> Unit
) {
    val cfo = CfoTheme.colors
    val messages by aiViewModel.messages.collectAsStateWithLifecycle()
    val chatBusy by aiViewModel.chatBusy.collectAsStateWithLifecycle()
    val genQuestionState by aiViewModel.generatedQuestionState.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showGeneratorPanel by remember { mutableStateOf(false) }
    var selectedSubjectId by remember { mutableStateOf("portugues") }
    var selectedDifficulty by remember { mutableStateOf(QuestionDifficulty.MEDIO) }
    var genSelectedOpt by remember { mutableStateOf<Int?>(null) }
    var genSubmitted by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            aiViewModel.sendMessage(initialPrompt)
            onConsumeInitialPrompt()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val selectedSubject = CurriculumCatalog.subjectById(selectedSubjectId) ?: CurriculumCatalog.allSubjects.first()

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Professor de IA • CFO MA",
                subtitle = "Explicações passo a passo, macetes e treino adaptativo",
                onBack = onBack,
                actions = {
                    OutlinedButton(
                        onClick = { showGeneratorPanel = !showGeneratorPanel },
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (showGeneratorPanel) "Chat" else "Gerar Questão", maxLines = 1)
                    }
                }
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
            if (showGeneratorPanel) {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                Text(
                                    text = "Gerar Questão Inédita de Treino (IA)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = cfo.titleGreen
                                )
                                Text(
                                    text = "Transparência: questões criadas por IA são sempre marcadas como 'Gerada por IA (Treino)' e nunca apresentadas como provas oficiais.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.mutedText
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    CurriculumCatalog.allSubjects.forEach { s ->
                                        FilterChip(
                                            selected = selectedSubjectId == s.id,
                                            onClick = { selectedSubjectId = s.id },
                                            label = { Text(s.shortName) }
                                        )
                                    }
                                }
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    QuestionDifficulty.entries.forEach { d ->
                                        FilterChip(
                                            selected = selectedDifficulty == d,
                                            onClick = { selectedDifficulty = d },
                                            label = { Text(d.label) }
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        genSelectedOpt = null
                                        genSubmitted = false
                                        val topicName = selectedSubject.topics.randomOrNull()?.title ?: selectedSubject.name
                                        aiViewModel.generatePracticeQuestion(
                                            subjectId = selectedSubject.id,
                                            subjectName = selectedSubject.name,
                                            topicName = topicName,
                                            difficulty = selectedDifficulty
                                        )
                                    },
                                    enabled = genQuestionState !is UiState.Loading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("generate_ai_question_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Text("Gerar Exercício Agora")
                                }
                            }
                        }
                    }

                    when (val state = genQuestionState) {
                        is UiState.Loading -> item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = cfo.titleGreen)
                                    Text("Elaborando questão no padrão PAES/UEMA...", color = cfo.bodyText)
                                }
                            }
                        }
                        is UiState.Error -> item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = cfo.wrongBg
                            ) {
                                Text(
                                    text = state.message,
                                    color = cfo.wrongText,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        is UiState.Success -> item {
                            QuestionInteractiveCard(
                                question = state.data,
                                selectedIndex = genSelectedOpt,
                                isSubmitted = genSubmitted,
                                onSelectOption = { genSelectedOpt = it },
                                onSubmit = {
                                    val c = genSelectedOpt ?: return@QuestionInteractiveCard
                                    genSubmitted = true
                                    onAnswerGeneratedQuestion(state.data, c, StudyMode.AULA, 2L)
                                },
                                onAskTutor = {
                                    showGeneratorPanel = false
                                    aiViewModel.sendMessage(
                                        "Explique passo a passo esta questão de ${state.data.subjectName}: ${state.data.statement}"
                                    )
                                }
                            )
                        }
                        null -> {}
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "Explicar Regra de Três e Porcentagem do zero",
                                "Macetes de Crase e Concordância PAES/UEMA",
                                "Resumo de História do Maranhão para o CFO",
                                "Como estruturar introdução de redação?"
                            ).forEach { suggestion ->
                                FilterChip(
                                    selected = false,
                                    onClick = { if (!chatBusy) aiViewModel.sendMessage(suggestion) },
                                    label = { Text(suggestion, maxLines = 1) }
                                )
                            }
                        }
                    }

                    items(messages, key = { it.id }) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.fromUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                modifier = Modifier.widthIn(max = 460.dp),
                                shape = RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (msg.fromUser) 18.dp else 4.dp,
                                    bottomEnd = if (msg.fromUser) 4.dp else 18.dp
                                ),
                                color = if (msg.fromUser) cfo.buttonPrimary else cfo.cardSurface,
                                border = if (msg.fromUser) null else BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (!msg.fromUser) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.School, null, tint = cfo.titleGreen, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Professor IA • CFO MA",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = cfo.titleGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    if (msg.streaming && msg.text.isBlank()) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = cfo.titleGreen)
                                    } else {
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (msg.fromUser) cfo.onButtonPrimary else cfo.bodyText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = cfo.cardSurface,
                    border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Tire uma dúvida de Português, Matemática ou edital...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tutor_chat_input"),
                            maxLines = 3,
                            shape = RoundedCornerShape(14.dp)
                        )
                        IconButton(
                            onClick = {
                                val text = inputText.trim()
                                if (text.isNotEmpty() && !chatBusy) {
                                    inputText = ""
                                    aiViewModel.sendMessage(text)
                                }
                            },
                            enabled = inputText.isNotBlank() && !chatBusy,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("tutor_chat_send"),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = cfo.buttonPrimary,
                                contentColor = cfo.onButtonPrimary
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar pergunta")
                        }
                    }
                }
            }
        }
    }
}
