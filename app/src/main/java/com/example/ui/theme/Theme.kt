package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class CfoExtendedColors(
    val isDark: Boolean,
    val cardSurface: Color,
    val softGreenSurface: Color,
    val slateBlueSurface: Color,
    val sandSurface: Color,
    val goldHighlight: Color,
    val goldSoftBg: Color,
    val titleGreen: Color,
    val bodyText: Color,
    val mutedText: Color,
    val oliveAccent: Color,
    val sageAccent: Color,
    val borderSubtle: Color,
    val correctText: Color,
    val correctBg: Color,
    val wrongText: Color,
    val wrongBg: Color,
    val warningBg: Color,
    val buttonPrimary: Color,
    val onButtonPrimary: Color
)

private val DayExtendedColors = CfoExtendedColors(
    isDark = false,
    cardSurface = DayCardSurface,
    softGreenSurface = DaySageSurface,
    slateBlueSurface = DaySlateBlue,
    sandSurface = SandBeige,
    goldHighlight = SoftGold,
    goldSoftBg = SoftGold.copy(alpha = 0.42f),
    titleGreen = TextOliveGreen,
    bodyText = DayBodyText,
    mutedText = TextOliveGreen,
    oliveAccent = OliveGreen,
    sageAccent = SageGreen,
    borderSubtle = SageGreen.copy(alpha = 0.7f),
    correctText = DayCorrectGreen,
    correctBg = DayCorrectContainer,
    wrongText = DayWrongTerracotta,
    wrongBg = DayWrongContainer,
    warningBg = DayWarningContainer,
    buttonPrimary = TextOliveGreen,
    onButtonPrimary = WarmWhite
)

private val NightExtendedColors = CfoExtendedColors(
    isDark = true,
    cardSurface = NightSurface,
    softGreenSurface = NightSageSurface,
    slateBlueSurface = NightSlateBlueSurface,
    sandSurface = NightSandSurface,
    goldHighlight = SoftGold,
    goldSoftBg = NightSandSurface,
    titleGreen = SageGreen,
    bodyText = NightOnSurface,
    mutedText = NightMutedText,
    oliveAccent = OliveGreen,
    sageAccent = SageGreen,
    borderSubtle = NightBorder,
    correctText = NightCorrectGreen,
    correctBg = NightCorrectContainer,
    wrongText = NightWrongTerracotta,
    wrongBg = NightWrongContainer,
    warningBg = NightWarningContainer,
    buttonPrimary = SageGreen,
    onButtonPrimary = Color(0xFF1A231E)
)

val LocalCfoColors = staticCompositionLocalOf { DayExtendedColors }

object CfoTheme {
    val colors: CfoExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCfoColors.current
}

private val LightColorScheme = lightColorScheme(
    primary = TextOliveGreen,
    onPrimary = WarmWhite,
    primaryContainer = DaySageSurface,
    onPrimaryContainer = TextOliveGreen,
    secondary = OliveGreen,
    onSecondary = WarmWhite,
    secondaryContainer = DaySlateBlue,
    onSecondaryContainer = DayBodyText,
    tertiary = Color(0xFF7B6330),
    onTertiary = WarmWhite,
    tertiaryContainer = SoftGold,
    onTertiaryContainer = DayBodyText,
    background = DayBackground,
    onBackground = DayBodyText,
    surface = DayCardSurface,
    onSurface = DayBodyText,
    surfaceVariant = SandBeige,
    onSurfaceVariant = TextOliveGreen,
    outline = SageGreen,
    outlineVariant = SandBeige,
    error = DayWrongTerracotta,
    onError = Color.White,
    errorContainer = DayWrongContainer,
    onErrorContainer = DayWrongTerracotta
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = Color(0xFF1A231E),
    primaryContainer = NightSageSurface,
    onPrimaryContainer = NightOnSurface,
    secondary = Color(0xFFA8BECB),
    onSecondary = Color(0xFF17232A),
    secondaryContainer = NightSlateBlueSurface,
    onSecondaryContainer = NightOnSurface,
    tertiary = SoftGold,
    onTertiary = Color(0xFF2B2314),
    tertiaryContainer = NightSandSurface,
    onTertiaryContainer = SoftGold,
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSageSurface,
    onSurfaceVariant = NightMutedText,
    outline = NightBorder,
    outlineVariant = Color(0xFF2D3932),
    error = NightWrongTerracotta,
    onError = Color(0xFF3B1411),
    errorContainer = NightWrongContainer,
    onErrorContainer = NightWrongTerracotta
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MissaoCfoTheme(
    themeMode: AppThemeMode = AppThemeMode.AUTO,
    darkTheme: Boolean = resolveIsDarkTheme(themeMode, isSystemInDarkTheme()),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) NightExtendedColors else DayExtendedColors

    CompositionLocalProvider(LocalCfoColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
