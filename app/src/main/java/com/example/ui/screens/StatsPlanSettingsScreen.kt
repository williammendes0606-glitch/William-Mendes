package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.repository.SpacedRepetitionEngine
import com.example.model.FatigueLevel
import com.example.model.MasteryStatus
import com.example.model.WorkSchedule
import com.example.ui.components.CfoTopBar
import com.example.ui.components.ChartBarPoint
import com.example.ui.components.MetricPill
import com.example.ui.components.SubjectRadarChart
import com.example.ui.components.WeeklyEvolutionChart
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CfoTheme
import com.example.viewmodel.StudyDashboardData
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun StatisticsAndBadgesScreen(
    data: StudyDashboardData,
    onBack: (() -> Unit)? = null
) {
    val cfo = CfoTheme.colors
    val profile = data.profile
    val totalHours = String.format("%.1f h", profile.totalStudyMinutes / 60.0)
    val accuracy = if (profile.totalQuestionsAnswered > 0L) {
        ((profile.totalQuestionsCorrect * 100L) / profile.totalQuestionsAnswered).toInt()
    } else 0

    val weeklyPoints = remember(data.questionLogs) {
        val cal = Calendar.getInstance()
        val fmt = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
        (6 downTo 0).map { offset ->
            val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val key = SpacedRepetitionEngine.dayKey(c.timeInMillis)
            val count = data.questionLogs.count { it.dayKey == key }.toFloat()
            ChartBarPoint(label = fmt.format(c.time), value = count)
        }
    }

    val radarAxes = remember(data.questionLogs) {
        listOf("portugues", "matematica", "historia", "geografia", "fisica", "biologia").map { id ->
            val subj = CurriculumCatalog.subjectById(id)
            val logs = data.questionLogs.filter { it.subjectId == id }
            val acc = if (logs.isEmpty()) 0.15f
            else (logs.count { it.correct }.toFloat() / logs.size.toFloat()).coerceIn(0.12f, 1f)
            (subj?.shortName ?: id) to acc
        }
    }

    val masteredCount = data.reviewItems.count { it.statusEnum() == MasteryStatus.DOMINADO }
    val pendingCount = data.pendingReviews.size
    val unlockedIds = remember(data.achievements) { data.achievements.map { it.badgeId }.toSet() }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Estatísticas & Conquistas",
                subtitle = "Nível ${profile.level} • ${profile.xp} XP total",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            icon = Icons.Default.Schedule,
                            label = "Horas estudadas",
                            value = totalHours,
                            containerColor = cfo.softGreenSurface,
                            contentColor = cfo.bodyText,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.EmojiEvents,
                            label = "Taxa de acerto",
                            value = "$accuracy%",
                            containerColor = cfo.slateBlueSurface,
                            contentColor = cfo.bodyText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Evolução nos Últimos 7 Dias (Questões)", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            WeeklyEvolutionChart(points = weeklyPoints)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Gráfico Radar por Disciplina", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            Text(
                                text = "$masteredCount tópicos dominados • $pendingCount revisões pendentes • ${data.simulatedExams.size} simulados",
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.mutedText
                            )
                            SubjectRadarChart(axes = radarAxes)
                        }
                    }
                }

                item {
                    Text(
                        text = "Medalhas e Gamificação (${unlockedIds.size}/${CurriculumCatalog.allBadges.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = cfo.titleGreen
                    )
                }

                items(CurriculumCatalog.allBadges, key = { it.id }) { badge ->
                    val unlocked = badge.id in unlockedIds
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (unlocked) cfo.goldSoftBg else cfo.cardSurface
                        ),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = badge.title,
                                tint = if (unlocked) cfo.titleGreen else cfo.mutedText,
                                modifier = Modifier.size(28.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(badge.title, style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                                Text(badge.description, style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (unlocked) cfo.correctBg else cfo.softGreenSurface
                            ) {
                                Text(
                                    text = if (unlocked) "Conquistada" else "+${badge.xpBonus} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (unlocked) cfo.correctText else cfo.titleGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
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
fun StudyPlanScreen(
    data: StudyDashboardData,
    onBack: () -> Unit,
    onSavePlan: (
        displayName: String,
        dailyGoalMinutes: Long,
        weeklyGoalDays: Long,
        workSchedule: WorkSchedule,
        fatigueLevel: FatigueLevel,
        sleepHours: Long,
        soundEnabled: Boolean,
        soundVolume: Double,
        focusSubjects: List<String>
    ) -> Unit
) {
    val cfo = CfoTheme.colors
    val profile = data.profile
    var dailyMins by remember(profile.dailyGoalMinutes) { mutableIntStateOf(profile.dailyGoalMinutes.toInt()) }
    var weeklyDays by remember(profile.weeklyGoalDays) { mutableIntStateOf(profile.weeklyGoalDays.toInt()) }
    var schedule by remember(profile.workSchedule) { mutableStateOf(profile.workScheduleEnum()) }
    var fatigue by remember(profile.fatigueLevel) { mutableStateOf(profile.fatigueEnum()) }
    var sleepHours by remember(profile.sleepHours) { mutableIntStateOf(profile.sleepHours.toInt()) }
    val selectedFocus = remember(profile.focusSubjects) {
        mutableStateListOf<String>().apply { addAll(profile.focusSubjects) }
    }

    val diagnosticWeak = data.diagnostics.filter { it.needsReview }.map { it.subjectName }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Plano de Estudos Personalizado",
                subtitle = "Adaptado ao seu trabalho, sono e dificuldades",
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
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Tempo disponível por dia: $dailyMins min", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            Slider(
                                value = dailyMins.toFloat(),
                                onValueChange = { dailyMins = (it / 5f).roundToInt() * 5 },
                                valueRange = 10f..180f
                            )

                            Text("Dias de estudo na semana: $weeklyDays dias", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            Slider(
                                value = weeklyDays.toFloat(),
                                onValueChange = { weeklyDays = it.roundToInt() },
                                valueRange = 1f..7f,
                                steps = 5
                            )

                            Text("Escala / Rotina de Trabalho", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                WorkSchedule.entries.forEach { ws ->
                                    FilterChip(
                                        selected = schedule == ws,
                                        onClick = { schedule = ws },
                                        label = { Text(ws.label) }
                                    )
                                }
                            }
                            Text(schedule.hint, style = MaterialTheme.typography.bodySmall, color = cfo.mutedText)

                            Text("Nível de cansaço atual", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FatigueLevel.entries.forEach { f ->
                                    FilterChip(
                                        selected = fatigue == f,
                                        onClick = { fatigue = f },
                                        label = { Text(f.label) }
                                    )
                                }
                            }

                            Text("Média de sono: ${sleepHours}h/noite", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            Slider(
                                value = sleepHours.toFloat(),
                                onValueChange = { sleepHours = it.roundToInt() },
                                valueRange = 3f..10f,
                                steps = 6
                            )

                            Text("Matérias Prioritárias (maiores dificuldades)", style = MaterialTheme.typography.titleSmall, color = cfo.titleGreen)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CurriculumCatalog.allSubjects.forEach { subj ->
                                    val active = subj.id in selectedFocus
                                    FilterChip(
                                        selected = active,
                                        onClick = {
                                            if (active && selectedFocus.size > 1) selectedFocus.remove(subj.id)
                                            else if (!active) selectedFocus.add(subj.id)
                                        },
                                        label = { Text(subj.shortName) }
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    onSavePlan(
                                        profile.displayName,
                                        dailyMins.toLong(),
                                        weeklyDays.toLong(),
                                        schedule,
                                        fatigue,
                                        sleepHours.toLong(),
                                        profile.soundEnabled,
                                        profile.soundVolume,
                                        selectedFocus.toList()
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("save_study_plan_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text("Salvar Plano Personalizado")
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
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Roteiro Sugerido para Esta Semana", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            val focusNames = selectedFocus.mapNotNull { CurriculumCatalog.subjectById(it)?.shortName }
                                .joinToString(" e ").ifBlank { "Português e Matemática" }
                            Text("• Prioridade máxima: $focusNames (50% do tempo diário).", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            if (diagnosticWeak.isNotEmpty()) {
                                Text("• Alerta do Diagnóstico: Reforçar ${diagnosticWeak.joinToString()}.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            }
                            Text("• Revisões pendentes hoje: ${data.pendingReviews.size} tópicos.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            Text("• Blocos de $dailyMins min divididos em teoria + fixação + revisão espaçada.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsBackupApkScreen(
    data: StudyDashboardData,
    themeMode: AppThemeMode,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onBack: () -> Unit,
    onSaveSettings: (
        displayName: String,
        dailyGoalMinutes: Long,
        weeklyGoalDays: Long,
        workSchedule: WorkSchedule,
        fatigueLevel: FatigueLevel,
        sleepHours: Long,
        soundEnabled: Boolean,
        soundVolume: Double,
        focusSubjects: List<String>
    ) -> Unit,
    onTestSound: () -> Unit,
    onPrepareBackupJson: ((String) -> Unit) -> Unit,
    onImportBackupJson: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val cfo = CfoTheme.colors
    val context = LocalContext.current
    val profile = data.profile
    var displayName by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var soundEnabled by remember(profile.soundEnabled) { mutableStateOf(profile.soundEnabled) }
    var soundVolume by remember(profile.soundVolume) { mutableFloatStateOf(profile.soundVolume.toFloat()) }
    var pendingJsonToWrite by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = pendingJsonToWrite
        if (uri != null && json != null) {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(json.toByteArray(Charsets.UTF_8))
            }
        }
        pendingJsonToWrite = null
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val raw = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
                it.readText()
            }
            if (!raw.isNullOrBlank()) {
                onImportBackupJson(raw)
            }
        }
    }

    Scaffold(
        topBar = {
            CfoTopBar(
                title = "Configurações, Tema & Backup",
                subtitle = "Aparência, efeitos sonoros, persistência e APK",
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
                // Cartão de Aparência: Automático, Modo Dia e Modo Noite
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appearance_settings_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.55f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Aparência (Modo Dia e Modo Noite)",
                                style = MaterialTheme.typography.titleMedium,
                                color = cfo.titleGreen
                            )
                            Text(
                                text = "Sua escolha é salva automaticamente no aparelho e aplicada a todas as telas do aplicativo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.mutedText
                            )

                            AppThemeMode.entries.forEach { mode ->
                                val selected = themeMode == mode
                                val icon = when (mode) {
                                    AppThemeMode.AUTO -> Icons.Default.BrightnessAuto
                                    AppThemeMode.DAY -> Icons.Default.LightMode
                                    AppThemeMode.NIGHT -> Icons.Default.DarkMode
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (selected) cfo.softGreenSurface else MaterialTheme.colorScheme.background)
                                        .border(
                                            width = if (selected) 1.5.dp else 1.dp,
                                            color = if (selected) cfo.buttonPrimary else cfo.borderSubtle.copy(alpha = 0.45f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onSelectThemeMode(mode) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("theme_option_${mode.name}"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = { onSelectThemeMode(mode) }
                                    )
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = mode.label,
                                        tint = cfo.titleGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mode.label,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = cfo.titleGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = mode.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = cfo.mutedText
                                        )
                                    }
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selecionado",
                                            tint = cfo.correctText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Perfil e Efeitos Sonoros", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = { Text("Nome do Candidato") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = cfo.titleGreen)
                                    Text("Efeitos sonoros locais (originais)", style = MaterialTheme.typography.bodyMedium, color = cfo.bodyText)
                                }
                                Switch(
                                    checked = soundEnabled,
                                    onCheckedChange = { soundEnabled = it },
                                    modifier = Modifier.testTag("sound_enabled_switch")
                                )
                            }
                            Text("Volume dos sons: ${(soundVolume * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium, color = cfo.mutedText)
                            Slider(
                                value = soundVolume,
                                onValueChange = { soundVolume = it },
                                valueRange = 0f..1f
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onTestSound,
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                ) {
                                    Text("Testar Som")
                                }
                                Button(
                                    onClick = {
                                        onSaveSettings(
                                            displayName,
                                            profile.dailyGoalMinutes,
                                            profile.weeklyGoalDays,
                                            profile.workScheduleEnum(),
                                            profile.fatigueEnum(),
                                            profile.sleepHours,
                                            soundEnabled,
                                            soundVolume.toDouble(),
                                            profile.focusSubjects
                                        )
                                    },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Text("Salvar")
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.softGreenSurface),
                        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Backup e Restauração do Progresso", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            Text(
                                text = "Além da sincronização automática no Firebase Firestore, você pode exportar uma cópia local em arquivo .json para guardar ou restaurar depois (sem precisar de permissão ampla de armazenamento).",
                                style = MaterialTheme.typography.bodySmall,
                                color = cfo.bodyText
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onPrepareBackupJson { json ->
                                            pendingJsonToWrite = json
                                            exportLauncher.launch("missao_cfo_ma_backup.json")
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag("export_backup_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cfo.buttonPrimary,
                                        contentColor = cfo.onButtonPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.Backup, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Exportar JSON")
                                }
                                OutlinedButton(
                                    onClick = { importLauncher.launch(arrayOf("application/json")) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag("import_backup_button")
                                ) {
                                    Icon(Icons.Default.Restore, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Importar JSON")
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cfo.slateBlueSurface)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Build, null, tint = cfo.titleGreen)
                                Text("Exportação ZIP & Compilação APK", style = MaterialTheme.typography.titleMedium, color = cfo.titleGreen)
                            }
                            Text("• Código-fonte (ZIP): Use o menu de exportação/GitHub do AI Studio para baixar todo o projeto Android completo.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            Text("• APK de Teste (Debug): No AI Studio (menu Gerar APK) ou localmente com 'gradle :app:assembleDebug'.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            Text("• APK Assinado (Release): Configure sua keystore de publicação e execute 'gradle :app:assembleRelease'.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                            Text("• Serviços integrados: Firebase Auth (Google Sign-In), Cloud Firestore e Firebase AI Logic.", style = MaterialTheme.typography.bodySmall, color = cfo.bodyText)
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("sign_out_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sair da Conta Google")
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
