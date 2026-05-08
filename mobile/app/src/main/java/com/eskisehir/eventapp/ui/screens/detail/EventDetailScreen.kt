package com.eskisehir.eventapp.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eskisehir.eventapp.data.local.entity.CommentEntity
import com.eskisehir.eventapp.data.model.SampleData
import com.eskisehir.eventapp.data.model.UserEventStatus
import com.eskisehir.eventapp.ui.viewmodel.EventInteractionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(eventId: Long, onBackClick: () -> Unit) {
    val event = SampleData.events.find { it.id == eventId }
    val viewModel: EventInteractionViewModel = hiltViewModel()

    val comments by viewModel.comments.collectAsState()
    val currentStatus by viewModel.currentEventStatus.collectAsState()
    val commentText by viewModel.commentText.collectAsState()
    val currentUserId by viewModel.userId.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadCommentsForEvent(eventId)
        viewModel.loadStatusForEvent(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event?.name ?: "Etkinlik") },
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
        if (event == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Etkinlik bulunamadi") }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                SuggestionChip(onClick = {}, label = { Text(event.category.displayNameTr) })
                Spacer(Modifier.height(8.dp))
                Text(event.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                InfoRow(icon = Icons.Default.LocationOn, text = event.venue)
                Spacer(Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.CalendarToday, text = event.date)
                Spacer(Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.AttachMoney,
                    text = if (event.price == 0.0) "Ucretsiz" else "${event.price.toInt()} TL"
                )

                Spacer(Modifier.height(16.dp))
                Text("Aciklama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))
                Text("Etiketler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    event.tags.forEach { tag ->
                        AssistChip(onClick = {}, label = { Text(tag) })
                    }
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                // Etkinlik Durumu
                Text(
                    "Bu Etkinlik Icin Durumun",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                val statusOptions = listOf(
                    UserEventStatus.ATTENDED   to "Gittim",
                    UserEventStatus.GOING      to "Gidecegim",
                    UserEventStatus.WANT_TO_GO to "Gitmek Istiyorum"
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statusOptions.forEach { (status, label) ->
                        FilterChip(
                            selected = currentStatus == status,
                            onClick = {
                                viewModel.setEventStatus(
                                    eventId,
                                    if (currentStatus == status) UserEventStatus.NONE else status
                                )
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                // Yorumlar
                Text(
                    "Yorumlar (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = viewModel::onCommentTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Yorumunuzu yazin...") },
                    maxLines = 3,
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.submitComment(eventId) },
                            enabled = commentText.isNotBlank()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Gonder",
                                tint = if (commentText.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                if (comments.isEmpty()) {
                    Text(
                        "Henuz yorum yapilmamis. Ilk yorumu sen yap!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    comments.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            isOwner = comment.userId == currentUserId,
                            onDelete = { viewModel.deleteComment(comment.id) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CommentItem(comment: CommentEntity, isOwner: Boolean, onDelete: () -> Unit) {
    val dateStr = remember(comment.timestamp) {
        SimpleDateFormat("dd MMM yyyy HH:mm", Locale("tr")).format(Date(comment.timestamp))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, null, Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        comment.userDisplayName.ifEmpty { comment.userEmail },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (isOwner) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, "Sil", Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(comment.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                dateStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
