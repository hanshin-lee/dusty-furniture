package com.dusty.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DustyLightColorScheme = lightColorScheme(
    primary = DustyBrown,
    onPrimary = WarmWhite,
    primaryContainer = LightOak,
    onPrimaryContainer = DarkEspresso,
    secondary = AntiqueGold,
    onSecondary = DarkEspresso,
    secondaryContainer = AntiqueGoldLight,
    onSecondaryContainer = DarkEspresso,
    tertiary = SageGreen,
    onTertiary = WarmWhite,
    tertiaryContainer = SageGreenLight,
    onTertiaryContainer = DarkEspresso,
    background = Parchment,
    onBackground = DarkEspresso,
    surface = Cream,
    onSurface = DarkEspresso,
    surfaceVariant = Linen,
    onSurfaceVariant = WarmGray,
    surfaceContainerLowest = WarmWhite,
    surfaceContainerLow = Cream,
    surfaceContainer = Linen,
    surfaceContainerHigh = ParchmentDark,
    surfaceContainerHighest = LinenDark,
    error = RustRed,
    onError = WarmWhite,
    outline = LightWarmGray,
    outlineVariant = Color(0xFFD4C8B8),
    inverseSurface = DarkEspresso,
    inverseOnSurface = Parchment,
    inversePrimary = AntiqueGoldLight
)

private val DustyDarkColorScheme = darkColorScheme(
    primary = AntiqueGoldLight,
    onPrimary = DustyBrownDark,
    primaryContainer = DustyBrown,
    onPrimaryContainer = AntiqueGoldLight,
    secondary = AntiqueGold,
    onSecondary = DarkEspresso,
    secondaryContainer = Teak,
    onSecondaryContainer = AntiqueGoldLight,
    tertiary = SageGreenLight,
    onTertiary = DarkEspresso,
    tertiaryContainer = SageGreenDark,
    onTertiaryContainer = SageGreenLight,
    background = DarkBackground,
    onBackground = Parchment,
    surface = DarkSurface,
    onSurface = Parchment,
    surfaceVariant = DarkCard,
    onSurfaceVariant = LightWarmGray,
    surfaceContainerLowest = Color(0xFF140E08),
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkCardElevated,
    surfaceContainerHighest = Color(0xFF4E3F34),
    error = Color(0xFFE57373),
    onError = DarkEspresso,
    outline = WarmGray,
    outlineVariant = WalnutStain,
    inverseSurface = Parchment,
    inverseOnSurface = DarkEspresso,
    inversePrimary = DustyBrown
)

@Composable
fun DustyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DustyDarkColorScheme else DustyLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DustyTypography,
        shapes = DustyShapes,
        content = content
    )
}
