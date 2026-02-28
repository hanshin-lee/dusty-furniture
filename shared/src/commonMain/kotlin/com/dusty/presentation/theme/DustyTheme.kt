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
    primaryContainer = DustyBrownLight,
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
    error = RustRed,
    onError = WarmWhite,
    outline = LightWarmGray
)

private val DustyDarkColorScheme = darkColorScheme(
    primary = AntiqueGoldLight,
    onPrimary = DustyBrownDark,
    primaryContainer = DustyBrown,
    onPrimaryContainer = AntiqueGoldLight,
    secondary = AntiqueGold,
    onSecondary = DarkEspresso,
    background = DarkBackground,
    onBackground = Parchment,
    surface = DarkSurface,
    onSurface = Parchment,
    surfaceVariant = DarkCard,
    onSurfaceVariant = LightWarmGray,
    error = Color(0xFFE57373),
    onError = DarkEspresso,
    outline = WarmGray
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
