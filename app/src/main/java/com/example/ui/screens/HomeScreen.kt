package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.repository.SpacedRepetitionEngine
import com.example.ui.components.MetricPill
import com.example.ui.components.SmoothProgressBar
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.StudyDashboardData
import java.util.Calendar

@Composable
fun HomeScreen(
    data: StudyDashboardData,
    onOpenModoAula: () -> Unit,
    onOpenModoRapido: () -> Unit,
    onOpenModoConstancia: () -> Unit,
    onOpenRevisoes: () -> Unit,
    onOpenDiagnostico: () -> Unit,
    onOpenSimulados: () -> Unit,
    onOpenCadernoErros: () -> Unit,
    onOpenFlashcards: () -> Unit,
    onOpenEstatisticas: () -> Unit,
    onOpenTutorIa: () -> Unit,
    onOpenRedacao: () -> Unit,
    onOpenPlanoEstudos: () -> Unit,
    onOpenConfiguracoes: () -> Unit
) {
    val cfo = CfoTheme.colors
    val profile = data.profile
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Bom dia"
        hour < 18 -> "Boa tarde"
        else -> "Boa noite"
    }
    val (xpInLevel, xpNeeded) = SpacedRepetitionEngine.xpProgressInCurrentLevel(profile.xp)
    val dailyProgress = (data.todayMinutesStudied.toFloat() / profile.dailyGoalMinutes.coerceAtLeast(1L).toFloat())
        .coerceIn(0f, 1f)
    val rec = data.constancyRecommendation

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Cabeçalho
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$greeting, ${profile.displayName}!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = cfo.titleGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Missão CFO MA • Foco UEMA/PAES",
                            style = MaterialTheme.typography.bodySmall,
                            color = cfo.mutedText
                        )
                    }
                    IconButton(
                        onClick = onOpenConfiguracoes,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações, Tema, Sons e Backup",
                            tint = cfo.titleGreen
                        )
                    }
                }
            }

            // 2. Hero Banner com nova imagem (Amanhecer no Modo Dia / Anoitecer no Modo Noite) + camada de contraste
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                    border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.55f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(182.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            Crossfade(targetState = cfo.isDark, label = "homeHeroCrossfade") { dark ->
                                Image(
                                    painter = painterResource(
                                        id = if (dark) R.drawable.img_cadet_night_1795082521593
                                        else R.drawable.img_cadet_dawn_1795082521496
                                    ),
                                    contentDescription = "Cadete observando o campo da academia militar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Camada de contraste sutil para garantir legibilidade sobre a fotografia
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x55141C17),
                                                Color(0x28141C17),
                                                Color(0xC8141C17)
                                            )
                                        )
                                    )
                            )

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xCC243028)
                            ) {
                                Text(
                                    text = "Nível ${profile.level} • ${profile.xp} XP ($xpInLevel/$xpNeeded)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFF7F6F0),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Rumo ao Oficialato PMMA",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFF7F6F0),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Constância diária constrói sua aprovação no PAES/UEMA.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE9E2D2)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricPill(
                                icon = Icons.Default.LocalFireDepartment,
                                label = "Sequência",
                                value = "${profile.streakDays} dias",
                                containerColor = cfo.goldSoftBg,
                                contentColor = cfo.bodyText,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                icon = Icons.Default.Star,
                                label = "Meta diária",
                                value = "${data.todayMinutesStudied}/${profile.dailyGoalMinutes} min",
                                containerColor = cfo.softGreenSurface,
                                contentColor = cfo.bodyText,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progresso no Edital PAES/UEMA",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = cfo.titleGreen
                                )
                                Text(
                                    text = "${data.overallEditalProgressPercent}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = cfo.titleGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            SmoothProgressBar(
                                progress = data.overallEditalProgressPercent / 100f,
                                trackColor = cfo.sandSurface,
                                indicatorColor = cfo.buttonPrimary
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Meta de hoje",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = cfo.mutedText
                                )
                                Text(
                                    text = "${(dailyProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = cfo.titleGreen
                                )
                            }
                            SmoothProgressBar(
                                progress = dailyProgress,
                                trackColor = cfo.slateBlueSurface,
                                indicatorColor = cfo.oliveAccent
                            )
                        }
                    }
                }
            }

            // 3. MODO CONSTÂNCIA
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_constancy_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface),
                    border = BorderStroke(1.5.dp, cfo.borderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cfo.goldHighlight
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Modo Constância",
                                    tint = Color(0xFF1F2821),
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MODO CONSTÂNCIA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = cfo.titleGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = rec.headline,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = cfo.bodyText
                                )
                            }
                        }

                        Text(
                            text = rec.details,
                            style = MaterialTheme.typography.bodySmall,
                            color = cfo.bodyText
                        )

                        if (rec.sleepProtectionNotice != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cfo.warningBg
                            ) {
                                Text(
                                    text = rec.sleepProtectionNotice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.bodyText,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = onOpenModoConstancia,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("home_constancy_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = cfo.buttonPrimary,
                                contentColor = cfo.onButtonPrimary
                            )
                        ) {
                            Text("Estudar Agora (Leve • Moderado • Completo)")
                        }
                    }
                }
            }

            // 4. Trilhas Principais
            item {
                Text(
                    text = "Trilhas de Estudo",
                    style = MaterialTheme.typography.titleLarge,
                    color = cfo.titleGreen
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeModuleCard(
                        title = "Modo Aula",
                        subtitle = "${data.completedLessonsCount}/${CurriculumCatalog.allLessons.size} aulas",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        containerColor = cfo.cardSurface,
                        badgeText = "Português & Mat +",
                        onClick = onOpenModoAula,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_modo_aula_button")
                    )
                    HomeModuleCard(
                        title = "Modo Rápido",
                        subtitle = "Missões de 2–5 min",
                        icon = Icons.Default.Bolt,
                        containerColor = cfo.slateBlueSurface,
                        badgeText = "XP Compartilhado",
                        onClick = onOpenModoRapido,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_modo_rapido_button")
                    )
                }
            }

            // 5. Memória, Diagnóstico e Erros
            item {
                Text(
                    text = "Memória, Diagnóstico e Erros",
                    style = MaterialTheme.typography.titleLarge,
                    color = cfo.titleGreen
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeModuleCard(
                        title = "Revisões Pendentes",
                        subtitle = if (data.pendingReviews.isEmpty()) "Tudo em dia!" else "${data.pendingReviews.size} para revisar hoje",
                        icon = Icons.Default.Replay,
                        containerColor = if (data.pendingReviews.isNotEmpty()) cfo.goldSoftBg else cfo.softGreenSurface,
                        badgeText = "SRS 1-2-7-15-30d",
                        onClick = onOpenRevisoes,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_revisoes_button")
                    )
                    HomeModuleCard(
                        title = "Caderno de Erros",
                        subtitle = "${data.activeErrors.size} ativos • ${data.masteredErrorsCount} superados",
                        icon = Icons.Default.ErrorOutline,
                        containerColor = cfo.cardSurface,
                        badgeText = "Refazer & Evoluir",
                        onClick = onOpenCadernoErros,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_caderno_erros_button")
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeModuleCard(
                        title = "Flashcards",
                        subtitle = "${data.pendingFlashcards.size} pendentes agora",
                        icon = Icons.Default.Style,
                        containerColor = cfo.softGreenSurface,
                        badgeText = "Autoavaliação",
                        onClick = onOpenFlashcards,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_flashcards_button")
                    )
                    HomeModuleCard(
                        title = "Diagnóstico Inicial",
                        subtitle = if (profile.diagnosticCompleted) "${data.diagnostics.size} áreas avaliadas" else "Mapeie forças e falhas",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        containerColor = cfo.slateBlueSurface,
                        badgeText = "Personaliza o Plano",
                        onClick = onOpenDiagnostico,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_diagnostico_button")
                    )
                }
            }

            // 6. Simulados, Redação, Tutor IA, Estatísticas e Plano
            item {
                Text(
                    text = "Prática Avançada e Inteligência",
                    style = MaterialTheme.typography.titleLarge,
                    color = cfo.titleGreen
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeModuleCard(
                        title = "Simulados",
                        subtitle = "${data.simulatedExams.size} concluídos",
                        icon = Icons.Default.Quiz,
                        containerColor = cfo.cardSurface,
                        badgeText = "Com Cronômetro",
                        onClick = onOpenSimulados,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_simulados_button")
                    )
                    HomeModuleCard(
                        title = "Redação PAES",
                        subtitle = "${data.essays.size} textos salvos",
                        icon = Icons.Default.EditNote,
                        containerColor = cfo.sandSurface,
                        badgeText = "Correção + Comparação",
                        onClick = onOpenRedacao,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_redacao_button")
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeModuleCard(
                        title = "Tutor IA",
                        subtitle = "Professor CFO MA",
                        icon = Icons.Default.AutoAwesome,
                        containerColor = cfo.softGreenSurface,
                        badgeText = "Passo a Passo",
                        onClick = onOpenTutorIa,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_tutor_ia_button")
                    )
                    HomeModuleCard(
                        title = "Estatísticas",
                        subtitle = "Gráfico Radar & XP",
                        icon = Icons.Default.Analytics,
                        containerColor = cfo.slateBlueSurface,
                        badgeText = "${data.achievements.size} medalhas",
                        onClick = onOpenEstatisticas,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_estatisticas_button")
                    )
                }
            }

            item {
                HomeModuleCard(
                    title = "Plano de Estudos Personalizado",
                    subtitle = "Escala ${profile.workScheduleEnum().label} • Meta ${profile.dailyGoalMinutes} min/dia • Foco adaptado às suas dificuldades",
                    icon = Icons.Default.CalendarMonth,
                    containerColor = cfo.cardSurface,
                    badgeText = "Ajustar Rotina",
                    onClick = onOpenPlanoEstudos,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_plano_button")
                )
            }

            // 7. Transparência sobre edital e fontes
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = cfo.sandSurface,
                    border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Nota de referência do edital",
                            tint = cfo.titleGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = CurriculumCatalog.EDITAL_TRANSPARENCY_NOTE,
                            style = MaterialTheme.typography.bodySmall,
                            color = cfo.bodyText
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun HomeModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    badgeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cfo = CfoTheme.colors
    Card(
        modifier = modifier
            .heightIn(min = 120.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = cfo.titleGreen.copy(alpha = 0.14f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = cfo.titleGreen,
                        modifier = Modifier
                            .padding(7.dp)
                            .size(20.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = cfo.goldHighlight.copy(alpha = if (cfo.isDark) 0.28f else 0.45f)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = cfo.bodyText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = cfo.titleGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = cfo.mutedText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
