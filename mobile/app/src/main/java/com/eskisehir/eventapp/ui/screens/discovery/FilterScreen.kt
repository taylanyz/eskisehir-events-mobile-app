package com.eskisehir.eventapp.ui.screens.discovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eskisehir.eventapp.data.model.AdvancedFilterRequestDto

/**
 * Advanced filter screen for Phase 5.3.
 * Allows users to filter POIs by price, distance, rating, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onFilterApply: (AdvancedFilterRequestDto) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var maxDistance by remember { mutableStateOf("") }
    var minRating by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Filters") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        minPrice = ""
                        maxPrice = ""
                        maxDistance = ""
                        minRating = 0f
                    }) {
                        Icon(Icons.Default.Clear, "Clear filters")
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
            // Price range filter
            item {
                Text("Price Range (₺)", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = minPrice,
                        onValueChange = { minPrice = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )
                    TextField(
                        value = maxPrice,
                        onValueChange = { maxPrice = it },
                        label = { Text("Max") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )
                }
            }

            // Distance filter
            item {
                Text("Max Distance (km)", style = MaterialTheme.typography.labelLarge)
                TextField(
                    value = maxDistance,
                    onValueChange = { maxDistance = it },
                    label = { Text("Distance") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            }

            // Rating filter
            item {
                Text("Minimum Rating", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Slider(
                        value = minRating,
                        onValueChange = { minRating = it },
                        valueRange = 0f..5f,
                        steps = 4,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${minRating.toInt()}/5")
                }
            }

            // Apply button
            item {
                Button(
                    onClick = {
                        val filter = AdvancedFilterRequestDto(
                            minPrice = minPrice.toDoubleOrNull(),
                            maxPrice = maxPrice.toDoubleOrNull(),
                            maxDistance = maxDistance.toDoubleOrNull(),
                            minRating = minRating.toDouble()
                        )
                        onFilterApply(filter)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}
