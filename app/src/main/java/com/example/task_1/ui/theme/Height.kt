package com.example.task_1.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Height(
    val default: Dp = 15.dp,
    val extraSmall: Dp = 24.dp,
    val small: Dp = 70.dp,
    val medium: Dp = 120.dp,
    val large: Dp = 200.dp,
    val extraLarge: Dp = 300.dp
)

val LocalHeight = staticCompositionLocalOf { Height() }
