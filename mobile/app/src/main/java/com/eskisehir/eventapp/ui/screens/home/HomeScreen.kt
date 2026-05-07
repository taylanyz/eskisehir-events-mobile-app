package com.eskisehir.eventapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eskisehir.eventapp.data.model.Event
import com.eskisehir.eventapp.data.model.SampleData
import com.eskisehir.eventapp.ui.components.EventCard

/**
 * Home Screen - Shows a list of upcoming events.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onEventClick: (Long) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Eskişehir Etkinlikleri", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SampleData.events) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}
