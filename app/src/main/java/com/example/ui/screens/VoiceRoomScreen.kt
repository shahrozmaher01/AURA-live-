package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BismaAudioManager
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun VoiceRoomScreen(
    room: VoiceRoom,
    currentUser: User,
    messages: List<RoomChatMessage>,
    activeGiftAnimation: Gift?,
    gifts: List<Gift>,
    audioManager: BismaAudioManager,
    onLeaveRoom: () -> Unit,
    onJoinSeat: (Int) -> Unit,
    onLeaveSeat: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendGift: (Gift, Int, String?) -> Boolean,
    onPlayGame: (String, Long) -> Pair<Boolean, Long>,
    onToggleLockSeat: (Int) -> Unit,
    onToggleMuteSeat: (Int) -> Unit,
    onKickUser: (Int) -> Unit,
    onOpenProfile: (Long) -> Unit,
    onToggleFollowHost: (Long) -> Unit,
    isFollowingHost: Boolean
) {
    val isMicMuted by audioManager.isMicrophoneMuted.collectAsState()
    val isSpeakerMuted by audioManager.isSpeakerMuted.collectAsState()
    val voiceLevel by audioManager.currentVoiceLevel.collectAsState()
    val activeSfx by audioManager.activeSfxEffect.collectAsState()

    val isHost = room.hostUserId == currentUser.id
    val mySeat = room.seats.find { it.userId == currentUser.id }
    val isOnSeat = mySeat != null

    var chatInput by remember { mutableStateOf("") }
    var showGiftDrawer by remember { mutableStateOf(false) }
    var showGamesDrawer by remember { mutableStateOf(false) }
    var showMusicDrawer by remember { mutableStateOf(false) }
    var selectedSeatForAction by remember { mutableStateOf<RoomSeat?>(null) }
    var showRoomInfoDialog by remember { mutableStateOf(false) }

    // Start/stop voice capture according to seat status
    LaunchedEffect(isOnSeat) {
        if (isOnSeat) {
            audioManager.startVoiceCapture(hasMicPermission = true)
        } else {
            audioManager.stopVoiceCapture()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B0B3B),
                        Color(0xFF0F0721),
                        Color(0xFF080314)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Room Header
            VoiceRoomHeader(
                room = room,
                isFollowingHost = isFollowingHost,
                onFollowHost = { onToggleFollowHost(room.hostUserId) },
                onInfoClick = { showRoomInfoDialog = true },
                onLeaveRoom = onLeaveRoom
            )

            // Announcement ticker
            if (room.announcement.isNotBlank()) {
                RoomAnnouncementBar(announcement = room.announcement)
            }

            // Seats Grid Section
            // Dynamic column count: 4 columns for 8/10/20 seats, 5 columns for 15/20 seats
            val columns = if (room.seatCount == 15) 5 else 4

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(room.seats) { seat ->
                        VoiceSeatItem(
                            seat = seat,
                            isHostSeat = seat.seatIndex == 0,
                            isMySeat = seat.userId == currentUser.id,
                            myVoiceLevel = if (seat.userId == currentUser.id && !isMicMuted) voiceLevel else 0f,
                            onClick = {
                                if (seat.userId == null) {
                                    // Empty seat -> sit down!
                                    onJoinSeat(seat.seatIndex)
                                } else {
                                    // Occupied seat -> action dialog
                                    selectedSeatForAction = seat
                                }
                            }
                        )
                    }
                }
            }

            // SFX Toast Notification Banner if sound effect triggered
            if (activeSfx != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NeonMagenta.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "🔊 $activeSfx",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Live Chat Messages List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .padding(horizontal = 12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(messages.reversed()) { msg ->
                        RoomChatMessageItem(
                            message = msg,
                            onUserClick = { onOpenProfile(msg.senderId) }
                        )
                    }
                }
            }

            // Bottom Control Bar
            RoomBottomControlBar(
                isOnSeat = isOnSeat,
                isMuted = isMicMuted,
                chatInput = chatInput,
                onChatInputChange = { chatInput = it },
                onSendChat = {
                    if (chatInput.isNotBlank()) {
                        onSendMessage(chatInput.trim())
                        chatInput = ""
                    }
                },
                onToggleMic = { audioManager.toggleMicrophone(isOnSeat) },
                onOpenGifts = { showGiftDrawer = true },
                onOpenGames = { showGamesDrawer = true },
                onOpenMusic = { showMusicDrawer = true },
                onLeaveSeat = onLeaveSeat
            )
        }

        // Full Screen Luxury Gift Animation Overlay
        if (activeGiftAnimation != null) {
            LuxuryGiftOverlay(gift = activeGiftAnimation)
        }

        // Seat Action Dialog (User Profile / Host Controls)
        if (selectedSeatForAction != null) {
            val seat = selectedSeatForAction!!
            SeatActionDialog(
                seat = seat,
                isHost = isHost,
                isMySeat = seat.userId == currentUser.id,
                onDismiss = { selectedSeatForAction = null },
                onOpenProfile = {
                    val uid = seat.userId
                    selectedSeatForAction = null
                    if (uid != null) onOpenProfile(uid)
                },
                onLeaveSeat = {
                    selectedSeatForAction = null
                    onLeaveSeat()
                },
                onToggleMute = {
                    selectedSeatForAction = null
                    onToggleMuteSeat(seat.seatIndex)
                },
                onToggleLock = {
                    selectedSeatForAction = null
                    onToggleLockSeat(seat.seatIndex)
                },
                onKick = {
                    selectedSeatForAction = null
                    onKickUser(seat.seatIndex)
                },
                onSendGiftToUser = {
                    selectedSeatForAction = null
                    showGiftDrawer = true
                }
            )
        }

        // Gift Drawer Modal Sheet
        if (showGiftDrawer) {
            GiftDrawerSheet(
                gifts = gifts,
                userCoins = currentUser.coins,
                onDismiss = { showGiftDrawer = false },
                onSendGift = { gift, count ->
                    val success = onSendGift(gift, count, room.hostName)
                    if (success) {
                        showGiftDrawer = false
                    }
                }
            )
        }

        // Room Games Drawer Modal Sheet (Lucky 77, Greedy Cat, Lucky Box, Greedy Lion)
        if (showGamesDrawer) {
            RoomGamesDrawerSheet(
                userCoins = currentUser.coins,
                onDismiss = { showGamesDrawer = false },
                onPlayGame = onPlayGame
            )
        }

        // Room Music & Sound Effects Drawer
        if (showMusicDrawer) {
            RoomMusicDrawerSheet(
                audioManager = audioManager,
                onDismiss = { showMusicDrawer = false }
            )
        }

        // Room Info Dialog
        if (showRoomInfoDialog) {
            AlertDialog(
                onDismissRequest = { showRoomInfoDialog = false },
                title = { Text(room.title, color = TextPrimary) },
                text = {
                    Column {
                        Text("Room ID: ${room.id}", color = GoldAccent, fontWeight = FontWeight.Bold)
                        Text("Host: ${room.hostName}", color = TextSecondary)
                        Text("Seat Layout: ${room.seatCount} Seats", color = NeonCyan)
                        Text("Category: ${room.category.displayName}", color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Announcement:", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(room.announcement, color = TextSecondary)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRoomInfoDialog = false }) {
                        Text("Close", color = NeonMagenta)
                    }
                },
                containerColor = RoyalVioletCard
            )
        }
    }
}

@Composable
fun VoiceRoomHeader(
    room: VoiceRoom,
    isFollowingHost: Boolean,
    onFollowHost: () -> Unit,
    onInfoClick: () -> Unit,
    onLeaveRoom: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Room Identity Tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(RoyalVioletCard)
                .clickable(onClick = onInfoClick)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            UserAvatarWithFrame(
                avatarUrl = room.hostAvatar,
                displayName = room.hostName,
                vipTier = room.hostVip,
                size = 32.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = room.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "ID: ${room.id} • ${room.activeMembersCount} online",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(6.dp))

            // Follow Host Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFollowingHost) RoyalVioletBorder else NeonPink)
                    .clickable(onClick = onFollowHost)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isFollowingHost) "Joined" else "+ Follow",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Close / Leave Button
        IconButton(
            onClick = onLeaveRoom,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RoyalVioletCard)
                .testTag("leave_room_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Leave Room",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun RoomAnnouncementBar(announcement: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(RoyalVioletCard.copy(alpha = 0.7f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "📢", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = announcement,
            color = GoldAccent,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VoiceSeatItem(
    seat: RoomSeat,
    isHostSeat: Boolean,
    isMySeat: Boolean,
    myVoiceLevel: Float,
    onClick: () -> Unit
) {
    val isSpeaking = seat.isSpeaking || (isMySeat && myVoiceLevel > 0.15f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
            .testTag("voice_seat_${seat.seatNumber}")
    ) {
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            if (seat.userId != null) {
                UserAvatarWithFrame(
                    avatarUrl = seat.userAvatar,
                    displayName = seat.userName ?: "User",
                    frameId = seat.frameId,
                    vipTier = seat.vipTier,
                    size = 50.dp,
                    isSpeaking = isSpeaking
                )

                // Host Crown indicator on Seat 1
                if (isHostSeat) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-6).dp)
                    ) {
                        Text(text = "👑", fontSize = 12.sp)
                    }
                }

                // Mute badge on avatar
                if (seat.isMuted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MutedRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MicOff,
                            contentDescription = "Muted",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            } else {
                // Empty Seat
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SeatEmptyBg)
                        .border(1.dp, SeatBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (seat.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "${seat.seatNumber}",
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = seat.userName ?: "Seat ${seat.seatNumber}",
            color = if (seat.userId != null) TextPrimary else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (seat.userId != null) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RoomChatMessageItem(
    message: RoomChatMessage,
    onUserClick: () -> Unit
) {
    when (message.type) {
        MessageType.TEXT -> {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x55170E32))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (message.senderVip != VipTier.NONE) {
                    VipBadge(vipTier = message.senderVip, modifier = Modifier.padding(end = 4.dp))
                }
                Text(
                    text = "${message.senderName}: ",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable(onClick = onUserClick)
                )
                Text(
                    text = message.content,
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            }
        }
        MessageType.GIFT -> {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0x99E040FB), Color(0x667C4DFF))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🎁 ", fontSize = 13.sp)
                Text(
                    text = "${message.senderName} ",
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "sent ${message.giftCount}x ${message.giftName ?: "Gift"} to ${message.targetUserName ?: "Host"}",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
        MessageType.ENTER, MessageType.LEAVE, MessageType.SYSTEM -> {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x44000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ ${message.senderName} ${message.content}",
                    color = GoldAccent.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun RoomBottomControlBar(
    isOnSeat: Boolean,
    isMuted: Boolean,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendChat: () -> Unit,
    onToggleMic: () -> Unit,
    onOpenGifts: () -> Unit,
    onOpenGames: () -> Unit,
    onOpenMusic: () -> Unit,
    onLeaveSeat: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chat text input field
        OutlinedTextField(
            value = chatInput,
            onValueChange = onChatInputChange,
            placeholder = { Text("Say something...", fontSize = 12.sp, color = TextMuted) },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("room_chat_input"),
            shape = RoundedCornerShape(23.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RoyalVioletCard,
                unfocusedContainerColor = RoyalVioletCard,
                focusedBorderColor = NeonMagenta,
                unfocusedBorderColor = RoyalVioletBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            trailingIcon = {
                if (chatInput.isNotBlank()) {
                    IconButton(onClick = onSendChat, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = NeonMagenta,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Mic toggle button (available if on seat)
        if (isOnSeat) {
            IconButton(
                onClick = onToggleMic,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isMuted) MutedRed else NeonPurple)
                    .testTag("room_mic_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        // Room Games button
        IconButton(
            onClick = onOpenGames,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(RoyalVioletCard)
                .testTag("room_games_btn")
        ) {
            Text(text = "🎰", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Sound Effects & Music button
        IconButton(
            onClick = onOpenMusic,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(RoyalVioletCard)
                .testTag("room_music_sfx_btn")
        ) {
            Text(text = "🎵", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Luxury Gift button
        IconButton(
            onClick = onOpenGifts,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(NeonMagenta, Color(0xFF7C4DFF)))
                )
                .testTag("room_gift_drawer_btn")
        ) {
            Text(text = "🎁", fontSize = 20.sp)
        }
    }
}

@Composable
fun LuxuryGiftOverlay(gift: Gift) {
    val scale = remember { Animatable(0.2f) }
    LaunchedEffect(gift) {
        scale.animateTo(
            targetValue = 1.3f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(1.0f, animationSpec = tween(400))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x44000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            Text(
                text = gift.iconEmoji,
                fontSize = 110.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(gift.animationColor)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "✨ ${gift.name} ✨",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftDrawerSheet(
    gifts: List<Gift>,
    userCoins: Long,
    onDismiss: () -> Unit,
    onSendGift: (Gift, Int) -> Unit
) {
    var selectedGift by remember { mutableStateOf(gifts.firstOrNull()) }
    var selectedCount by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf(GiftCategory.POPULAR) }

    val filteredGifts = remember(selectedTab, gifts) {
        gifts.filter { it.category == selectedTab }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RoyalVioletSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = RoyalVioletBorder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Category Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GiftCategory.entries.forEach { cat ->
                    val isSelected = selectedTab == cat
                    Text(
                        text = cat.title,
                        color = if (isSelected) NeonMagenta else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { selectedTab = cat }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gifts Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredGifts) { gift ->
                    val isSelected = selectedGift?.id == gift.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) RoyalVioletCardAlt else Color.Transparent)
                            .border(
                                1.5.dp,
                                if (isSelected) GoldAccent else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedGift = gift }
                            .padding(8.dp)
                            .testTag("gift_item_${gift.id}")
                    ) {
                        Text(text = gift.iconEmoji, fontSize = 32.sp)
                        Text(
                            text = gift.name,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 10.sp)
                            Text(
                                text = "${gift.priceCoins}",
                                color = GoldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action row: Coin balance, combo count, send button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Balance: 🪙 %,d".format(userCoins), color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Combo Counts (1, 10, 66, 100)
                    listOf(1, 10, 66).forEach { count ->
                        val isSelected = selectedCount == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonPurple else RoyalVioletCard)
                                .clickable { selectedCount = count }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "x$count",
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (selectedGift != null) {
                                onSendGift(selectedGift!!, selectedCount)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("send_gift_btn")
                    ) {
                        Text("Send", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomGamesDrawerSheet(
    userCoins: Long,
    onDismiss: () -> Unit,
    onPlayGame: (String, Long) -> Pair<Boolean, Long>
) {
    var selectedGame by remember { mutableStateOf("Lucky 77") }
    var betAmount by remember { mutableStateOf(100L) }
    var lastResult by remember { mutableStateOf<String?>(null) }
    var isSpinning by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RoyalVioletSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = RoyalVioletBorder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎰 Room Mini-Games Arena",
                color = GoldAccent,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Text(
                text = "Spin with coins & win luxury multipliers!",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Game Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Lucky 77", "Greedy Cat", "Lucky Box", "Greedy Lion").forEach { game ->
                    val isSelected = selectedGame == game
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonPurple else RoyalVioletCard)
                            .clickable { selectedGame = game }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = game,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game Visual Slot Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalVioletDark),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonMagenta)))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val emoji = when (selectedGame) {
                            "Greedy Cat" -> "🐱 💎 🍒"
                            "Lucky Box" -> "🎁 👑 ⭐"
                            "Greedy Lion" -> "🦁 🪙 🏆"
                            else -> "7️⃣ 7️⃣ 7️⃣"
                        }
                        Text(text = emoji, fontSize = 34.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (lastResult != null) {
                            Text(
                                text = lastResult!!,
                                color = if (lastResult!!.contains("Won")) GoldAccent else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Text("Select bet and press Spin!", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bet Stake Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Stake: 🪙", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                listOf(50L, 100L, 500L, 1000L).forEach { amount ->
                    val isSelected = betAmount == amount
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldAccent else RoyalVioletCard)
                            .clickable { betAmount = amount }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$amount",
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spin Button
            LuxuryButton(
                text = "SPIN 🎰 (🪙 $betAmount)",
                onClick = {
                    val (isWin, wonAmount) = onPlayGame(selectedGame, betAmount)
                    lastResult = if (isWin) {
                        "🎉 Won $wonAmount Coins!"
                    } else {
                        "Better luck next round!"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = userCoins >= betAmount
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomMusicDrawerSheet(
    audioManager: BismaAudioManager,
    onDismiss: () -> Unit
) {
    val isBgmPlaying by audioManager.isBgmPlaying.collectAsState()
    val currentTrack by audioManager.currentBgmTrack.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RoyalVioletSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = RoyalVioletBorder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "🎵 Room Audio & Atmosphere",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Sound Effects (SFX):", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    Pair("👏", "Applause"),
                    Pair("🎉", "Cheer"),
                    Pair("😂", "Laughter"),
                    Pair("🥁", "Drum Roll"),
                    Pair("🎺", "Fanfare")
                ).forEach { (emoji, label) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { audioManager.playSoundEffect("$emoji $label") }
                            .padding(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RoyalVioletCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                        Text(text = label, color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Background Music Tracks:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            listOf(
                "Chill Lounge Vibes 🎷",
                "Acoustic Sunset Guitar 🎸",
                "Cyberpunk Night Beat 🎧"
            ).forEach { track ->
                val isThisPlaying = isBgmPlaying && currentTrack == track
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isThisPlaying) RoyalVioletCardAlt else RoyalVioletCard)
                        .clickable { audioManager.toggleBgm(track) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = track, color = TextPrimary, fontSize = 13.sp)
                    Text(
                        text = if (isThisPlaying) "Playing ▶" else "Play",
                        color = if (isThisPlaying) NeonMagenta else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SeatActionDialog(
    seat: RoomSeat,
    isHost: Boolean,
    isMySeat: Boolean,
    onDismiss: () -> Unit,
    onOpenProfile: () -> Unit,
    onLeaveSeat: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleLock: () -> Unit,
    onKick: () -> Unit,
    onSendGiftToUser: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = seat.userName ?: "Seat #${seat.seatNumber}",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (seat.userId != null) {
                    Text("User ID: ${seat.userId}", color = TextSecondary, fontSize = 12.sp)
                    if (seat.vipTier != VipTier.NONE) {
                        VipBadge(vipTier = seat.vipTier, modifier = Modifier.padding(top = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons list
                if (seat.userId != null) {
                    Button(
                        onClick = onOpenProfile,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalVioletCardAlt)
                    ) {
                        Text("View Profile", color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = onSendGiftToUser,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                    ) {
                        Text("Send Gift 🎁", color = Color.White)
                    }
                }

                if (isMySeat) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onLeaveSeat,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MutedRed)
                    ) {
                        Text("Step Down from Seat", color = Color.White)
                    }
                }

                // Host Controls
                if (isHost && !isMySeat) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Host Seat Management:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onToggleMute,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalVioletCardAlt)
                        ) {
                            Text(if (seat.isMuted) "Unmute" else "Mute Mic", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onToggleLock,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalVioletCardAlt)
                        ) {
                            Text(if (seat.isLocked) "Unlock" else "Lock Seat", fontSize = 11.sp)
                        }
                    }

                    if (seat.userId != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onKick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MutedRed.copy(alpha = 0.8f))
                        ) {
                            Text("Remove User from Seat", color = Color.White)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = RoyalVioletCard
    )
}
