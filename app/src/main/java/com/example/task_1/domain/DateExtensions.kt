package com.example.task_1.domain

 import kotlinx.datetime.toKotlinLocalIsoWeekDate
import java.time.LocalDate

fun LocalDate.isDateInRange(
    periodFilter: PeriodFilter,
    startDate: LocalDate,
    endDate: LocalDate
): Boolean {

    return when (periodFilter) {
//        PeriodFilter.NONE -> true
        PeriodFilter.MONTH -> (this.month == startDate.month && this.year == startDate.year)
        PeriodFilter.CUSTOM -> this in startDate..endDate
        PeriodFilter.TODAY -> this == startDate
        PeriodFilter.WEEK -> this.toKotlinLocalIsoWeekDate() == startDate.toKotlinLocalIsoWeekDate()
                          && this.year == startDate.year
    }
}