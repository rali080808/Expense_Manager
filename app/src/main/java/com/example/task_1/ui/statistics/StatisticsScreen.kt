package com.example.task_1.ui.statistics

import android.icu.math.BigDecimal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.example.task_1.domain.Category
import com.example.task_1.domain.uiStates.StatisticsUiState

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.task_1.R
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.PeriodFilterBar
import com.example.task_1.ui.dashboard.CategoryOverviewCard
import com.example.task_1.ui.theme.height
import com.example.task_1.ui.theme.spacing

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {

    val lifecycleOwner = LocalLifecycleOwner.current
    var uiState by remember { mutableStateOf<StatisticsUiState>(StatisticsUiState.Loading) }
    LaunchedEffect(viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { state ->
                uiState = state
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val currentState = uiState) {
            is StatisticsUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

            is StatisticsUiState.Error -> ErrorDialog(
                (uiState as StatisticsUiState.Error).message,
                loadData = { viewModel.loadData() }
            )

            is StatisticsUiState.Success -> {
                val transactions = currentState.transactions
                val totalCurrentMonth = currentState.totalCurrentMonth
                val totalSelectedPeriod = currentState.totalSelectedPeriod
                val averageDaily = currentState.averageDaily
                val periodFilter = currentState.periodFilter
                val startDate = currentState.startDate
                val endDate = currentState.endDate
                val currentCurrency = currentState.selectedCurrency
                LazyColumn(Modifier.padding(horizontal = MaterialTheme.spacing.small)) {
                    item {
                        Text(
                            stringResource(R.string.statistics),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                                Text(
                                    stringResource(R.string.spent_this_month),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "${totalCurrentMonth} $currentCurrency",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(MaterialTheme.height.default))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            MetricCard(
                                stringResource(R.string.total_period),
                                "${totalSelectedPeriod} ${currentCurrency}",
                                Modifier.weight(1f)
                            )
                            MetricCard(
                                stringResource(R.string.daily_average),
                                "${averageDaily} ${currentCurrency}",
                                Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))

                        Text("Category Breakdown", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))
                    }

                    items(
                        viewModel.sortCategoriesByExpenseDesc(),
                        key = { it.id ?: 0L }) { category ->
                        CategoryOverviewCard(
                            category,
                            totalExpenses = viewModel.totalExpenses(),
                            transactions,
                            periodFilter,
                            startDate,
                            endDate
                        )

                    }
                    item {
                        PeriodFilterBar(
                            onPeriodSelected = { periodFilter, startDate, endDate ->
                                viewModel.setDateRange(
                                    periodFilter,
                                    startDate,
                                    endDate
                                )
                            }
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun AnimatedCategoryBar(category: Category, amount: BigDecimal, percentage: Float) {
    var showPopup by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(percentage, animationSpec = tween(1000, easing = LinearOutSlowInEasing))
    }

    Box(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)) {
        Column(modifier = Modifier.clickable { showPopup = true }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category.text, fontWeight = FontWeight.SemiBold) // Used category.text
                Text("${percentage}%")
            }
            Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.height.small),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                repeat(20) { i ->
                    val isActive = (i.toFloat() / 20) <= progress.value
                    val barColor = Color(category.color) // Used category.color
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.small)
                            .background(if (isActive) barColor else barColor.copy(alpha = 0.2f))
                    )
                }
            }
        }

        if (showPopup) {
            Popup(alignment = Alignment.TopCenter, onDismissRequest = { showPopup = false }) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(
                        top = MaterialTheme.spacing.medium,
                        start = MaterialTheme.spacing.small,
                        end = MaterialTheme.spacing.small
                    )
                ) {
                    Text(
                        "${category.text}: $${amount}",
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.padding(MaterialTheme.spacing.small)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.small)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}