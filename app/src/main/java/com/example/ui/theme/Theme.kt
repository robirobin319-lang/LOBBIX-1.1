package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LobbixDarkColorScheme = darkColorScheme(
    primary = LobbixCrimson,
    onPrimary = LobbixWhite,
    primaryContainer = LobbixCrimsonContainer,
    onPrimaryContainer = LobbixCrimsonBright,
    secondary = LobbixSilver,
    onSecondary = LobbixBlack,
    secondaryContainer = LobbixSurfaceHigh,
    onSecondaryContainer = LobbixWhite,
    tertiary = LobbixGold,
    onTertiary = LobbixBlack,
    background = LobbixBlack,
    onBackground = LobbixWhite,
    surface = LobbixSurface,
    onSurface = LobbixWhite,
    surfaceVariant = LobbixSurfaceElevated,
    onSurfaceVariant = LobbixSilver,
    outline = LobbixBorder,
    outlineVariant = LobbixSurfaceHigh,
    error = LobbixCrimsonBright,
    onError = LobbixBlack
)

val LobbixShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun LobbixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LobbixDarkColorScheme,
        typography = LobbixTypography,
        shapes = LobbixShapes,
        content = content
    )
}
