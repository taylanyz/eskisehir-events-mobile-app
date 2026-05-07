package com.eskisehir.eventapp.ui.screens.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.data.model.Category
import com.eskisehir.eventapp.data.model.RecommendationRequest
import com.eskisehir.eventapp.ui.viewmodel.RecommendationViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(onPoiClick: (Long) -> Unit) {
    val viewModel: RecommendationViewModel = hiltViewModel()
    val recommendations by viewModel.recommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Tercih state'i
    var selectedCategories by remember { mutableStateOf(setOf(Category.CONCERT, Category.THEATER, Category.FESTIVAL)) }
    var maxPrice by remember { mutableStateOf("500") }
    var latitude by remember { mutableStateOf("39.7667") }
    var longitude by remember { mutableStateOf("30.5256") }

    LaunchedEffect(Unit) {
        // İlk yükleme - önerileri hemen getir
        val request = RecommendationRequest(
            preferredCategories = selectedCategories.toList(),
            preferredTags = emptyList(),
            maxPrice = maxPrice.toDoubleOrNull() ?: 500.0,
            limit = 10,
            latitude = latitude.toDoubleOrNull() ?: 39.7667,
            longitude = longitude.toDoubleOrNull() ?: 30.5256
        )
        viewModel.loadRecommendations(request)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🎭 Etkinlik Önerileri",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        val request = RecommendationRequest(
                            preferredCategories = selectedCategories.toList(),
                            preferredTags = emptyList(),
                            maxPrice = maxPrice.toDoubleOrNull() ?: 500.0,
                            limit = 10,
                            latitude = latitude.toDoubleOrNull() ?: 39.7667,
                            longitude = longitude.toDoubleOrNull() ?: 30.5256
                        )
                        viewModel.loadRecommendations(request)
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Yenile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tercih Formu
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "⚙️ Tercihler",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Kategori seçimi
                        Text(
                            text = "Kategoriler:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(Category.values()) { category ->
                                FilterChip(
                                    selected = selectedCategories.contains(category),
                                    onClick = {
                                        selectedCategories = if (selectedCategories.contains(category)) {
                                            selectedCategories - category
                                        } else {
                                            selectedCategories + category
                                        }
                                    },
                                    label = { Text(category.displayNameTr, maxLines = 1) }
                                )
                            }
                        }

                        // Max fiyat
                        Text(
                            text = "Max Fiyat: ₺$maxPrice",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = maxPrice.toFloatOrNull() ?: 500f,
                            onValueChange = { maxPrice = it.toInt().toString() },
                            valueRange = 0f..1000f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Arama Butonu
                        Button(
                            onClick = {
                                val request = RecommendationRequest(
                                    preferredCategories = selectedCategories.toList(),
                                    preferredTags = emptyList(),
                                    maxPrice = maxPrice.toDoubleOrNull() ?: 500.0,
                                    limit = 10,
                                    latitude = latitude.toDoubleOrNull() ?: 39.7667,
                                    longitude = longitude.toDoubleOrNull() ?: 30.5256
                                )
                                viewModel.loadRecommendations(request)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("🔍 Etkinlik Ara")
                        }
                    }
                }
            }

            // Loading State
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Etkinlikler yükleniyor...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Error Message
            if (!errorMessage.isNullOrBlank() && !isLoading) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "⚠️ ${errorMessage}",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Etkinlikler
            if (recommendations.isNotEmpty()) {
                item {
                    Text(
                        text = "🎪 Önerilen Etkinlikler (${recommendations.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(recommendations) { event ->
                    EventCard(event = event, onEventClick = onPoiClick)
                }
            } else if (!isLoading && errorMessage.isNullOrBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Uygun etkinlik bulunamadı. Tercihlerini değiştir! 🎯",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun EventCard(event: com.eskisehir.eventapp.data.model.PoiResponse, onEventClick: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                    Text(
                        text = event.category.displayNameTr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (event.price != null && event.price!! > 0) {
                    Text(
                        text = "₺${event.price.toInt()}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Ücretsiz",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Açıklama
            Text(
                text = event.description ?: "Açıklama yok",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            // Mekan ve Tarih
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${event.district ?: "Bilinmiyor"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                event.date?.let {
                    Text(
                        text = "📅 $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tags
            if (!event.tags.isNullOrEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(event.tags.take(3)) { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }
    }
}
