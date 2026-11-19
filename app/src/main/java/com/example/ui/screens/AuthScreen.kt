package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.firebase.attemptAutoSignIn
import com.example.firebase.onGoogleSignInClicked
import com.example.ui.theme.CfoTheme
import kotlinx.coroutines.CancellationException

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val cfo = CfoTheme.colors

    var isLoading by remember { mutableStateOf(false) }
    var isAutoChecking by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                    isAutoChecking = false
                    onAuthSuccess()
                },
                onUnauthenticated = {
                    isAutoChecking = false
                },
                scope = scope
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            isAutoChecking = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(206.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Crossfade(targetState = cfo.isDark, label = "authHeroCrossfade") { dark ->
                    Image(
                        painter = painterResource(
                            id = if (dark) R.drawable.img_cadet_night_1795082521593
                            else R.drawable.img_cadet_dawn_1795082521496
                        ),
                        contentDescription = "Cadete em formação observando a academia militar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x22171E1A),
                                    Color(0xAA171E1A)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = cfo.goldHighlight
                    ) {
                        Text(
                            text = "CFO PMMA • UEMA/PAES",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1F2821),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "Disciplina, Preparo e Conquista",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF7F6F0),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = cfo.goldHighlight
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Escudo CFO MA",
                        tint = Color(0xFF1F2821),
                        modifier = Modifier
                            .padding(10.dp)
                            .size(28.dp)
                    )
                }
                Column {
                    Text(
                        text = "MISSÃO CFO MA",
                        style = MaterialTheme.typography.headlineMedium,
                        color = cfo.titleGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Plataforma Completa de Preparação ao Oficialato",
                        style = MaterialTheme.typography.bodyMedium,
                        color = cfo.mutedText
                    )
                }
            }

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
                    AuthFeatureRow(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        tintColor = cfo.softGreenSurface,
                        title = "Edital PAES/UEMA Completo",
                        subtitle = "Aulas aprofundadas do básico ao avançado, com foco especial em Português, Matemática e temas regionais do Maranhão."
                    )
                    AuthFeatureRow(
                        icon = Icons.Default.Psychology,
                        tintColor = cfo.slateBlueSurface,
                        title = "Revisão Espaçada, Redação e Tutor IA",
                        subtitle = "Caderno de erros automático, flashcards adaptativos, simulados cronometrados e correção de redação."
                    )
                    AuthFeatureRow(
                        icon = Icons.Default.CloudDone,
                        tintColor = cfo.goldSoftBg,
                        title = "Progresso Seguro e Exportável",
                        subtitle = "Conecte sua conta Google para salvar seu avanço na nuvem e exportar backups JSON a qualquer momento."
                    )
                }
            }

            if (errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = cfo.wrongBg
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = cfo.wrongText,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    errorMessage = null
                    isLoading = true
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isLoading = false
                            onAuthSuccess()
                        },
                        onAuthError = { msg ->
                            isLoading = false
                            errorMessage = msg
                        },
                        scope = scope,
                        onAuthCancelled = {
                            isLoading = false
                        }
                    )
                },
                enabled = !isLoading && !isAutoChecking,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .testTag("google_sign_in_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cfo.buttonPrimary,
                    contentColor = cfo.onButtonPrimary
                )
            ) {
                if (isLoading || isAutoChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = cfo.onButtonPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(if (isAutoChecking) "Verificando sessão..." else "Conectando...")
                } else {
                    Text(
                        text = "Entrar com Google e Iniciar Missão",
                        style = MaterialTheme.typography.titleMedium,
                        color = cfo.onButtonPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tintColor: Color,
    title: String,
    subtitle: String
) {
    val cfo = CfoTheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tintColor
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = cfo.titleGreen,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = cfo.titleGreen
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = cfo.mutedText
            )
        }
    }
}
