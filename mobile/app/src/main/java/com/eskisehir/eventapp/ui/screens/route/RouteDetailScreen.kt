package com.eskisehir.eventapp.ui.screens.route

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.data.model.RouteResponse
import com.eskisehir.eventapp.ui.viewmodel.RouteViewModel
import com.eskisehir.eventapp.ui.components.PoiCard

/**
 * Screen displaying the optimized route with ordered POIs.
 * Shows total distance, walking time, cost, and feasibility status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    onBackClick: () -> Unit,
    onPoiClick: (Long) -> Unit,
    onStartNavigation: (List<Long>) -> Unit = {},
    viewModel: RouteViewModel = hiltViewModel()
) {
    val route = viewModel.route.collectAsState().value
    val selectedPoiIndex = viewModel.selectedPoiIndex.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (route != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Route summary
                item {
                    RouteMetricsSummary(route)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Feasibility badge
                item {
                    FeasibilityBadge(route.routeStatus)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Ordered POIs list
                item {
                    Text(
                        text = "Optimized Route Order",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                itemsIndexed(route.orderedPois) { index, poi ->
                    RoutePoiItem(
                        poi = poi,
                        order = index + 1,
                        isSelected = index == selectedPoiIndex,
                        onSelect = { viewModel.selectPoi(index) },
                        onPoiClick = { onPoiClick(poi.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Start Navigation button
                item {
                    Button(
                        onClick = {
                            val eventIds = route.orderedPois.map { it.id }
                            onStartNavigation(eventIds)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Start Turn-by-Turn Navigation")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No route generated yet")
            }
        }
    }
}

@Composable
private fun RouteMetricsSummary(route: RouteResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Distance:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${String.format("%.1f", route.totalDistanceKm)} km",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Walking Time:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${route.totalWalkingMinutes} min (~${route.totalWalkingMinutes / 60}h)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Estimated Cost:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "₺${String.format("%.0f", route.estimatedCostTRY)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FeasibilityBadge(status: String) {
    val backgroundColor = when (status) {
        "FEASIBLE" -> MaterialTheme.colorScheme.inverseSurface
        "PARTIAL" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (status) {
        "FEASIBLE" -> MaterialTheme.colorScheme.inverseOnSurface
        "PARTIAL" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = when (status) {
                "FEASIBLE" -> "✓ Route is feasible within your constraints"
                "PARTIAL" -> "⚠ Route exceeds some constraints"
                else -> "✗ Route not feasible"
            },
            modifier = Modifier.padding(12.dp),
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RoutePoiItem(
    poi: com.eskisehir.eventapp.data.model.PoiResponse,
    order: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPoiClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .then(
                if (isSelected)
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                else
                    Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Order badge
            Surface(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // POI info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poi.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = poi.category.displayNameTr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // View details button
            TextButton(onClick = onPoiClick) {
                Text("View")
            }
        }
    }
}
