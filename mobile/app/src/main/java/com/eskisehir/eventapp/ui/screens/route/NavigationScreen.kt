package com.eskisehir.eventapp.ui.screens.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.data.model.NavigationStepDto
import com.eskisehir.eventapp.ui.viewmodel.NavigationViewModel
import kotlinx.coroutines.delay

/**
 * Screen for active turn-by-turn navigation (Phase 4.6).
 * Shows current step, distance/time to destination, and next waypoint.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    eventIds: List<Long>,
    startLatitude: Double? = null,
    startLongitude: Double? = null,
    onBackClick: () -> Unit,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navigationData by viewModel.navigationData.collectAsState()
    val currentStepIndex by viewModel.currentStepIndex.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isNavigationActive by viewModel.isNavigationActive.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Start navigation on compose
    LaunchedEffect(Unit) {
        viewModel.startNavigation(eventIds, startLatitude, startLongitude)
    }

    // Simulate GPS updates every 2 seconds (in real app, use LocationManager)
    LaunchedEffect(isNavigationActive) {
        while (isNavigationActive) {
            delay(2000)
            navigationData?.let { nav ->
                if (currentStepIndex < nav.steps.size) {
                    val step = nav.steps[currentStepIndex]
                    // Move towards target: simple linear interpolation
                    val progress = (System.currentTimeMillis() / 100000.0) % 1.0
                    val lat = step.startLatitude + (step.endLatitude - step.startLatitude) * progress
                    val lng = step.startLongitude + (step.endLongitude - step.startLongitude) * progress
                    viewModel.updateCurrentLocation(lat, lng)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Turn-by-Turn Navigation") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopNavigation()
                        onBackClick()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        } else if (navigationData != null && isNavigationActive) {
            NavigationContent(
                navigationData = navigationData!!,
                currentStepIndex = currentStepIndex,
                currentLocation = currentLocation,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Navigation Complete!")
            }
        }
    }
}

@Composable
private fun NavigationContent(
    navigationData: com.eskisehir.eventapp.data.model.TurnByTurnNavigationResponse,
    currentStepIndex: Int,
    currentLocation: Pair<Double, Double>?,
    viewModel: NavigationViewModel,
    modifier: Modifier = Modifier
) {
    val currentStep = navigationData.steps.getOrNull(currentStepIndex)
    val remainingMetrics = viewModel.getRemainingMetrics()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { (currentStepIndex + 1).toFloat() / navigationData.steps.size },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Current step instruction (MAIN)
        if (currentStep != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Navigation,
                            contentDescription = "Navigation",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentStep.instruction,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${String.format("%.2f", currentStep.distanceKm)} km • ~${currentStep.durationMinutes} min",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Destination POI
                    if (currentStep.toPoiName != null) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Destination",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Destination: ${currentStep.toPoiName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Remaining time/distance
        if (remainingMetrics != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${String.format("%.1f", remainingMetrics.first)} km",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${remainingMetrics.second} min",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Estimated time",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Step buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.previousStep() },
                modifier = Modifier.weight(1f),
                enabled = currentStepIndex > 0
            ) {
                Text("← Previous")
            }

            Button(
                onClick = { viewModel.advanceToNextStep() },
                modifier = Modifier.weight(1f),
                enabled = currentStepIndex < navigationData.steps.size - 1
            ) {
                Text("Next →")
            }
        }

        // Stop button
        Button(
            onClick = { viewModel.stopNavigation() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Icon(
                Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Stop Navigation")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remaining steps preview
        if (navigationData.steps.size > currentStepIndex + 1) {
            Text(
                text = "Upcoming Steps",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            for (i in (currentStepIndex + 1) until minOf(currentStepIndex + 4, navigationData.steps.size)) {
                val step = navigationData.steps[i]
                Text(
                    text = "${i + 1}. ${step.instruction}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
