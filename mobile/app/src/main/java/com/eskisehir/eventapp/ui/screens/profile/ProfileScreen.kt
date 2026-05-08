package com.eskisehir.eventapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eskisehir.eventapp.data.model.Event
import com.eskisehir.eventapp.ui.viewmodel.AuthState
import com.eskisehir.eventapp.ui.viewmodel.AuthViewModel
import com.eskisehir.eventapp.ui.viewmodel.ProfileViewModel

val ALL_INTERESTS = listOf(
    "Muzik", "Spor", "Sanat", "Tiyatro", "Sinema",
    "Teknoloji", "Egitim", "Yemek", "Festival", "Gezi", "Kultur", "Workshop"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(onLogoutSuccess: () -> Unit = {}) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.observeAsState(initial = false)

    val displayName by profileViewModel.displayName.collectAsState()
    val email by profileViewModel.email.collectAsState()
    val interestAreas by profileViewModel.interestAreas.collectAsState()
    val attendedEvents by profileViewModel.attendedEvents.collectAsState()
    val goingEvents by profileViewModel.goingEvents.collectAsState()
    val wantToGoEvents by profileViewModel.wantToGoEvents.collectAsState()
    val showInterestsDialog by profileViewModel.showInterestsDialog.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.LogoutSuccess) {
            onLogoutSuccess()
        }
    }

    if (showInterestsDialog) {
        InterestsDialog(
            currentSelected = interestAreas,
            onDismiss = { profileViewModel.closeInterestsDialog() },
            onSave = { profileViewModel.saveInterestAreas(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ─── Kullanici Bilgileri ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))

                    val nameText = displayName?.takeIf { it.isNotBlank() }
                    if (nameText != null) {
                        Text(
                            text = nameText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Ad Soyad bilgisi eklenmemis",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = email ?: "Yukleniyor...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Ilgi Alanlari ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Ilgi Alanlari",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { profileViewModel.openInterestsDialog() }) {
                            Text("Duzenle")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (interestAreas.isEmpty()) {
                        Text(
                            "Henuz ilgi alani secmediniz. Eklemek icin 'Duzenle' butonuna tiklayin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            interestAreas.forEach { interest ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(interest) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Star, null, Modifier.size(14.dp))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Etkinlik Kategorileri ────────────────────────────────────
            EventCategorySection(
                title = "Gittim",
                icon = Icons.Default.CheckCircle,
                events = attendedEvents,
                emptyMessage = "Henuz bu kategoriye etkinlik eklemediniz."
            )

            Spacer(Modifier.height(12.dp))

            EventCategorySection(
                title = "Gidecegim",
                icon = Icons.Default.CalendarToday,
                events = goingEvents,
                emptyMessage = "Henuz bu kategoriye etkinlik eklemediniz."
            )

            Spacer(Modifier.height(12.dp))

            EventCategorySection(
                title = "Gitmek Istiyorum",
                icon = Icons.Default.FavoriteBorder,
                events = wantToGoEvents,
                emptyMessage = "Henuz bu kategoriye etkinlik eklemediniz."
            )

            Spacer(Modifier.height(24.dp))

            // ─── Cikis Butonu ─────────────────────────────────────────────
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onError,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Logout, contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onError)
                    Spacer(Modifier.width(8.dp))
                    Text("Cikis Yap", color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EventCategorySection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    events: List<Event>,
    emptyMessage: String
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$title (${events.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Daralt" else "Genislet"
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                if (events.isEmpty()) {
                    Text(
                        emptyMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    events.forEach { event ->
                        EventListItem(event = event)
                        if (event != events.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventListItem(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null,
                    Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(
                    event.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.LocationOn, null,
                    Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(
                    event.venue,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        SuggestionChip(
            onClick = {},
            label = { Text(event.category.displayNameTr, style = MaterialTheme.typography.labelSmall) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestsDialog(
    currentSelected: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val selected = remember { mutableStateListOf<String>().also { it.addAll(currentSelected) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ilgi Alanlarini Sec", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Birden fazla secim yapabilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ALL_INTERESTS.forEach { interest ->
                        val isSelected = interest in selected
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selected.remove(interest)
                                else selected.add(interest)
                            },
                            label = { Text(interest) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selected.toList()) }) {
                Text("Kaydet", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Iptal") }
        }
    )
}
