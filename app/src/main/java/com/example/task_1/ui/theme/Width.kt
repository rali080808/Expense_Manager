package com.example.task_1.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Width(
    val default: Dp = 100.dp,
    val extraSmall: Dp = 110.dp,
    val small: Dp = 150.dp,
    val medium: Dp = 370.dp,
    val large: Dp = 600.dp,
    val extraLarge: Dp = 900.dp
)

val LocalWidth = staticCompositionLocalOf { Width() }
