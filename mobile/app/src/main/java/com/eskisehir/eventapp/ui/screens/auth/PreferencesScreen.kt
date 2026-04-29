package com.eskisehir.eventapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.data.model.Category
import com.eskisehir.eventapp.data.model.MobilityPreference
import com.eskisehir.eventapp.data.model.PreferenceUpdateRequest
import com.eskisehir.eventapp.data.model.SensitivityLevel
import com.eskisehir.eventapp.ui.viewmodel.UserViewModel

/**
 * PreferencesScreen for updating user preferences.
 * Includes category selection, budget/crowd tolerance, mobility preference, and sustainability.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    onBackClick: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var budgetSensitivity by remember { mutableStateOf(SensitivityLevel.MEDIUM) }
    var crowdTolerance by remember { mutableStateOf(SensitivityLevel.MEDIUM) }
    var mobilityPreference by remember { mutableStateOf(MobilityPreference.WALKING) }
    var sustainabilityPreference by remember { mutableStateOf(0.5) }
    var maxWalkingMinutes by remember { mutableStateOf(60) }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showMobilityDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tercihlerimi Güncelle") },
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

            // Category Selection
            Text(
                text = "Etkinlik Kategorileri",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            CategorySelector(
                selectedCategories = selectedCategories,
                onCategoriesChange = { selectedCategories = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tags
            Text(
                text = "Etiketler",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = selectedTags.joinToString(", "),
                onValueChange = {},
                label = { Text("Etiketler (virgülle ayrılmış)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                enabled = false
            )

            // Budget Sensitivity
            Text(
                text = "Bütçe Hassasiyeti: ${budgetSensitivity.displayNameTr}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SensitivityLevel.values().forEach { level ->
                    Button(
                        onClick = { budgetSensitivity = level },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (budgetSensitivity == level) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(level.displayNameTr)
                    }
                }
            }

            // Crowd Tolerance
            Text(
                text = "Kalabalık Toleransı: ${crowdTolerance.displayNameTr}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SensitivityLevel.values().forEach { level ->
                    Button(
                        onClick = { crowdTolerance = level },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (crowdTolerance == level) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(level.displayNameTr)
                    }
                }
            }

            // Mobility Preference
            Text(
                text = "Ulaşım Tercihi: ${mobilityPreference.displayNameTr}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MobilityPreference.values().forEach { pref ->
                    Button(
                        onClick = { mobilityPreference = pref },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mobilityPreference == pref) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(pref.displayNameTr, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Sustainability Preference Slider
            Text(
                text = "Sürdürülebilirlik Tercihi: ${String.format("%.1f", sustainabilityPreference)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Slider(
                value = sustainabilityPreference.toFloat(),
                onValueChange = { sustainabilityPreference = it.toDouble() },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Max Walking Minutes
            Text(
                text = "Maksimum Yürüyüş Süresi (dakika): $maxWalkingMinutes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Slider(
                value = maxWalkingMinutes.toFloat(),
                onValueChange = { maxWalkingMinutes = it.toInt() },
                valueRange = 1f..600f,
                steps = 59,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Save Button
            Button(
                onClick = {
                    val request = PreferenceUpdateRequest(
                        preferredCategories = selectedCategories.toList(),
                        preferredTags = selectedTags.toList(),
                        budgetSensitivity = budgetSensitivity.name,
                        crowdTolerance = crowdTolerance.name,
                        mobilityPreference = mobilityPreference.name,
                        sustainabilityPreference = sustainabilityPreference,
                        maxWalkingMinutes = maxWalkingMinutes
                    )
                    viewModel.updatePreferences(request)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Tercihleri Kaydet")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategorySelector(
    selectedCategories: Set<String>,
    onCategoriesChange: (Set<String>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Category.values().forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedCategories.contains(category.name),
                    onCheckedChange = { isChecked ->
                        val newSet = selectedCategories.toMutableSet()
                        if (isChecked) {
                            newSet.add(category.name)
                        } else {
                            newSet.remove(category.name)
                        }
                        onCategoriesChange(newSet)
                    }
                )
                Text(
                    text = category.displayNameTr,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
