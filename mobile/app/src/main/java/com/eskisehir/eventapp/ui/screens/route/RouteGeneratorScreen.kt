package com.eskisehir.eventapp.ui.screens.route

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.ui.viewmodel.RouteViewModel

/**
 * Screen for generating a new optimized route.
 * Allows user to set duration, budget, and walking time constraints.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteGeneratorScreen(
    selectedEventIds: List<Long>,
    onRouteGenerated: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: RouteViewModel = hiltViewModel()
) {
    var durationMinutes by remember { mutableStateOf(120) }  // 2 hours default
    var maxWalkingMinutes by remember { mutableStateOf(60) }
    var maxBudget by remember { mutableStateOf(500.0) }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val route by viewModel.route.collectAsState()

    LaunchedEffect(route) {
        if (route != null) {
            onRouteGenerated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generate Route") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Event count display
            Text(
                text = "Selected ${selectedEventIds.size} POI(s)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Duration slider
            Text(
                text = "Trip Duration: ${durationMinutes} min (${durationMinutes / 60} hrs)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = durationMinutes.toFloat(),
                onValueChange = { durationMinutes = it.toInt() },
                valueRange = 30f..360f,
                steps = 32,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Max walking time slider
            Text(
                text = "Max Walking Time: $maxWalkingMinutes min",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = maxWalkingMinutes.toFloat(),
                onValueChange = { maxWalkingMinutes = it.toInt() },
                valueRange = 5f..180f,
                steps = 34,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Budget slider
            Text(
                text = "Max Budget: ₺${String.format("%.0f", maxBudget)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = maxBudget.toFloat(),
                onValueChange = { maxBudget = it.toDouble() },
                valueRange = 50f..2000f,
                steps = 39,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Error message
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Generate button
            Button(
                onClick = {
                    viewModel.generateRoute(
                        eventIds = selectedEventIds,
                        durationMinutes = durationMinutes,
                        maxWalkingMinutes = maxWalkingMinutes,
                        maxBudget = maxBudget
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = selectedEventIds.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Generate Optimized Route")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
