package com.yourpackage.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Border(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 0.5.dp,
    val small: Dp = 1.dp,
    val medium: Dp = 3.dp,
    val large: Dp = 5.dp,
    val extraLarge: Dp = 6.dp
)

val LocalBorder = staticCompositionLocalOf { Border() }
