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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.model.KnowledgeArea
import com.example.model.LessonContent
import com.example.model.QuestionItem
import com.example.model.StudyMode
import com.example.model.SubjectDefinition
import com.example.ui.components.CfoTopBar
import com.example.ui.components.QuestionInteractiveCard
import com.example.ui.components.SmoothProgressBar
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.StudyDashboardData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectsAndLessonsScreen(
    data: StudyDashboardData,
    initialLessonId: String? = null,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode) -> Unit,
    onCompleteLesson: (LessonContent, Long, Boolean, Long, Long) -> Unit,
    onAskTutorAboutQuestion: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    var selectedArea by remember { mutableStateOf<KnowledgeArea?>(null) }
    var selectedSubject by remember { mutableStateOf<SubjectDefinition?>(null) }
    var activeLesson by remember {
        mutableStateOf(initialLessonId?.let { id -> CurriculumCatalog.allLessons.firstOrNull { it.id == id } })
    }

    if (activeLesson != null) {
        BackHandler { activeLesson = null }
        LessonPlayerScreen(
            lesson = activeLesson!!,
            onBack = { activeLesson = null },
            onAnswerQuestion = onAnswerQuestion,
            onFinishLesson = { scorePercent, minutes ->
                onCompleteLesson(activeLesson!!, 6L, true, scorePercent, minutes)
                activeLesson = null
            },
            onAskTutor = onAskTutorAboutQuestion
        )
        return
    }

    if (selectedSubject != null) {
        BackHandler { selectedSubject = null }
        SubjectDetailScreen(
            subject = selectedSubject!!,
            data = data,
            onBack = { selectedSubject = null },
            onOpenLesson = { activeLesson = it },
            onAnswerQuestion = onAnswerQuestion,
            onAskTutor = onAskTutorAboutQuestion
        )
        return
    }

    val filteredSubjects = CurriculumCatalog.allSubjects.filter {
        selectedArea == null || it.area == selectedArea
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Modo Aula • Disciplinas",
                subtitle = "Edital PAES/UEMA • CFO PMMA",
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
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = selectedArea == null,
                            onClick = { selectedArea = null },
                            label = { Text("Todas (${CurriculumCatalog.allSubjects.size})") }
                        )
                        KnowledgeArea.entries.filter { it != KnowledgeArea.REDACAO }.forEach { area ->
                            FilterChip(
                                selected = selectedArea == area,
                                onClick = { selectedArea = area },
                                label = { Text(area.label) }
                            )
                        }
                    }
                }

                items(filteredSubjects, key = { it.id }) { subject ->
                    val lessons = CurriculumCatalog.lessonsForSubject(subject.id)
                    val completedCount = lessons.count { l ->
                        data.lessonProgress.any { it.lessonId == l.id && it.completed }
                    }
                    val isPriority = subject.id in data.profile.focusSubjects

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedSubject = subject }
                            .testTag("subject_card_${subject.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPriority) cfo.softGreenSurface else cfo.cardSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isPriority) cfo.buttonPrimary.copy(alpha = 0.55f)
                            else cfo.borderSubtle.copy(alpha = 0.45f)
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
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = cfo.titleGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isPriority) cfo.goldSoftBg else cfo.slateBlueSurface
                                ) {
                                    Text(
                                        text = if (isPriority) "Foco Prioritário" else subject.area.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = cfo.bodyText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = subject.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.mutedText
                            )

                            Text(
                                text = "${subject.topics.size} tópicos do edital • $completedCount/${lessons.size} aulas concluídas",
                                style = MaterialTheme.typography.labelMedium,
                                color = cfo.titleGreen
                            )

                            if (lessons.isNotEmpty()) {
                                SmoothProgressBar(
                                    progress = completedCount.toFloat() / lessons.size.toFloat()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectDetailScreen(
    subject: SubjectDefinition,
    data: StudyDashboardData,
    onBack: () -> Unit,
    onOpenLesson: (LessonContent) -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    val lessons = CurriculumCatalog.lessonsForSubject(subject.id)
    val questions = CurriculumCatalog.questionsForSubject(subject.id)
    var practiceIdx by remember { mutableIntStateOf(0) }
    var selectedOpt by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = subject.shortName,
                subtitle = subject.area.label,
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
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = cfo.slateBlueSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, null, tint = cfo.titleGreen)
                            Text(
                                text = subject.editalStatusNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.bodyText
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Aulas Completas",
                        style = MaterialTheme.typography.titleLarge,
                        color = cfo.titleGreen
                    )
                }

                items(lessons, key = { it.id }) { lesson ->
                    val done = data.lessonProgress.any { it.lessonId == lesson.id && it.completed }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenLesson(lesson) }
                            .testTag("lesson_card_${lesson.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (done) cfo.correctBg else cfo.cardSurface
                        ),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (done) cfo.correctText else cfo.softGreenSurface
                            ) {
                                Icon(
                                    imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                    contentDescription = lesson.title,
                                    tint = if (done) cfo.onButtonPrimary else cfo.titleGreen,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = cfo.titleGreen
                                )
                                Text(
                                    text = "${lesson.estimatedMinutes} min • ${lesson.editalBadge}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = cfo.mutedText
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Conteúdos Confirmados no Edital",
                        style = MaterialTheme.typography.titleLarge,
                        color = cfo.titleGreen
                    )
                }

                items(subject.topics, key = { it.id }) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = cfo.titleGreen
                            )
                            topic.subtopics.forEach { sub ->
                                Text(
                                    text = "• $sub",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.mutedText
                                )
                            }
                        }
                    }
                }

                if (questions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Banco de Questões da Disciplina (${questions.size})",
                            style = MaterialTheme.typography.titleLarge,
                            color = cfo.titleGreen
                        )
                    }
                    item {
                        val q = questions[practiceIdx % questions.size]
                        QuestionInteractiveCard(
                            question = q,
                            selectedIndex = selectedOpt,
                            isSubmitted = submitted,
                            onSelectOption = { selectedOpt = it },
                            onSubmit = {
                                val chosen = selectedOpt ?: return@QuestionInteractiveCard
                                submitted = true
                                onAnswerQuestion(q, chosen, StudyMode.AULA)
                            },
                            onAskTutor = { onAskTutor(q) },
                            nextButtonLabel = "Próxima Questão",
                            onNext = {
                                practiceIdx = (practiceIdx + 1) % questions.size
                                selectedOpt = null
                                submitted = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonPlayerScreen(
    lesson: LessonContent,
    onBack: () -> Unit,
    onAnswerQuestion: (QuestionItem, Int, StudyMode) -> Unit,
    onFinishLesson: (scorePercent: Long, minutes: Long) -> Unit,
    onAskTutor: (QuestionItem) -> Unit
) {
    val cfo = CfoTheme.colors
    var step by remember { mutableIntStateOf(0) }
    val stepTitles = listOf(
        "1. Do Zero (Básico)",
        "2. Aprofundamento",
        "3. Exemplos Passo a Passo",
        "4. Macetes & Memorização",
        "5. Resumo Estruturado",
        "6. Exercícios de Fixação",
        "7. Avaliação Final da Aula"
    )

    val practiceQuestions = remember(lesson.id) {
        lesson.practiceQuestionIds.mapNotNull { CurriculumCatalog.questionById(it) }
    }
    val finalQuestions = remember(lesson.id) {
        lesson.finalExamQuestionIds.mapNotNull { CurriculumCatalog.questionById(it) }
    }
    val selectedMap = remember { mutableStateMapOf<String, Int>() }
    val submittedMap = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = lesson.title,
                subtitle = stepTitles[step],
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
                    SmoothProgressBar(progress = (step + 1).toFloat() / stepTitles.size.toFloat())
                }

                when (step) {
                    0 -> item {
                        LessonSectionCard(
                            title = "Entendendo do Zero",
                            badge = lesson.editalBadge,
                            body = lesson.basicExplanation,
                            containerColor = cfo.cardSurface
                        )
                    }
                    1 -> item {
                        LessonSectionCard(
                            title = "Desenvolvimento Aprofundado",
                            badge = "Nível Prova UEMA/PAES",
                            body = lesson.deepExplanation,
                            containerColor = cfo.cardSurface
                        )
                    }
                    2 -> {
                        items(lesson.workedExamples) { ex ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                                border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(ex.title, style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                    Surface(shape = RoundedCornerShape(12.dp), color = cfo.slateBlueSurface) {
                                        Text(
                                            text = ex.problem,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = cfo.bodyText,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                    ex.stepByStep.forEach { s ->
                                        Text(s, style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                    }
                                    Surface(shape = RoundedCornerShape(10.dp), color = cfo.correctBg) {
                                        Text(
                                            text = "Conclusão: ${ex.answer}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = cfo.correctText,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cfo.goldSoftBg)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Lightbulb, null, tint = cfo.titleGreen)
                                        Text("Macetes para Prova", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                    }
                                    lesson.examTricks.forEach { trick ->
                                        Text("• $trick", style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                    }
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Psychology, null, tint = cfo.titleGreen)
                                        Text("Técnicas de Memorização", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                    }
                                    lesson.memoryTechniques.forEach { tech ->
                                        Text("• $tech", style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                    }
                                }
                            }
                        }
                    }
                    4 -> item {
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = cfo.titleGreen)
                                    Text("Resumo de Ouro", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                }
                                lesson.structuredSummary.forEachIndexed { i, point ->
                                    Text("${i + 1}. $point", style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                }
                            }
                        }
                    }
                    5 -> {
                        items(practiceQuestions, key = { "prac_${it.id}" }) { q ->
                            QuestionInteractiveCard(
                                question = q,
                                selectedIndex = selectedMap["prac_${q.id}"],
                                isSubmitted = submittedMap["prac_${q.id}"] == true,
                                onSelectOption = { selectedMap["prac_${q.id}"] = it },
                                onSubmit = {
                                    val c = selectedMap["prac_${q.id}"] ?: return@QuestionInteractiveCard
                                    submittedMap["prac_${q.id}"] = true
                                    onAnswerQuestion(q, c, StudyMode.AULA)
                                },
                                onAskTutor = { onAskTutor(q) }
                            )
                        }
                    }
                    6 -> {
                        item {
                            Surface(shape = RoundedCornerShape(14.dp), color = cfo.slateBlueSurface) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.School, null, tint = cfo.titleGreen)
                                    Text(
                                        text = "Responda às questões finais abaixo para concluir esta aula e registrar +50 XP.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = cfo.bodyText
                                    )
                                }
                            }
                        }
                        items(finalQuestions, key = { "fin_${it.id}" }) { q ->
                            QuestionInteractiveCard(
                                question = q,
                                selectedIndex = selectedMap["fin_${q.id}"],
                                isSubmitted = submittedMap["fin_${q.id}"] == true,
                                onSelectOption = { selectedMap["fin_${q.id}"] = it },
                                onSubmit = {
                                    val c = selectedMap["fin_${q.id}"] ?: return@QuestionInteractiveCard
                                    submittedMap["fin_${q.id}"] = true
                                    onAnswerQuestion(q, c, StudyMode.AULA)
                                },
                                onAskTutor = { onAskTutor(q) }
                            )
                        }
                    }
                }

                item {
                    val allFinalDone = finalQuestions.all { submittedMap["fin_${it.id}"] == true }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (step > 0) {
                            OutlinedButton(
                                onClick = { step-- },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Etapa Anterior")
                            }
                        }
                        if (step < stepTitles.lastIndex) {
                            Button(
                                onClick = { step++ },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 50.dp)
                                    .testTag("lesson_next_step_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text("Avançar")
                            }
                        } else {
                            Button(
                                onClick = {
                                    val correctCount = finalQuestions.count {
                                        selectedMap["fin_${it.id}"] == it.correctIndex
                                    }
                                    val pct = if (finalQuestions.isNotEmpty()) {
                                        (correctCount * 100L) / finalQuestions.size
                                    } else 100L
                                    onFinishLesson(pct, lesson.estimatedMinutes.toLong())
                                },
                                enabled = allFinalDone,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 50.dp)
                                    .testTag("lesson_finish_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text(if (allFinalDone) "Concluir Aula (+50 XP)" else "Responda a Avaliação")
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LessonSectionCard(
    title: String,
    badge: String,
    body: String,
    containerColor: Color
) {
    val cfo = CfoTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(shape = RoundedCornerShape(8.dp), color = cfo.softGreenSurface) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = cfo.titleGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = cfo.titleGreen
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = cfo.bodyText
            )
        }
    }
}
