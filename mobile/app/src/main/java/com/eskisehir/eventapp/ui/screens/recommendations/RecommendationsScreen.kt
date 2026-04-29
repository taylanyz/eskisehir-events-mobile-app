package com.eskisehir.eventapp.ui.screens.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.data.model.PoiResponse
import com.eskisehir.eventapp.ui.components.PoiCard
import com.eskisehir.eventapp.ui.viewmodel.RecommendationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(onPoiClick: (Long) -> Unit) {
    val viewModel: RecommendationViewModel = hiltViewModel()
    val recommendations by viewModel.recommendations.collectAsState()
    val trending by viewModel.trending.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val interactionMessage by viewModel.interactionMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val trackedViews = remember { mutableStateListOf<Long>() }

    var feedbackPoi by remember { mutableStateOf<PoiResponse?>(null) }
    var feedbackComment by remember { mutableStateOf("") }
    var feedbackRating by remember { mutableStateOf<Int?>(null) }
    var helpfulChoice by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val request = viewModel.defaultRequest()
        viewModel.loadRecommendations(request)
        viewModel.loadTrending()
    }

    LaunchedEffect(interactionMessage) {
        interactionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearInteractionMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Öneriler", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        val request = viewModel.defaultRequest()
                        viewModel.loadRecommendations(request)
                        viewModel.loadTrending()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            val currentErrorMessage = errorMessage
            if (!currentErrorMessage.isNullOrBlank()) {
                Text(
                    text = currentErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Text("Trend olan mekanlar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(trending) { poi ->
                    RecommendationPoiSection(
                        poi = poi,
                        trackedViews = trackedViews,
                        viewModel = viewModel,
                        onPoiClick = onPoiClick,
                        onFeedbackClick = {
                            feedbackPoi = poi
                            feedbackComment = ""
                            feedbackRating = null
                            helpfulChoice = null
                        },
                        modifier = Modifier.width(280.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Kişisel öneriler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(recommendations) { poi ->
                    RecommendationPoiSection(
                        poi = poi,
                        trackedViews = trackedViews,
                        viewModel = viewModel,
                        onPoiClick = onPoiClick,
                        onFeedbackClick = {
                            feedbackPoi = poi
                            feedbackComment = ""
                            feedbackRating = null
                            helpfulChoice = null
                        }
                    )
                }
            }
        }
    }

    feedbackPoi?.let { poi ->
        AlertDialog(
            onDismissRequest = { feedbackPoi = null },
            title = { Text(text = "${poi.name} için geri bildirim") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Puan ver")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { rating ->
                            FilterChip(
                                selected = feedbackRating == rating,
                                onClick = { feedbackRating = rating },
                                label = { Text(rating.toString()) }
                            )
                        }
                    }

                    Text("Hızlı seçim")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = helpfulChoice == true,
                            onClick = { helpfulChoice = true },
                            label = { Text("Helpful") }
                        )
                        FilterChip(
                            selected = helpfulChoice == false,
                            onClick = { helpfulChoice = false },
                            label = { Text("Not Helpful") }
                        )
                    }

                    OutlinedTextField(
                        value = feedbackComment,
                        onValueChange = { feedbackComment = it },
                        label = { Text("Yorum") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logFeedback(
                        poiId = poi.id,
                        rating = feedbackRating,
                        isHelpful = helpfulChoice,
                        comment = feedbackComment
                    )
                    feedbackPoi = null
                }) {
                    Text("Gönder")
                }
            },
            dismissButton = {
                TextButton(onClick = { feedbackPoi = null }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun RecommendationPoiSection(
    poi: PoiResponse,
    trackedViews: MutableList<Long>,
    viewModel: RecommendationViewModel,
    onPoiClick: (Long) -> Unit,
    onFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(poi.id) {
        if (!trackedViews.contains(poi.id)) {
            trackedViews.add(poi.id)
            viewModel.logView(poi.id)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        PoiCard(
            poi = poi,
            onClick = {
                viewModel.logOpen(poi.id)
                onPoiClick(poi.id)
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { viewModel.logBookmark(poi.id) }) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kaydet")
            }
            OutlinedButton(onClick = { viewModel.logShare(poi.id) }) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Paylaş")
            }
            FilledTonalButton(onClick = onFeedbackClick) {
                Icon(Icons.Default.Feedback, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Feedback")
            }
        }
    }
}
