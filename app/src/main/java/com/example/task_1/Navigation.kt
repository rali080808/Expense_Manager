package com.example.task_1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
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
import com.example.task_1.domain.CategoryViewModel
import com.example.task_1.domain.DashboardViewModel
import com.example.task_1.domain.TransactionViewModel
import com.example.task_1.ui.category.AddCategory
import com.example.task_1.ui.category.CategoriesScreen
import com.example.task_1.ui.category.CategoryDeleteDialog
import com.example.task_1.ui.category.EditCategory
import com.example.task_1.ui.dashboard.DashboardScreen
import com.example.task_1.ui.ShowDescription
import com.example.task_1.ui.transaction.AddTransaction
import com.example.task_1.ui.transaction.TransactionsScreen

@Serializable
data class ShowDescriptionRoute(
    val text: String
)

@Composable
fun Navigation() {
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    val dashboardViewModel = remember{ DashboardViewModel(DataService) }
    val transactionViewModel = remember{ TransactionViewModel(DataService) }
    val categoryViewModel = remember{ CategoryViewModel (DataService)}
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
                        navController.navigate(destination.name)
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = AppDestinations.DASHBOARD.name,
                modifier = Modifier
                    .padding(innerPadding)
            ) {

                composable(AppDestinations.DASHBOARD.name) {
                    DashboardScreen(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        viewModel = dashboardViewModel,

                        onNavigateToDescription = { textArg ->
                            navController.navigate(ShowDescriptionRoute(text = textArg))
                        }
                    )
                }

                composable(AppDestinations.TRANSACTIONS.name) {
                    TransactionsScreen(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        viewModel = transactionViewModel,
                        onAddClick = { navController.navigate("addTransaction") },
                        onNavigateToDescription = { textArg ->
                            navController.navigate(ShowDescriptionRoute(text = textArg))
                        }
                    )
                }

                composable("addTransaction") {
                    AddTransaction(
                        returnToTransactionScreen={navController.popBackStack()},
                        viewModel= transactionViewModel,
                    )
                }

                composable<ShowDescriptionRoute> { backStackEntry ->

                    val route = backStackEntry.toRoute<ShowDescriptionRoute>()

                    ShowDescription(
                        description = route.text,
                        returnToMainScreen = { navController.popBackStack() },
                    )
                }

                composable(AppDestinations.CATEGORIES.name) {
                    CategoriesScreen(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        viewModel=categoryViewModel,
                        addCategoryOnClick = {navController.navigate("addCategory")},
                        editCategoryOnClick = {navController.navigate("editCategory")},
                        categoryDeleteDialog = {navController.navigate("categoryDeleteDialog")}
                    )
                }

                composable("addCategory") {
                    AddCategory(
                        returnToCategoryScreen={navController.popBackStack()},
                        viewModel = categoryViewModel
                    )
                }

                composable("editCategory") {
                    EditCategory(
                        returnToCategoryScreen = { navController.popBackStack() },
                        viewModel = categoryViewModel
                    )
                }

                composable("categoryDeleteDialog") {
                    CategoryDeleteDialog(
                        returnToCategoryScreen = { navController.popBackStack() },
                        viewModel = categoryViewModel
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int
) {
    DASHBOARD("Dashboard", R.drawable.ic_home),
    TRANSACTIONS("Transactions", R.drawable.ic_favorite),
    CATEGORIES("Categories", R.drawable.ic_account_box)
}
