package com.example.task_1

import androidx.compose.foundation.layout.consumeWindowInsets
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

 import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.ui.category.CategoryViewModel
import com.example.task_1.ui.dashboard.DashboardViewModel
import com.example.task_1.ui.transaction.TransactionViewModel
import com.example.task_1.ui.category.CategoriesScreen
import com.example.task_1.ui.dashboard.DashboardScreen
import com.example.task_1.ui.transaction.TransactionsScreen
 import kotlinx.serialization.Contextual
import androidx.compose.runtime.collectAsState
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.statistics.StatisticsScreen
import com.example.task_1.ui.statistics.StatisticsViewModel
import com.example.task_1.ui.theme.spacing

@Serializable
object CategoriesScreenRoute
@Serializable
object StatisticsScreenRoute

@Serializable
object DashboardScreenRoute

@Serializable
object TransactionsScreenRoute

@Composable
fun Navigation(dataService: IDataService) {
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
    val dashboardViewModel = remember { DashboardViewModel(dataService) }
    val transactionViewModel = remember { TransactionViewModel(dataService) }
    val categoryViewModel = remember { CategoryViewModel(dataService) }
    val statisticsViewModel = remember { StatisticsViewModel(dataService) }

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

                    })
            }
        }) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppDestinations.DASHBOARD.route,
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.default, // Remove left padding
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()


                    )
                    .consumeWindowInsets(paddingValues)
            ) {

                composable<DashboardScreenRoute> {
                    LaunchedEffect(Unit) {
                        dashboardViewModel.loadData()
                    }
                    DashboardScreen(
                        viewModel = dashboardViewModel
                    )
                }

                composable<TransactionsScreenRoute> {
                    LaunchedEffect(Unit) {
                        transactionViewModel.loadData()
                    }
                    TransactionsScreen(
                        viewModel = transactionViewModel,
                    )
                }

                composable<StatisticsScreenRoute> {
                    LaunchedEffect(Unit) {
                        statisticsViewModel.loadData()
                    }
                    StatisticsScreen(
                        viewModel = statisticsViewModel,
                    )
                }





                composable<CategoriesScreenRoute> {
                    LaunchedEffect(Unit) {
                        categoryViewModel.loadData()
                    }
                    CategoriesScreen(

                        viewModel = categoryViewModel,
                        addCategory = { category -> categoryViewModel.addCategory(category) },
                        editCategory = { categoryIDForEdit, category ->
                            categoryViewModel.editCategory(categoryIDForEdit, category)
                        },
                    )
                }

            }
        }
    }

}

enum class AppDestinations(
    val label: String, val icon: Int, val route: Any
) {
    DASHBOARD("Dashboard", R.drawable.dashboard_24px, DashboardScreenRoute), TRANSACTIONS(
        "Transactions", R.drawable.wallet_24px, TransactionsScreenRoute
    ),
    CATEGORIES("Categories", R.drawable.bunny, CategoriesScreenRoute),
    STATISTICS("Statistics", R.drawable.finance_24px, StatisticsScreenRoute)

}
