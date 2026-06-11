package com.example.task_1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task_1.ui.screens.CategoriesScreen
import com.example.task_1.ui.screens.DashboardScreen
import com.example.task_1.ui.screens.TransactionsScreen


@Composable
fun Navigation()
{
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }

                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            currentDestination.composable(Modifier.padding(innerPadding).shadow(50.dp), 50.sp)
        }
    }
}
enum class AppDestinations(
    val label: String,
    val icon: Int, // TODO pass a single object instead of many arguments to the screens
    val composable: @Composable (Modifier, TextUnit) -> Unit
) {
    DASHBOARD("Dashboard", R.drawable.ic_home, {modifier, fontSize -> DashboardScreen(modifier, fontSize, color=Color.Blue)}),
    TRANSACTIONS("Transactions", R.drawable.ic_favorite,{modifier, fontSize -> TransactionsScreen(modifier, fontSize, color=Color.Red)}),
    CATEGORIES("Categories", R.drawable.ic_account_box, {modifier, fontSize -> CategoriesScreen(modifier, fontSize, color=Color.Yellow)}),
}
