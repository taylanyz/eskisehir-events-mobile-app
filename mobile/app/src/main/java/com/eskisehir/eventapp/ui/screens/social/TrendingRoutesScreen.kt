package com.eskisehir.eventapp.ui.screens.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eskisehir.eventapp.data.model.SharedRouteDto
import com.eskisehir.eventapp.ui.components.RouteStatsDisplay

/**
 * Screen displaying trending/popular routes shared by the community.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingRoutesScreen(
    onBack: () -> Unit,
    onRouteClick: (SharedRouteDto) -> Unit,
    modifier: Modifier = Modifier
) {
    var trendingRoutes by remember { mutableStateOf<List<SharedRouteDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Mock data for demonstration
    LaunchedEffect(Unit) {
        // In a real app, this would fetch from API: GET /api/routes/trending
        trendingRoutes = listOf(
            SharedRouteDto(
                id = 1L,
                name = "Historic City Center Tour",
                shareCode = "abc123xyz",
                isPublic = true,
                averageRating = 4.8,
                totalRatings = 156,
                shareCount = 342,
                totalDistanceKm = 5.2,
                totalDurationMinutes = 120,
                estimatedBudgetTRY = 250.0
            ),
            SharedRouteDto(
                id = 2L,
                name = "Culinary Adventure Route",
                shareCode = "def456uvw",
                isPublic = true,
                averageRating = 4.7,
                totalRatings = 98,
                shareCount = 287,
                totalDistanceKm = 3.8,
                totalDurationMinutes = 180,
                estimatedBudgetTRY = 450.0
            ),
            SharedRouteDto(
                id = 3L,
                name = "Art Gallery Hop",
                shareCode = "ghi789rst",
                isPublic = true,
                averageRating = 4.5,
                totalRatings = 72,
                shareCount = 198,
                totalDistanceKm = 2.5,
                totalDurationMinutes = 90,
                estimatedBudgetTRY = 180.0
            )
        )
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trending Routes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (trendingRoutes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No trending routes yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(trendingRoutes) { route ->
                    TrendingRouteCard(
                        route = route,
                        onRouteClick = { onRouteClick(route) }
                    )
                }
            }
        }
    }
}

/**
 * Card displaying a trending route with stats.
 */
@Composable
private fun TrendingRouteCard(
    route: SharedRouteDto,
    onRouteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRouteClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Badge(
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Route details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%.1f", route.totalDistanceKm)} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${route.totalDurationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${route.estimatedBudgetTRY.toInt()} ₺",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Stats
            RouteStatsDisplay(
                averageRating = route.averageRating,
                totalRatings = route.totalRatings,
                shareCount = route.shareCount,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
