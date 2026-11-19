package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.curriculum.CurriculumCatalog
import com.example.model.UiState
import com.example.ui.components.CfoTopBar
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.AiViewModel
import com.example.viewmodel.StudyDashboardData

@Composable
fun EssayScreen(
    data: StudyDashboardData,
    aiViewModel: AiViewModel,
    onBack: () -> Unit,
    onSaveEssay: (
        existingId: String?,
        themeTitle: String,
        draftText: String,
        rewrittenText: String,
        grammarErrors: List<String>,
        cohesionFeedback: String,
        improvementSuggestions: List<String>,
        formativeScore: Long,
        aiSummary: String
    ) -> Unit
) {
    val cfo = CfoTheme.colors
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Temas & Guia", "Escrever & Corrigir", "Salvas (${data.essays.size})")

    var selectedTheme by remember { mutableStateOf(CurriculumCatalog.allEssayThemes.first()) }
    var editingEssayId by remember { mutableStateOf<String?>(null) }
    var draftText by remember { mutableStateOf("") }
    var rewrittenText by remember { mutableStateOf("") }
    var showComparison by remember { mutableStateOf(false) }

    val evalState by aiViewModel.essayEvalState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Laboratório de Redação",
                subtitle = "Dissertativo-argumentativa • Padrão PAES/UEMA",
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
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(label, maxLines = 1) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
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
                                    Text("Estrutura Dissertativa de Alta Nota", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                    Text("• Introdução (5–7 linhas): Repertório legitimado + contextualização + tese com dois argumentos-chave.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                    Text("• Desenvolvimento 1 e 2 (7–9 linhas cada): Tópico frasal + fundamentação sociocultural + análise crítica conectada ao tema.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                    Text("• Conclusão (5–7 linhas): Retomada da tese + síntese propositiva realista (sem clichês vazios).", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                    Text("• Técnica de ouro: Nunca use gírias; prefira conectivos interparágrafos ('Sob essa ótica', 'Ademais', 'Portanto').", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                }
                            }
                        }

                        items(CurriculumCatalog.allEssayThemes, key = { it.id }) { theme ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable {
                                        selectedTheme = theme
                                        editingEssayId = null
                                        aiViewModel.clearEssayEval()
                                        selectedTab = 1
                                    },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                                border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(theme.title, style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                    Text(theme.contextText, style = MaterialTheme.typography.bodySmall, color = cfo.mutedText)
                                    Surface(shape = RoundedCornerShape(10.dp), color = cfo.slateBlueSurface) {
                                        Text(
                                            text = "Exemplo de Introdução:\n${theme.sampleIntro}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = cfo.bodyText,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                    Text("Esqueleto sugerido:", style = MaterialTheme.typography.labelLarge, color = cfo.titleGreen)
                                    theme.sampleOutline.forEach { step ->
                                        Text("• $step", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                    }
                                    Button(
                                        onClick = {
                                            selectedTheme = theme
                                            editingEssayId = null
                                            aiViewModel.clearEssayEval()
                                            selectedTab = 1
                                        },
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = cfo.buttonPrimary,
                                            contentColor = cfo.onButtonPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.EditNote, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Praticar Este Tema")
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        item {
                            Surface(shape = RoundedCornerShape(14.dp), color = cfo.goldSoftBg) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Proposta Ativa", style = MaterialTheme.typography.labelMedium, color = cfo.titleGreen)
                                    Text(selectedTheme.title, style = MaterialTheme.typography.titleMedium, color = cfo.bodyText)
                                    Text(selectedTheme.commandPrompt, style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = draftText,
                                onValueChange = { draftText = it },
                                label = { Text("1ª Versão (Rascunho)") },
                                placeholder = { Text("Redija sua dissertação completa aqui...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 180.dp)
                                    .testTag("essay_draft_input"),
                                minLines = 7
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { aiViewModel.evaluateEssay(selectedTheme.title, draftText) },
                                    enabled = evalState !is UiState.Loading,
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag("evaluate_essay_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Corrigir com IA")
                                }
                                OutlinedButton(
                                    onClick = { showComparison = !showComparison },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.CompareArrows, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (showComparison) "Ocultar Reescrita" else "Comparar Reescrita")
                                }
                            }
                        }

                        if (showComparison) {
                            item {
                                OutlinedTextField(
                                    value = rewrittenText,
                                    onValueChange = { rewrittenText = it },
                                    label = { Text("Versão Reescrita (Melhorada)") },
                                    placeholder = { Text("Reescreva aplicando as correções de gramática e coesão...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 160.dp)
                                        .testTag("essay_rewrite_input"),
                                    minLines = 6
                                )
                            }
                            if (draftText.isNotBlank() && rewrittenText.isNotBlank()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = cfo.slateBlueSurface)
                                    ) {
                                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("Comparação Antes x Depois", style = MaterialTheme.typography.labelLarge, color = cfo.titleGreen)
                                            Text("Antes: ${draftText.trim().split(Regex("\\s+")).size} palavras", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                            Text("Depois: ${rewrittenText.trim().split(Regex("\\s+")).size} palavras", style = MaterialTheme.typography.bodySmall, color = cfo.correctText, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        when (val eval = evalState) {
                            is UiState.Loading -> item {
                                Surface(shape = RoundedCornerShape(14.dp), color = cfo.softGreenSurface) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = cfo.titleGreen)
                                        Text("Analisando gramática, coesão e repertório...", color = cfo.bodyText)
                                    }
                                }
                            }
                            is UiState.Error -> item {
                                Surface(shape = RoundedCornerShape(14.dp), color = cfo.wrongBg) {
                                    Text(eval.message, color = cfo.wrongText, modifier = Modifier.padding(14.dp))
                                }
                            }
                            is UiState.Success -> item {
                                val res = eval.data
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                                    border = BorderStroke(1.5.dp, cfo.correctText)
                                ) {
                                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Feedback da IA (Orientativo)", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                                            Surface(shape = RoundedCornerShape(8.dp), color = cfo.correctBg) {
                                                Text(
                                                    text = "Nota formativa: ${res.formativeScore}/1000",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = cfo.correctText,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        Text(res.aiSummary, style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                        Text("Coesão e Coerência: ${res.cohesionFeedback}", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                        if (res.grammarErrors.isNotEmpty()) {
                                            Text("Desvios gramaticais:", style = MaterialTheme.typography.labelLarge, color = cfo.wrongText)
                                            res.grammarErrors.forEach { err ->
                                                Text("• $err", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                            }
                                        }
                                        if (res.improvementSuggestions.isNotEmpty()) {
                                            Text("Sugestões de melhoria:", style = MaterialTheme.typography.labelLarge, color = cfo.titleGreen)
                                            res.improvementSuggestions.forEach { s ->
                                                Text("• $s", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                            }
                                        }
                                        if (res.rewrittenSuggestion.isNotBlank()) {
                                            Surface(shape = RoundedCornerShape(10.dp), color = cfo.softGreenSurface) {
                                                Text(
                                                    text = "Sugestão de reescrita:\n${res.rewrittenSuggestion}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = cfo.bodyText,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                        }
                                        Button(
                                            onClick = {
                                                onSaveEssay(
                                                    editingEssayId,
                                                    selectedTheme.title,
                                                    draftText,
                                                    rewrittenText.ifBlank { res.rewrittenSuggestion },
                                                    res.grammarErrors,
                                                    res.cohesionFeedback,
                                                    res.improvementSuggestions,
                                                    res.formativeScore,
                                                    res.aiSummary
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = cfo.buttonPrimary,
                                                contentColor = cfo.onButtonPrimary
                                            )
                                        ) {
                                            Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Salvar Redação + Correção")
                                        }
                                    }
                                }
                            }
                            null -> {}
                        }

                        if (evalState !is UiState.Success && draftText.isNotBlank()) {
                            item {
                                OutlinedButton(
                                    onClick = {
                                        onSaveEssay(
                                            editingEssayId,
                                            selectedTheme.title,
                                            draftText,
                                            rewrittenText,
                                            emptyList(),
                                            "",
                                            emptyList(),
                                            0L,
                                            "Rascunho salvo pelo candidato."
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Salvar Rascunho na Nuvem")
                                }
                            }
                        }
                    }

                    2 -> {
                        if (data.essays.isEmpty()) {
                            item {
                                Surface(shape = RoundedCornerShape(16.dp), color = cfo.softGreenSurface) {
                                    Text(
                                        text = "Nenhuma redação salva ainda. Escolha um tema e envie para correção ou salve seu rascunho.",
                                        color = cfo.bodyText,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        } else {
                            items(data.essays, key = { it.id.ifBlank { it.themeTitle + it.draftText.take(20) } }) { essay ->
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
                                            Text(essay.themeTitle, style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen, modifier = Modifier.weight(1f))
                                            if (essay.formativeScore > 0L) {
                                                Surface(shape = RoundedCornerShape(8.dp), color = cfo.goldSoftBg) {
                                                    Text(
                                                        text = "${essay.formativeScore}/1000",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = cfo.bodyText,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(essay.draftText.take(220), style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                                        if (essay.aiSummary.isNotBlank()) {
                                            Text(essay.aiSummary, style = MaterialTheme.typography.labelSmall, color = cfo.mutedText)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                editingEssayId = essay.id.ifBlank { null }
                                                draftText = essay.draftText
                                                rewrittenText = essay.rewrittenText
                                                showComparison = essay.rewrittenText.isNotBlank()
                                                selectedTab = 1
                                            },
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        ) {
                                            Text("Reabrir e Reescrever")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
