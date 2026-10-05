package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Moment
import com.example.model.User
import com.example.ui.components.LuxuryButton
import com.example.ui.components.UserAvatarWithFrame
import com.example.ui.components.VipBadge
import com.example.ui.theme.*

@Composable
fun MomentsScreen(
    currentUser: User,
    moments: List<Moment>,
    onToggleLike: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onDeleteMoment: (String) -> Unit,
    onPublishMoment: (String, String?) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var commentingMomentId by remember { mutableStateOf<String?>(null) }
    var commentText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ Community Moments",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("publish_moment_btn")
                    ) {
                        Text("+ Post", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            if (moments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No moments posted yet. Be the first to share! 🌟", color = TextMuted)
                    }
                }
            } else {
                items(moments) { moment ->
                    MomentCard(
                        moment = moment,
                        currentUserId = currentUser.id,
                        onLike = { onToggleLike(moment.id) },
                        onComment = { commentingMomentId = moment.id },
                        onDelete = { onDeleteMoment(moment.id) }
                    )
                }
            }
        }

        // Post Moment Dialog with zero-permission Photo Picker
        if (showCreateDialog) {
            CreateMomentDialog(
                onDismiss = { showCreateDialog = false },
                onPublish = { text, uri ->
                    showCreateDialog = false
                    onPublishMoment(text, uri)
                }
            )
        }

        // Add Comment Dialog
        if (commentingMomentId != null) {
            AlertDialog(
                onDismissRequest = { commentingMomentId = null },
                title = { Text("Add Comment", color = TextPrimary) },
                text = {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Write a friendly comment...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentingMomentId!!, commentText.trim())
                                commentText = ""
                                commentingMomentId = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                    ) {
                        Text("Comment")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { commentingMomentId = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = RoyalVioletCard
            )
        }
    }
}

@Composable
fun MomentCard(
    moment: Moment,
    currentUserId: Long,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RoyalVioletBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatarWithFrame(
                        avatarUrl = moment.authorAvatar,
                        displayName = moment.authorName,
                        vipTier = moment.authorVip,
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = moment.authorName,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            VipBadge(vipTier = moment.authorVip)
                        }
                        Text(
                            text = "Bisma Creator",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                if (moment.authorId == currentUserId) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Moment",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text
            Text(
                text = moment.text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Optional Image
            if (!moment.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = moment.imageUri,
                    contentDescription = "Moment Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onLike)
                        .padding(end = 16.dp)
                ) {
                    Icon(
                        imageVector = if (moment.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (moment.isLikedByMe) NeonPink else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${moment.likesCount}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onComment)
                        .padding(end = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${moment.comments.size}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Comments list if any
            if (moment.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoyalVioletDark)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    moment.comments.take(3).forEach { c ->
                        Row {
                            Text(
                                text = "${c.authorName}: ",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(text = c.text, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateMomentDialog(
    onDismiss: () -> Unit,
    onPublish: (String, String?) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // Modern zero-permission Android Photo Picker!
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Moment", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = { Text("Share what's on your mind...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("moment_caption_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Image Selection button
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedImageUri != null) "Photo Selected ✓" else "Attach Photo from Gallery",
                        color = GoldAccent
                    )
                }

                if (selectedImageUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            LuxuryButton(
                text = "Publish",
                onClick = {
                    if (caption.isNotBlank() || selectedImageUri != null) {
                        onPublish(caption.trim(), selectedImageUri)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = RoyalVioletCard
    )
}
