package com.example.task_1.domain

import com.example.task_1.R
import java.math.BigDecimal
import kotlin.random.Random

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(
        val message: ErrorMessage
    ) : Result<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(value)
        return this
    }

    inline fun onFailure(action: (ErrorMessage) -> Unit): Result<T> {
        if (this is Failure) action(message)
        return this
    }
}


fun validateLength(text: String, lengthSmallest: Int, lengthLargest: Int): Result<String> {
    return when {
        text.length < lengthSmallest -> Result.Failure(ErrorMessage(
            R.string.this_field_should_be_at_least_symbols,
            listOf(lengthSmallest))
        )

        text.length > lengthLargest -> Result.Failure(ErrorMessage(
            R.string.this_field_be_at_most_symbols,
            listOf(lengthLargest)
        ))

        else -> Result.Success(text)
    }
}

fun isPositive(number: BigDecimal): Result<BigDecimal> {
    return when {
        number <= BigDecimal.ZERO -> Result.Failure(ErrorMessage( R.string.this_field_should_be_bigger_than_zero, listOf()))
        else -> Result.Success(number)
    }
}

fun isNotEmpty(text: String) :Result<String> {
    return when {
        text.isEmpty() -> Result.Failure(ErrorMessage( R.string.this_field_should_not_be_empty, listOf()))
        else -> Result.Success(text)
    }
}
fun isChosenCategory(categoryId : Long  ) : Result<Long> {
    return when {
        categoryId == NoFilter -> Result.Failure(ErrorMessage( R.string.please_choose_a_category))
        else -> Result.Success(categoryId)
    }
}

fun categoryExists(categoryText: String, categories: List<Category>) : Result<String> {
    val exists = categories.any { it.text.equals(categoryText, ignoreCase = true) }

    return if (exists) {
        Result.Failure(ErrorMessage( R.string.category_already_exists, listOf(categoryText)))
    } else {
        Result.Success(categoryText)
    }
}