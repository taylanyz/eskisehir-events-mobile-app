package com.eskisehir.eventapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eskisehir.eventapp.data.local.entity.FavoritePlaceEntity
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
fun ProfileScreen(
    onLogoutSuccess: () -> Unit = {},
    onEditProfileClick: () -> Unit = {}
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.observeAsState(initial = false)

    val displayName     by profileViewModel.displayName.collectAsState()
    val email           by profileViewModel.email.collectAsState()
    val profileImageUri by profileViewModel.profileImageUri.collectAsState()
    val interestAreas   by profileViewModel.interestAreas.collectAsState()
    val attendedEvents  by profileViewModel.attendedEvents.collectAsState()
    val goingEvents     by profileViewModel.goingEvents.collectAsState()
    val wantToGoEvents  by profileViewModel.wantToGoEvents.collectAsState()
    val favoriteEvents  by profileViewModel.favoriteEvents.collectAsState()
    val favoritePlaces  by profileViewModel.favoritePlaces.collectAsState()
    val showInterestsDialog by profileViewModel.showInterestsDialog.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.LogoutSuccess) onLogoutSuccess()
    }

    if (showInterestsDialog) {
        InterestsDialog(
            currentSelected = interestAreas,
            onDismiss = { profileViewModel.closeInterestsDialog() },
            onSave    = { profileViewModel.saveInterestAreas(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onEditProfileClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Profili Duzenle")
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ── Kullanici Bilgileri ───────────────────────────────────────
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profil fotografi
                    if (profileImageUri.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profil fotografi",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val initials = displayName
                            ?.split(" ")?.take(2)
                            ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            ?.joinToString("") ?: "?"
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    val nameText = displayName?.takeIf { it.isNotBlank() }
                    if (nameText != null) {
                        Text(nameText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Ad Soyad eklenmemis", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(email ?: "Yukleniyor...", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onEditProfileClick) {
                        Icon(Icons.Default.Edit, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Profili Duzenle")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Ilgi Alanlari ─────────────────────────────────────────────
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ilgi Alanlari", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { profileViewModel.openInterestsDialog() }) { Text("Duzenle") }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (interestAreas.isEmpty()) {
                        Text("Henuz ilgi alani secmediniz.", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            interestAreas.forEach { interest ->
                                AssistChip(onClick = {}, label = { Text(interest) },
                                    leadingIcon = { Icon(Icons.Default.Star, null, Modifier.size(14.dp)) })
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Favori Etkinlikler ────────────────────────────────────────
            ProfileEventSection(
                title = "Favori Etkinliklerim",
                icon = Icons.Default.Favorite,
                events = favoriteEvents,
                emptyMessage = "Henuz favori etkinlik eklemediniz.",
                onRemove = { profileViewModel.removeFavoriteEvent(it) }
            )

            Spacer(Modifier.height(12.dp))

            // ── Favori Mekanlar ───────────────────────────────────────────
            FavoritePlacesSection(
                places = favoritePlaces,
                onRemove = { profileViewModel.removeFavoritePlace(it) }
            )

            Spacer(Modifier.height(12.dp))

            // ── Etkinlik Durumu Kategorileri ──────────────────────────────
            EventCategorySection(title = "Gittim", icon = Icons.Default.CheckCircle,
                events = attendedEvents, emptyMessage = "Henuz bu kategoriye etkinlik eklemediniz.")
            Spacer(Modifier.height(12.dp))
            EventCategorySection(title = "Gidecegim", icon = Icons.Default.CalendarToday,
                events = goingEvents, emptyMessage = "Henuz bu kategoriye etkinlik eklemediniz.")
            Spacer(Modifier.height(12.dp))
            EventCategorySection(title = "Gitmek Istiyorum", icon = Icons.Default.FavoriteBorder,
                events = wantToGoEvents, emptyMessage = "Henuz bu kategoriye etkinlik eklemediniz.")

            Spacer(Modifier.height(24.dp))

            // ── Cikis Butonu ──────────────────────────────────────────────
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onError, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Logout, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onError)
                    Spacer(Modifier.width(8.dp))
                    Text("Cikis Yap", color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Favori Etkinlik Bolumu ─────────────────────────────────────────────────────
@Composable
private fun ProfileEventSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    events: List<Event>,
    emptyMessage: String,
    onRemove: ((Long) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(true) }
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("$title (${events.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                }
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                if (events.isEmpty()) {
                    Text(emptyMessage, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    events.forEach { event ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(4.dp))
                                    Text(event.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(4.dp))
                                    Text(event.venue, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(event.category.displayNameTr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            if (onRemove != null) {
                                IconButton(onClick = { onRemove(event.id) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Close, "Kaldir", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        if (event != events.last()) HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// ── Favori Mekanlar Bolumu ─────────────────────────────────────────────────────
@Composable
private fun FavoritePlacesSection(
    places: List<FavoritePlaceEntity>,
    onRemove: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Favori Mekanlarim (${places.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                }
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                if (places.isEmpty()) {
                    Text("Henuz favori mekan eklemediniz.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    places.forEach { place ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(place.placeName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(place.placeAddress, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(place.placeCategory, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onRemove(place.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Close, "Kaldir", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        if (place != places.last()) HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// ── Durum Kategorisi Bolumu ────────────────────────────────────────────────────
@Composable
private fun EventCategorySection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    events: List<Event>,
    emptyMessage: String
) {
    var expanded by remember { mutableStateOf(true) }
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("$title (${events.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                }
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                if (events.isEmpty()) {
                    Text(emptyMessage, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    events.forEach { event ->
                        EventListItem(event = event)
                        if (event != events.last()) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EventListItem(event: Event) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(event.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(event.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(event.venue, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        SuggestionChip(onClick = {}, label = { Text(event.category.displayNameTr, style = MaterialTheme.typography.labelSmall) })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InterestsDialog(
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
                Text("Birden fazla secim yapabilirsiniz.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ALL_INTERESTS.forEach { interest ->
                        val isSelected = interest in selected
                        FilterChip(
                            selected = isSelected,
                            onClick = { if (isSelected) selected.remove(interest) else selected.add(interest) },
                            label = { Text(interest) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selected.toList()) }) { Text("Kaydet", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Iptal") }
        }
    )
}
