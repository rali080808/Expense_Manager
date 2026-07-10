package com.example.task_1.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Width(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 0.5.dp,
    val small: Dp = 20.dp,
    val medium: Dp = 100.dp,
    val large: Dp = 180.dp,
    val extraLarge: Dp = 300.dp
)

val LocalWidth = staticCompositionLocalOf { Width() }
