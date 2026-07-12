package com.example.task_1.domain

import androidx.core.content.MimeTypeFilter.matches
import com.example.task_1.R
import java.math.BigDecimal
import kotlin.collections.listOf

import java.text.BreakIterator
import kotlin.text.Regex
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
        text.length < lengthSmallest -> Result.Failure(
            ErrorMessage(
                R.string.this_field_should_be_at_least_symbols,
                listOf(lengthSmallest)
            )
        )

        text.length > lengthLargest -> Result.Failure(
            ErrorMessage(
                R.string.this_field_be_at_most_symbols,
                listOf(lengthLargest)
            )
        )

        else -> Result.Success(text)
    }
}

fun isPositive(number: BigDecimal): Result<BigDecimal> {
    return when {
        number <= BigDecimal.ZERO -> Result.Failure(
            ErrorMessage(
                R.string.this_field_should_be_bigger_than_zero,
                listOf()
            )
        )

        else -> Result.Success(number)
    }
}

fun isNotEmpty(text: String): Result<String> {
    return when {
        text.isEmpty() -> Result.Failure(
            ErrorMessage(
                R.string.this_field_should_not_be_empty,
                listOf()
            )
        )

        else -> Result.Success(text)
    }
}

fun isChosenCategory(categoryId: Long): Result<Long> {
    return when {
        categoryId == NoFilter -> Result.Failure(ErrorMessage(R.string.please_choose_a_category))
        else -> Result.Success(categoryId)
    }
}

fun categoryExists(categoryText: String, categories: List<Category>, categoryId: Long?): Result<String> {
    val exists = categories.any { it.text.equals(categoryText, ignoreCase = true) && it.id != categoryId}

    return if (exists) {
        Result.Failure(ErrorMessage(R.string.category_already_exists, listOf(categoryText)))
    } else {
        Result.Success(categoryText)
    }
}

fun hasUpToTwoDecimalPlaces(money: BigDecimal): Result<BigDecimal> {
    return when {
        money.stripTrailingZeros().scale() > 2 -> Result.Failure(
            ErrorMessage(
                R.string.money_must_have_no_more_than_2_decimal_places,
                listOf()
            )
        )

        else -> Result.Success(money)
    }
}

fun validateMoney(money: String): Result<BigDecimal> {
    try {
        val moneyBigDecimal = BigDecimal(money)
        hasUpToTwoDecimalPlaces(moneyBigDecimal).onFailure { message ->
            return Result.Failure(
                message
            )
        }
        isPositive(moneyBigDecimal).onFailure { message -> return Result.Failure(message) }
        return Result.Success(moneyBigDecimal)
    } catch (e: Exception) {
        return Result.Failure(ErrorMessage(R.string.invalid_format))
    }
}


fun validateIcon(icon: String): Result<String> {
    if (icon.isEmpty()) {
        return Result.Failure(ErrorMessage(R.string.you_should_choose_1_emoji_and_nothing_else))
    }

    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(icon)

    var count = 0
    while (iterator.next() != BreakIterator.DONE) {
        count++
    }

     return if (count == 1) {
        Result.Success(icon)
    } else {
        Result.Failure(ErrorMessage(R.string.you_should_choose_1_emoji_and_nothing_else))
    }
}
fun containsEmojis(text: String): Result<String> {

    val emojiRegex = Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+")
    if(   emojiRegex.containsMatchIn(text) ) {
        return Result.Failure(ErrorMessage(R.string.an_emoji_is_reserved_for_the_icon_of_the_category_the_name_should_not_contain_any_emojis) )
    }
    return Result.Success(text)

}