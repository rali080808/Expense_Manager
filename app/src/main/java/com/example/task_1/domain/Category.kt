package com.example.task_1.domain

import androidx.core.util.Predicate.not
import com.example.task_1.data.DataService

class Category (val text: String, val icon: String) {



}

val NoFilter: Category = Category("", "")
val MAX_CATEGORY_LENGTH  = 16
