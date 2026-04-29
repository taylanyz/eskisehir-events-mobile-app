package com.eskisehir.eventapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Analytics screen for Phase 5.5.
 * Displays app metrics and usage statistics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var successRate by remember { mutableStateOf(97.5) }
    var totalRequests by remember { mutableStateOf(1234) }
    var averageResponseTime by remember { mutableStateOf(245L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success rate card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Success Rate",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            "${"%.1f".format(successRate)}%",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Total requests card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Total Requests",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            totalRequests.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Average response time card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Avg Response Time",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            "${averageResponseTime}ms",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Performance metrics section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Performance Metrics",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Metric items
            items(3) { index ->
                MetricRow(
                    label = when (index) {
                        0 -> "API Response Times"
                        1 -> "Cache Hit Rate"
                        else -> "Error Rate"
                    },
                    value = when (index) {
                        0 -> "245ms"
                        1 -> "78.3%"
                        else -> "2.5%"
                    }
                )
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
