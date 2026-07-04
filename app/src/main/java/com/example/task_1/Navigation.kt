package com.example.task_1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

import com.example.task_1.data.DataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.ui.category.CategoryViewModel
import com.example.task_1.ui.dashboard.DashboardViewModel
import com.example.task_1.ui.transaction.TransactionViewModel
import com.example.task_1.ui.category.CategoriesScreen
import com.example.task_1.ui.dashboard.DashboardScreen
import com.example.task_1.ui.ShowDescription
import com.example.task_1.ui.transaction.AddTransaction
import com.example.task_1.ui.transaction.TransactionsScreen
import com.example.task_1.ui.category.AddCategory
import com.example.task_1.ui.category.EditCategory
import com.example.task_1.ui.category.CategoryDeleteDialog
import kotlinx.serialization.Contextual
import androidx.compose.runtime.collectAsState
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.Transactions
import com.example.task_1.ui.TransactionCard

@Serializable
data class TransactionCardRoute(
    val transactionIndex: Int,
    val categoryID: Int
)

@Serializable
data class ShowDescriptionRoute(
    val description: String,
)

@Serializable
data class EditCategoryRoute(
    val categoryIDForEdit: Int,
)

@Serializable
object CategoriesScreenRoute
@Serializable
object DashboardScreenRoute
@Serializable
object TransactionsScreenRoute

@Composable
fun Navigation() {
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    val dashboardViewModel = remember { DashboardViewModel(DataService) }
    val transactionViewModel = remember { TransactionViewModel(DataService) }
    val categoryViewModel = remember { CategoryViewModel(DataService) }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            painterResource(destination.icon),
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = {
                        currentDestination = destination
                        navController.navigate(destination.route)

                    }
                )
            }
        }
    ) {Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
             NavHost(
                navController = navController,
                startDestination = AppDestinations.DASHBOARD.route,
modifier = Modifier.padding(paddingValues)
            ) {

                composable<DashboardScreenRoute> {
                    LaunchedEffect(Unit) {
                        dashboardViewModel.loadData()
                    }
                    DashboardScreen(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        viewModel = dashboardViewModel,

                        onNavigateToDescription = { description ->
                            navController.navigate(
                                ShowDescriptionRoute(description = description))
                        }
                    )
                }

                composable<TransactionsScreenRoute> {
                    LaunchedEffect(Unit) {
                        transactionViewModel.loadData()
                    }
                    TransactionsScreen(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        viewModel = transactionViewModel,
                        onAddClick = { navController.navigate("addTransaction") },
                        onNavigateToDescription = { index ->
                            navController.navigate(ShowDescriptionRoute(index))
                        }
                    )
                }

                composable<TransactionCardRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<TransactionCardRoute>()

                    val previousRoute = navController.previousBackStackEntry?.destination?.route

                    val getTransaction: (Int) -> Transaction =
                        if (previousRoute?.contains("TransactionsScreenRoute") == true) { index ->
                            transactionViewModel.getTransaction(index)
                        } else { index ->
                            dashboardViewModel.getTransaction(index)
                        }

                    val getCategory: (Int) -> Category =
                        if (previousRoute?.contains("TransactionsScreenRoute") == true) { id ->
                            transactionViewModel.getCategory(id)
                        } else { id ->
                            dashboardViewModel.getCategory(id)
                        }
                    TransactionCard(
                        transaction = getTransaction(route.transactionIndex),
                         category = getCategory(route.categoryID),
                         showDescription = { transactionIndex ->
                            navController.navigate(ShowDescriptionRoute(transactionIndex))
                        },
                    )
                }

                composable("addTransaction") {
                    AddTransaction(
                        returnToTransactionScreen = { navController.popBackStack() },
                        viewModel = transactionViewModel,
                    )
                }

                composable<ShowDescriptionRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<ShowDescriptionRoute>()


                    ShowDescription(
                       description = route.description,
                        returnToMainScreen = { navController.popBackStack() },
                    )
                }

                composable<CategoriesScreenRoute> {
                    LaunchedEffect(Unit) {
                        categoryViewModel.loadData()
                    }
                    CategoriesScreen(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        viewModel = categoryViewModel,
                        addCategoryOnClick = { navController.navigate("addCategory") },
                        editCategoryOnClick = { categoryIDForEdit ->
                            navController.navigate(
                                EditCategoryRoute(categoryIDForEdit)
                            )
                        },
                        categoryDeleteDialog = { navController.navigate("categoryDeleteDialog") }
                    )
                }

                composable("addCategory") {
                    AddCategory(
                        returnToCategoryScreen = { navController.popBackStack() },
                        viewModel = categoryViewModel
                    )
                }

                composable<EditCategoryRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<EditCategoryRoute>()
                    val currentCategory =
                        categoryViewModel.categories.value[route.categoryIDForEdit] ?: ErrorCategory
                    EditCategory(
                        categoryIDForEdit = route.categoryIDForEdit,
                        returnToCategoryScreen = { navController.popBackStack() },
                        currentCategory = currentCategory,
                        editCategory = { categoryID, editedCategory ->
                            categoryViewModel.editCategory(
                                categoryID,
                                editedCategory
                            )
                        }
                    )
                }

                composable("categoryDeleteDialog") {
                    CategoryDeleteDialog(
                        returnToCategoryScreen = { navController.popBackStack() },
                        viewModel = categoryViewModel
                    )
                }
            }
        }}

}

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val route: Any
) {
    DASHBOARD("Dashboard", R.drawable.ic_home, DashboardScreenRoute),
    TRANSACTIONS("Transactions", R.drawable.ic_favorite, TransactionsScreenRoute),
    CATEGORIES("Categories", R.drawable.ic_account_box, CategoriesScreenRoute)
}
