package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    currentUser: User,
    rooms: List<VoiceRoom>,
    popularUsers: List<User>,
    unreadNotificationsCount: Int,
    onOpenRoom: (VoiceRoom) -> Unit,
    onOpenProfile: (User) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenRankings: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenBackpack: () -> Unit,
    onOpenAgency: () -> Unit,
    onDailyCheckinClaim: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<RoomCategory?>(null) }
    var hasClaimedDaily by remember { mutableStateOf(false) }

    val filteredRooms = remember(selectedCategory, rooms) {
        if (selectedCategory == null) rooms else rooms.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Profile & Actions Header
        item {
            HomeTopHeader(
                user = currentUser,
                unreadCount = unreadNotificationsCount,
                onAvatarClick = { onOpenProfile(currentUser) },
                onSearchClick = onOpenSearch,
                onNotificationClick = onOpenNotifications,
                onCoinClick = onOpenWallet
            )
        }

        // Hero Promotional Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RoyalVioletBorder, RoundedCornerShape(16.dp))
                    .testTag("home_hero_banner")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_party),
                    contentDescription = "Bisma Voice Lounge Festival",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xD0170E32), Color(0x60170E32), Color.Transparent)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "BISMA LIVE CARNIVAL",
                        color = GoldAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Double Charm XP & Gift Bonanza!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(NeonMagenta, NeonPurple)))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Join Live Rooms 🎙️",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Quick Feature Shortcuts Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickShortcutButton(icon = "🏆", label = "Rankings", onClick = onOpenRankings)
                QuickShortcutButton(icon = "🛍️", label = "Store", onClick = onOpenStore)
                QuickShortcutButton(icon = "🎒", label = "Backpack", onClick = onOpenBackpack)
                QuickShortcutButton(icon = "🏢", label = "Agency", onClick = onOpenAgency)
            }
        }

        // Daily Check-in Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonPurpleLight)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Voice Attendance",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (hasClaimedDaily) "Claimed today! +500 Coins" else "Claim free 500 Coins & XP",
                                color = if (hasClaimedDaily) GoldAccent else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (!hasClaimedDaily) {
                                hasClaimedDaily = true
                                onDailyCheckinClaim()
                            }
                        },
                        enabled = !hasClaimedDaily,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            disabledContainerColor = RoyalVioletCardAlt
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (hasClaimedDaily) "Claimed" else "Check In",
                            color = if (hasClaimedDaily) TextMuted else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Room Categories Filter
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("🔥 All Rooms") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White,
                            containerColor = RoyalVioletCard,
                            labelColor = TextSecondary
                        )
                    )
                }
                items(RoomCategory.entries.toTypedArray()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White,
                            containerColor = RoyalVioletCard,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Popular Hosts Section
        item {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ Top Voice Hosts",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(popularUsers) { user ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onOpenProfile(user) }
                                .width(64.dp)
                        ) {
                            UserAvatarWithFrame(
                                avatarUrl = user.avatarUrl,
                                displayName = user.displayName,
                                frameId = user.equippedFrameId,
                                vipTier = user.vipTier,
                                size = 52.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.displayName,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            LevelBadge(type = "CHARM", level = user.charmLevel)
                        }
                    }
                }
            }
        }

        // Recommended Voice Rooms Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎙️ Live Voice Rooms",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${filteredRooms.size} live",
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }

        // Voice Room Cards
        items(filteredRooms) { room ->
            LiveRoomCard(
                room = room,
                onClick = { onOpenRoom(room) }
            )
        }
    }
}

@Composable
fun HomeTopHeader(
    user: User,
    unreadCount: Int,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCoinClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onAvatarClick)
        ) {
            UserAvatarWithFrame(
                avatarUrl = user.avatarUrl,
                displayName = user.displayName,
                frameId = user.equippedFrameId,
                vipTier = user.vipTier,
                size = 46.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    VipBadge(vipTier = user.vipTier)
                }
                Text(
                    text = "ID: ${user.id}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            CoinChip(coins = user.coins, onClickAdd = onCoinClick)
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(RoyalVioletCard)
                    .testTag("home_search_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RoyalVioletCard)
                        .testTag("home_notifications_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = (-2).dp, y = 2.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeonPink)
                    )
                }
            }
        }
    }
}

@Composable
fun LiveRoomCard(
    room: VoiceRoom,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("room_card_${room.id}"),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalVioletBorder, RoyalVioletCardAlt)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host Avatar & Live indicator
            Box(contentAlignment = Alignment.BottomCenter) {
                UserAvatarWithFrame(
                    avatarUrl = room.hostAvatar,
                    displayName = room.hostName,
                    vipTier = room.hostVip,
                    size = 54.dp
                )
                Box(
                    modifier = Modifier
                        .offset(y = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPink)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = room.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RoyalVioletCardAlt)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${room.seatCount} Seats",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Host: ${room.hostName} • ID: ${room.id}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RoyalVioletSurface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = room.category.displayName,
                            color = GoldAccent,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "👥 ${room.activeMembersCount} online",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickShortcutButton(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(RoyalVioletCard),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
