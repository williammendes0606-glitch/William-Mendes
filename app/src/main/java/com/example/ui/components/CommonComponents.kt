package com.example.ui.components

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.QuestionOrigin
import com.example.ui.theme.CfoTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CfoTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = CfoTheme.colors.titleGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("top_bar_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = CfoTheme.colors.titleGreen
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = CfoTheme.colors.titleGreen
        )
    )
}

@Composable
fun MetricPill(
    icon: ImageVector,
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color = CfoTheme.colors.bodyText,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, CfoTheme.colors.borderSubtle.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CfoTheme.colors.titleGreen,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = CfoTheme.colors.mutedText
                )
            }
        }
    }
}

@Composable
fun SmoothProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = CfoTheme.colors.softGreenSurface,
    indicatorColor: Color = CfoTheme.colors.buttonPrimary
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "progressAnim"
    )
    LinearProgressIndicator(
        progress = { animated },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = indicatorColor,
        trackColor = trackColor
    )
}

@Composable
fun OriginAndDifficultyBadges(
    origin: QuestionOrigin,
    difficulty: QuestionDifficulty
) {
    val cfo = CfoTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (bg, icon) = when (origin) {
            QuestionOrigin.IA_TREINO -> cfo.softGreenSurface to Icons.Default.AutoAwesome
            QuestionOrigin.PADRAO_PAES_UEMA -> cfo.slateBlueSurface to Icons.Default.Verified
            QuestionOrigin.REFERENCIA_PAES_VERIFICADA -> cfo.goldSoftBg to Icons.Default.Verified
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = bg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = origin.badgeLabel,
                    tint = cfo.titleGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = origin.badgeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = cfo.bodyText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = cfo.goldSoftBg
        ) {
            Text(
                text = difficulty.label,
                style = MaterialTheme.typography.labelSmall,
                color = cfo.bodyText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun QuestionInteractiveCard(
    question: QuestionItem,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    onSelectOption: (Int) -> Unit,
    onSubmit: () -> Unit,
    onAskTutor: (() -> Unit)? = null,
    nextButtonLabel: String? = null,
    onNext: (() -> Unit)? = null
) {
    val letters = listOf("A", "B", "C", "D", "E")
    val cfo = CfoTheme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cfo.cardSurface),
        border = BorderStroke(1.dp, cfo.borderSubtle.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OriginAndDifficultyBadges(origin = question.origin, difficulty = question.difficulty)

            Text(
                text = "${question.subjectName} • ${question.topicName}",
                style = MaterialTheme.typography.labelMedium,
                color = cfo.mutedText
            )

            Text(
                text = question.statement,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = cfo.bodyText
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { idx, optionText ->
                    val isSelected = selectedIndex == idx
                    val isCorrect = idx == question.correctIndex
                    val bgColor = when {
                        !isSubmitted && isSelected -> cfo.slateBlueSurface
                        isSubmitted && isCorrect -> cfo.correctBg
                        isSubmitted && isSelected && !isCorrect -> cfo.wrongBg
                        else -> MaterialTheme.colorScheme.background
                    }
                    val borderColor = when {
                        !isSubmitted && isSelected -> cfo.buttonPrimary
                        isSubmitted && isCorrect -> cfo.correctText
                        isSubmitted && isSelected && !isCorrect -> cfo.wrongText
                        else -> cfo.borderSubtle.copy(alpha = 0.5f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isSubmitted) { onSelectOption(idx) }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .testTag("question_option_$idx"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val badgeActive = isSelected || (isSubmitted && isCorrect)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (badgeActive) borderColor
                                    else cfo.softGreenSurface
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letters.getOrElse(idx) { "${idx + 1}" },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (badgeActive) cfo.onButtonPrimary else cfo.titleGreen
                            )
                        }
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            color = cfo.bodyText
                        )
                        if (isSubmitted && isCorrect) {
                            Icon(Icons.Default.CheckCircle, "Correta", tint = cfo.correctText, modifier = Modifier.size(20.dp))
                        } else if (isSubmitted && isSelected && !isCorrect) {
                            Icon(Icons.Default.Close, "Incorreta", tint = cfo.wrongText, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            if (!isSubmitted) {
                Button(
                    onClick = onSubmit,
                    enabled = selectedIndex != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("submit_answer_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cfo.buttonPrimary,
                        contentColor = cfo.onButtonPrimary
                    )
                ) {
                    Text("Confirmar Resposta")
                }
            }

            AnimatedVisibility(visible = isSubmitted) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val userRight = selectedIndex == question.correctIndex
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (userRight) cfo.correctBg else cfo.wrongBg
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (userRight) "Resposta correta! Excelente." else "Resposta incorreta — salva no Caderno de Erros para revisão hoje.",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (userRight) cfo.correctText else cfo.wrongText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = question.generalExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = cfo.bodyText
                            )
                            if (question.optionExplanations.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Análise por alternativa:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = cfo.titleGreen
                                )
                                question.optionExplanations.forEachIndexed { idx, exp ->
                                    Text(
                                        text = "${letters.getOrElse(idx) { "${idx + 1}" }}) $exp",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = cfo.bodyText
                                    )
                                }
                            }
                        }
                    }

                    if (question.examTrick.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = cfo.goldSoftBg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Macete de prova",
                                    tint = cfo.titleGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Macete: ${question.examTrick}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cfo.bodyText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Text(
                        text = "Fonte: ${question.sourceDetail}",
                        style = MaterialTheme.typography.labelSmall,
                        color = cfo.mutedText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onAskTutor != null) {
                            OutlinedButton(
                                onClick = onAskTutor,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("ask_tutor_question_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Tutor IA", maxLines = 1)
                            }
                        }
                        if (onNext != null && nextButtonLabel != null) {
                            Button(
                                onClick = onNext,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("next_question_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cfo.buttonPrimary,
                                    contentColor = cfo.onButtonPrimary
                                )
                            ) {
                                Text(nextButtonLabel, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ChartBarPoint(val label: String, val value: Float)

@Composable
fun WeeklyEvolutionChart(
    points: List<ChartBarPoint>,
    modifier: Modifier = Modifier
) {
    val cfo = CfoTheme.colors
    val barColor = cfo.buttonPrimary
    val trackColor = cfo.softGreenSurface
    val labelColor = cfo.bodyText.toArgb()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(top = 8.dp)
    ) {
        if (points.isEmpty()) return@Canvas
        val maxVal = (points.maxOfOrNull { it.value } ?: 10f).coerceAtLeast(10f)
        val bottomPad = 36f
        val topPad = 24f
        val chartHeight = (size.height - bottomPad - topPad).coerceAtLeast(20f)
        val slotW = size.width / points.size
        val barW = (slotW * 0.48f).coerceAtMost(44f)

        val textPaint = Paint().apply {
            color = labelColor
            textAlign = Paint.Align.CENTER
            textSize = 26f
            isAntiAlias = true
        }

        points.forEachIndexed { idx, pt ->
            val cx = slotW * idx + slotW / 2f
            val left = cx - barW / 2f
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(left, topPad),
                size = androidx.compose.ui.geometry.Size(barW, chartHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
            val ratio = (pt.value / maxVal).coerceIn(0f, 1f)
            val fillH = (chartHeight * ratio).coerceAtLeast(if (pt.value > 0f) 10f else 0f)
            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, topPad + (chartHeight - fillH)),
                size = androidx.compose.ui.geometry.Size(barW, fillH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
            drawContext.canvas.nativeCanvas.drawText(
                pt.label,
                cx,
                size.height - 6f,
                textPaint
            )
            if (pt.value > 0f) {
                drawContext.canvas.nativeCanvas.drawText(
                    pt.value.toInt().toString(),
                    cx,
                    topPad + (chartHeight - fillH) - 6f,
                    textPaint
                )
            }
        }
    }
}

@Composable
fun SubjectRadarChart(
    axes: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val cfo = CfoTheme.colors
    val gridColor = cfo.borderSubtle.copy(alpha = 0.7f)
    val fillColor = cfo.buttonPrimary.copy(alpha = 0.28f)
    val strokeColor = cfo.buttonPrimary
    val labelPaintColor = cfo.bodyText.toArgb()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        if (axes.size < 3) return@Canvas
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = (minOf(size.width, size.height) / 2f) * 0.68f
        val count = axes.size
        val angleStep = (2.0 * PI) / count

        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { factor ->
            val ringPath = Path()
            for (i in 0 until count) {
                val angle = -PI / 2.0 + i * angleStep
                val x = cx + (radius * factor * cos(angle)).toFloat()
                val y = cy + (radius * factor * sin(angle)).toFloat()
                if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
            }
            ringPath.close()
            drawPath(ringPath, color = gridColor, style = Stroke(width = 1.5f))
        }

        val textPaint = Paint().apply {
            color = labelPaintColor
            textSize = 25f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        axes.forEachIndexed { i, (label, _) ->
            val angle = -PI / 2.0 + i * angleStep
            val ex = cx + (radius * cos(angle)).toFloat()
            val ey = cy + (radius * sin(angle)).toFloat()
            drawLine(gridColor, Offset(cx, cy), Offset(ex, ey), strokeWidth = 1.5f)

            val lx = cx + ((radius + 32f) * cos(angle)).toFloat()
            val ly = cy + ((radius + 26f) * sin(angle)).toFloat() + 8f
            drawContext.canvas.nativeCanvas.drawText(label, lx, ly, textPaint)
        }

        val dataPath = Path()
        val vertices = mutableListOf<Offset>()
        axes.forEachIndexed { i, (_, value) ->
            val v = value.coerceIn(0.08f, 1f)
            val angle = -PI / 2.0 + i * angleStep
            val x = cx + (radius * v * cos(angle)).toFloat()
            val y = cy + (radius * v * sin(angle)).toFloat()
            vertices.add(Offset(x, y))
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()
        drawPath(dataPath, color = fillColor)
        drawPath(dataPath, color = strokeColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
        vertices.forEach { pt ->
            drawCircle(color = strokeColor, radius = 6f, center = pt)
        }
    }
}
