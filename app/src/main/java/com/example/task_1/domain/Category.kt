package com.example.task_1.domain

import androidx.compose.ui.graphics.Color
import androidx.core.util.Predicate.not
import com.example.task_1.data.DataService

class Category (val text: String, val icon: String, val color: Color)
val NoFilter: Category = Category("", "", Color.Gray)
val MAX_CATEGORY_LENGTH  = 16
