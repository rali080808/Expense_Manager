package com.example.task_1.ui.theme



import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Elevation(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 0.5.dp,
    val small: Dp = 1.dp,
    val medium: Dp = 2.dp,
    val large: Dp = 2.2.dp,
    val extraLarge: Dp = 3.2.dp
)

val LocalElevation = staticCompositionLocalOf { Elevation() }
