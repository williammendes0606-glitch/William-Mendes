package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Nova paleta oficial MISSÃO CFO MA
val OliveGreen = Color(0xFF718675)       // Verde oliva
val SageGreen = Color(0xFFA8B8A5)        // Verde sage
val SandBeige = Color(0xFFE9E2D2)        // Areia
val SoftGold = Color(0xFFD1B477)         // Dourado suave
val WarmWhite = Color(0xFFF7F6F0)        // Branco quente
val TextOliveGreen = Color(0xFF46564B)   // Verde para textos

// Tokens Modo Dia (claro, confortável, alto contraste)
val DayBackground = WarmWhite            // #F7F6F0
val DaySandSurface = SandBeige           // #E9E2D2
val DayCardSurface = Color(0xFFFCFBF7)   // Branco quente elevado
val DaySageSurface = Color(0xFFDFE6DD)   // Verde sage suave para cartões legíveis
val DaySlateBlue = Color(0xFFDCE4E8)     // Azul acinzentado discreto para apoio
val DayBodyText = Color(0xFF2C342E)      // Texto principal escuro legível
val DayMutedText = TextOliveGreen        // #46564B para subtítulos e ênfases
val DayBorder = SageGreen                // #A8B8A5
val DayCorrectGreen = Color(0xFF33664A)
val DayCorrectContainer = Color(0xFFDDEADF)
val DayWrongTerracotta = Color(0xFF9F473D)
val DayWrongContainer = Color(0xFFF4E2DF)
val DayWarningContainer = Color(0xFFF1E8D1)

// Tokens Modo Noite (verde acinzentado profundo + azul acinzentado, sem preto puro)
val NightBackground = Color(0xFF171E1A)
val NightSurface = Color(0xFF202924)
val NightSageSurface = Color(0xFF2A362F)
val NightSlateBlueSurface = Color(0xFF24313A)
val NightSandSurface = Color(0xFF302D25)
val NightOnSurface = Color(0xFFECE9DF)
val NightMutedText = Color(0xFFBDC7BE)
val NightBorder = Color(0xFF425247)
val NightCorrectGreen = Color(0xFF88C29E)
val NightCorrectContainer = Color(0xFF243D2F)
val NightWrongTerracotta = Color(0xFFE3948A)
val NightWrongContainer = Color(0xFF482825)
val NightWarningContainer = Color(0xFF3D3422)

// Aliases semânticos legados mapeados para a nova paleta (para referências estáticas)
val SoftGreen = DaySageSurface
val SlateBlue = DaySlateBlue
val LightBeige = SandBeige
val DeepForestGreen = TextOliveGreen
val ForestGreenMid = OliveGreen
val DarkSlateText = DayBodyText
val MutedSlateText = TextOliveGreen
val SubtleBorder = SageGreen
val CardWhite = DayCardSurface
val CorrectGreen = DayCorrectGreen
val CorrectContainer = DayCorrectContainer
val WrongTerracotta = DayWrongTerracotta
val WrongContainer = DayWrongContainer
val WarningAmber = Color(0xFF8A6E35)
val WarningContainer = DayWarningContainer
