//package com.example.task_1.data;
//
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.toArgb
//import com.example.task_1.domain.Transaction;
//
//
//import com.example.task_1.domain.Category;
//import com.example.task_1.domain.Currency
//import com.example.task_1.domain.PayMethod
//import kotlinx.coroutines.delay
//import java.time.LocalDate
//
//const val WAIT_TIME: Long = 200
//
//object DataService : IDataService {
//    private val categories: MutableList<Category> = mutableListOf(
//        Category(1, "ice cream", "🍦", Color.Magenta.toArgb()),
//        Category(2, "dresses", "👗", Color.Blue.toArgb())
//    )
//    private var nextCategoryID = 3L
//    private val transactions: MutableList<Transaction> =
//        mutableListOf(
//            Transaction(
//                id = 1,
//                "Rali",
//                "LIDL",
//                "6.0",
//                Currency.EURO,
//                LocalDate.of(2026, 6, 10).toString(),
//                1,
//                "Vanilla ice cream for everyone!",
//                PayMethod.CASH
//            ),
//
//            Transaction(
//                id = 2,
//                "Rali",
//                "Stradivarius",
//                "61.0",
//                Currency.EURO,
//                LocalDate.of(2026, 6, 20).toString(),
//                2,
//                "Numerous new dresses!",
//                PayMethod.CASH
//            ),
//
//            Transaction(
//                id = 3,
//                "Rali",
//                "Billa",
//                "2.0",
//                Currency.EURO,
//                LocalDate.of(2026, 6, 27).toString(),
//                1,
//                "Another ice cream!",
//                PayMethod.CASH
//            )
//        )
//
//
//    override suspend fun addCategory(category: Category) {
//        delay(WAIT_TIME)
//        categories.add(category.copy(id=nextCategoryID++))
//
//    }
//
//    var nextTransactionID: Long = 3;
//    override suspend fun addTransaction(transaction: Transaction) {
//        delay(WAIT_TIME)
//        transactions.add(transaction.copy(id = nextTransactionID++))
//    }
//
//    override suspend fun editTransaction(transaction: Transaction) {
////        delay(WAIT_TIME)
////        transactions.find{ it.id == transaction.id }
//        // TODO editTransaction
////
////        val oldCategoryID = transactions[index].categoryID
////        val oldMoney = transactions[index].money;
////        val currentCategoryID = transaction.categoryID
////        val currentMoney = transaction.money;
////
////        transactions.editTransaction(index, transaction)
////
////        categories[oldCategoryID]?.let { oldCategory ->
////            categories[oldCategoryID] = oldCategory.copy(
////                expenseOnThisCategory = oldCategory.expenseOnThisCategory - oldMoney
////            )
////        }
////
////        categories[currentCategoryID]?.let { currentCategory ->
////            categories[currentCategoryID] = currentCategory.copy(
////                expenseOnThisCategory = currentCategory.expenseOnThisCategory + currentMoney
////            )
////        }
//    }
//
//    override suspend fun removeCategory(id: Long): List<Category> {
//        delay(WAIT_TIME)
//        categories.removeIf { it.id == id }
//        return categories; //toMap
//    }
//
//    override suspend fun editCategory(id: Long, editedCategory: Category): List<Category> {
//        delay(WAIT_TIME)
//        val index = categories.indexOfFirst { it.id == id }
//        categories[index] = editedCategory.copy(id=id)
//        return categories;
//    }
//
//    override suspend fun getCategories(): List<Category> {
//        delay(WAIT_TIME)
//        return categories;
//    }
//
//    override suspend fun getTransactions(): List<Transaction> {
//        delay(WAIT_TIME)
//        return transactions;
//    }
//}
//
//
