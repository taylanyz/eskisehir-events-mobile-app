package com.eskisehir.eventapp.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.eskisehir.eventapp.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(onBackClick: () -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val displayName by viewModel.displayName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val interestAreas by viewModel.interestAreas.collectAsState()
    val favoriteEvents by viewModel.favoriteEvents.collectAsState()
    val favoritePlaces by viewModel.favoritePlaces.collectAsState()

    // Local editable state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showInterestsDialog by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.saveProfileImageUri(uri.toString())
        }
    }

    val displayUri = selectedImageUri?.toString() ?: profileImageUri.takeIf { it.isNotEmpty() }

    if (showInterestsDialog) {
        InterestsDialog(
            currentSelected = interestAreas,
            onDismiss = { showInterestsDialog = false },
            onSave = { viewModel.saveInterestAreas(it); showInterestsDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profili Duzenle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Profil Fotografi ──────────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                if (displayUri != null) {
                    AsyncImage(
                        model = displayUri,
                        contentDescription = "Profil fotografi",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = displayName
                            ?.split(" ")
                            ?.take(2)
                            ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            ?.joinToString("") ?: "?"
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Camera badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Fotograf sec",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Fotografi Degistir")
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // ── Kullanici Bilgileri (read-only) ───────────────────────────
            SectionHeader(icon = Icons.Default.Person, title = "Hesap Bilgileri")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = displayName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ad Soyad") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // ── Ilgi Alanlari ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(icon = Icons.Default.Star, title = "Ilgi Alanlari")
                TextButton(onClick = { showInterestsDialog = true }) {
                    Text("Duzenle")
                }
            }
            Spacer(Modifier.height(8.dp))
            if (interestAreas.isEmpty()) {
                EmptyHint("Ilgi alani eklenmemis. 'Duzenle' butonuna tiklayin.")
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    interestAreas.forEach { interest ->
                        AssistChip(onClick = {}, label = { Text(interest) })
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // ── Favori Etkinlikler ────────────────────────────────────────
            SectionHeader(icon = Icons.Default.Favorite, title = "Favori Etkinliklerim (${favoriteEvents.size})")
            Spacer(Modifier.height(8.dp))
            if (favoriteEvents.isEmpty()) {
                EmptyHint("Henuz favori etkinlik eklemediniz.")
            } else {
                favoriteEvents.forEach { event ->
                    EditFavoriteEventItem(
                        event = event,
                        onRemove = { viewModel.removeFavoriteEvent(event.id) }
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // ── Favori Mekanlar ───────────────────────────────────────────
            SectionHeader(icon = Icons.Default.Place, title = "Favori Mekanlarim (${favoritePlaces.size})")
            Spacer(Modifier.height(8.dp))
            if (favoritePlaces.isEmpty()) {
                EmptyHint("Henuz favori mekan eklemediniz.")
            } else {
                favoritePlaces.forEach { place ->
                    EditFavoritePlaceItem(
                        place = place,
                        onRemove = { viewModel.removeFavoritePlace(place.id) }
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EditFavoriteEventItem(event: Event, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${event.date}  •  ${event.venue}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    event.category.displayNameTr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, "Kaldir", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EditFavoritePlaceItem(place: FavoritePlaceEntity, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Place, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(place.placeName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(place.placeAddress, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(place.placeCategory, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, "Kaldir", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
