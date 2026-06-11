package com.example.task_1.data

import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.Transactions
import java.time.LocalDate

val transactions : Transactions = Transactions(mutableListOf(
    Transaction("Az", "Ti", 6.0, "€", LocalDate.of(2026, 6, 10), Category.Food),
    Transaction("Az", "Ti", 25.0,"€", LocalDate.of(2026, 6, 10), Category.Food),
    Transaction("Az", "Ti", 33.0,"€", LocalDate.of(2026, 6, 10), Category.Food),
    Transaction("Az", "Ti", 10.3,"€", LocalDate.of(2026, 6, 10), Category.Medicaments),
    Transaction("Az", "Ti", 20.3,"€", LocalDate.of(2026, 6, 10), Category.Medicaments),
    Transaction("Az", "Ti", 23.3,"€", LocalDate.of(2026, 6, 10), Category.Clothes),
    Transaction("Az", "Ti", 20.3,"€", LocalDate.of(2026, 6, 10), Category.Clothes),
    Transaction("Az", "Ti", 8.3,"€", LocalDate.of(2026, 6, 10), Category.Food),
    Transaction("Az", "Ti", 23.3,"€", LocalDate.of(2026, 6, 10), Category.Food),
    Transaction("Az", "Ti", 32.3,"€", LocalDate.of(2026, 6, 10), Category.Medicaments),
    Transaction("Az", "Ti", 88.0,"€", LocalDate.of(2026, 6, 10), Category.Clothes)))
