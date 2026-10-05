package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AgencyScreen(
    agency: Agency?,
    allAgencies: List<Agency>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "🏢 Agency Management", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (agency != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonPurpleLight)))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(agency.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Agency Code: ${agency.code}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(RoyalVioletCardAlt)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Lv. ${agency.level}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Leader: ${agency.leaderName}", color = TextSecondary, fontSize = 12.sp)
                            Text("Members: ${agency.memberCount} Signed Hosts", color = TextSecondary, fontSize = 12.sp)
                            Text("Total Agency Volume: 🪙 %,d Coins".format(agency.totalEarningsCoins), color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Announcement:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(agency.announcement, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            item {
                Text("All Verified Bisma Agencies:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(allAgencies) { ag ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ag.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Leader: ${ag.leaderName} • ${ag.memberCount} Members", color = TextSecondary, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalVioletCardAlt)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Lv.${ag.level}", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyScreen(
    family: Family?,
    allFamilies: List<Family>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "🛡️ Family Brotherhood", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (family != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFE040FB), RoyalVioletBorder)))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(family.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(family.badge, color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(RoyalVioletCardAlt)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Lv. ${family.level}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Family Patriarch: ${family.leaderName}", color = TextSecondary, fontSize = 12.sp)
                            Text("Members: ${family.memberCount} Active", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(family.announcement, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            item {
                Text("Active Bisma Families:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(allFamilies) { fam ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(fam.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Leader: ${fam.leaderName} • ${fam.memberCount} Members", color = TextSecondary, fontSize = 11.sp)
                        }
                        Text(fam.badge, color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RankingsScreen(
    users: List<User>,
    rooms: List<VoiceRoom>,
    onBack: () -> Unit,
    onOpenUser: (User) -> Unit,
    onOpenRoom: (VoiceRoom) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Richest, 1: Top Charm, 2: Top Rooms

    val richestUsers = remember(users) {
        users.sortedByDescending { it.coins + it.richXp }
    }
    val charmUsers = remember(users) {
        users.sortedByDescending { it.charmXp }
    }
    val topRooms = remember(rooms) {
        rooms.sortedByDescending { it.totalCoinsReceived + it.activeMembersCount * 100 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "🏆 Leaderboards & Hall of Fame", onBack = onBack)

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = RoyalVioletSurface,
            contentColor = NeonMagenta
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("💰 Richest") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("💖 Top Charm") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("🎙️ Top Rooms") })
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    items(richestUsers.take(15).withIndex().toList()) { (index, user) ->
                        RankingUserCard(rank = index + 1, user = user, statLabel = "🪙 %,d".format(user.coins), onClick = { onOpenUser(user) })
                    }
                }
                1 -> {
                    items(charmUsers.take(15).withIndex().toList()) { (index, user) ->
                        RankingUserCard(rank = index + 1, user = user, statLabel = "💖 Lv.${user.charmLevel}", onClick = { onOpenUser(user) })
                    }
                }
                2 -> {
                    items(topRooms.take(15).withIndex().toList()) { (index, room) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenRoom(room) },
                            colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (index) {
                                        0 -> "🥇"
                                        1 -> "🥈"
                                        2 -> "🥉"
                                        else -> "#${index + 1}"
                                    },
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.width(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(room.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Host: ${room.hostName} • ${room.activeMembersCount} online", color = TextSecondary, fontSize = 11.sp)
                                }
                                Text("%,d Coins".format(room.totalCoinsReceived), color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingUserCard(rank: Int, user: User, statLabel: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "#$rank"
                },
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(32.dp)
            )

            UserAvatarWithFrame(
                avatarUrl = user.avatarUrl,
                displayName = user.displayName,
                frameId = user.equippedFrameId,
                vipTier = user.vipTier,
                size = 42.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    VipBadge(vipTier = user.vipTier)
                }
                Text("ID: ${user.id}", color = TextSecondary, fontSize = 10.sp)
            }

            Text(text = statLabel, color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun SearchScreen(
    users: List<User>,
    rooms: List<VoiceRoom>,
    onBack: () -> Unit,
    onOpenUser: (User) -> Unit,
    onOpenRoom: (VoiceRoom) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val matchedUsers = remember(query, users) {
        if (query.isBlank()) emptyList() else {
            users.filter {
                it.id.toString().contains(query, ignoreCase = true) ||
                it.username.contains(query, ignoreCase = true) ||
                it.displayName.contains(query, ignoreCase = true)
            }
        }
    }

    val matchedRooms = remember(query, rooms) {
        if (query.isBlank()) emptyList() else {
            rooms.filter {
                it.id.toString().contains(query, ignoreCase = true) ||
                it.title.contains(query, ignoreCase = true) ||
                it.hostName.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "Search Community", onBack = onBack)

        // Search Bar Input
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search by User ID, Name, or Room ID...", fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = RoyalVioletBorder,
                    focusedContainerColor = RoyalVioletCard,
                    unfocusedContainerColor = RoyalVioletCard
                ),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (query.isBlank()) {
                item {
                    EmptyStateNotice(
                        title = "Find Users & Rooms",
                        subtitle = "Enter a numeric 6-digit User ID, username, or room title to discover live audio hosts."
                    )
                }
            } else if (matchedUsers.isEmpty() && matchedRooms.isEmpty()) {
                item {
                    EmptyStateNotice(
                        title = "No Matches Found",
                        subtitle = "We couldn't find any account or live room matching \"$query\"."
                    )
                }
            } else {
                if (matchedUsers.isNotEmpty()) {
                    item {
                        Text("Users (${matchedUsers.size}):", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    items(matchedUsers) { user ->
                        UserListItem(
                            user = user,
                            actionLabel = "Profile",
                            onAction = { onOpenUser(user) },
                            onCardClick = { onOpenUser(user) }
                        )
                    }
                }

                if (matchedRooms.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Voice Rooms (${matchedRooms.size}):", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    items(matchedRooms) { room ->
                        LiveRoomCard(room = room, onClick = { onOpenRoom(room) })
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onSubmitTicket: (String, String) -> Boolean
) {
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var micSensitivity by remember { mutableStateOf(0.7f) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var ticketCategory by remember { mutableStateOf("Voice Room Quality") }
    var ticketDescription by remember { mutableStateOf("") }
    var feedbackSubmittedNotice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "Settings & Support", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Audio & Media Settings:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = RoyalVioletCard), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sound Effects & Audio", color = TextPrimary)
                            Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Microphone Input Sensitivity", color = TextSecondary, fontSize = 11.sp)
                        Slider(
                            value = micSensitivity,
                            onValueChange = { micSensitivity = it },
                            colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonPurple)
                        )
                    }
                }
            }

            item {
                Text("App Preferences:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = RoyalVioletCard), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Push Notifications", color = TextPrimary)
                            Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                        }
                    }
                }
            }

            item {
                Text("Customer Care & Help:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFeedbackDialog = true },
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💬", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Customer Support & Feedback", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Report an issue or request assistance", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            if (feedbackSubmittedNotice) {
                item {
                    Text("Support ticket submitted! Our team will review shortly.", color = SpeakingGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MutedRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button")
                ) {
                    Text("Log Out from Bisma Live", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Support Dialog
        if (showFeedbackDialog) {
            AlertDialog(
                onDismissRequest = { showFeedbackDialog = false },
                title = { Text("Customer Support Ticket", color = TextPrimary) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = ticketCategory,
                            onValueChange = { ticketCategory = it },
                            label = { Text("Issue Category") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ticketDescription,
                            onValueChange = { ticketDescription = it },
                            label = { Text("Describe the problem") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (ticketDescription.isNotBlank()) {
                                onSubmitTicket(ticketCategory, ticketDescription)
                                showFeedbackDialog = false
                                feedbackSubmittedNotice = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                    ) {
                        Text("Submit Ticket")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = RoyalVioletCard
            )
        }
    }
}

@Composable
fun AdminScreen(
    users: List<User>,
    rooms: List<VoiceRoom>,
    onBack: () -> Unit,
    onBanUser: (Long) -> Unit,
    onLockRoom: (Long) -> Unit,
    onBroadcast: (String) -> Unit
) {
    var broadcastMsg by remember { mutableStateOf("") }
    var notice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "🛡️ Admin & Moderation Panel", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Metrics Overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(title = "Users", value = "${users.size}", modifier = Modifier.weight(1f))
                    MetricCard(title = "Rooms", value = "${rooms.size}", modifier = Modifier.weight(1f))
                    MetricCard(title = "Volume", value = "🪙 840K", modifier = Modifier.weight(1f))
                }
            }

            if (notice != null) {
                item {
                    Text(text = notice!!, color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            }

            // System Announcement Broadcast
            item {
                Card(colors = CardDefaults.cardColors(containerColor = RoyalVioletCard), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Platform Official Broadcast:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = broadcastMsg,
                            onValueChange = { broadcastMsg = it },
                            placeholder = { Text("Send global system notice to all users...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (broadcastMsg.isNotBlank()) {
                                    onBroadcast(broadcastMsg.trim())
                                    notice = "Broadcast sent to all users!"
                                    broadcastMsg = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Broadcast Notice 📢", fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Text("Manage Active Users:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(users) { u ->
                Card(colors = CardDefaults.cardColors(containerColor = RoyalVioletCard), shape = RoundedCornerShape(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(u.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("ID: ${u.id} • Role: ${u.role}", color = TextSecondary, fontSize = 10.sp)
                        }
                        if (u.isBanned) {
                            Text("BANNED", color = MutedRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        } else {
                            OutlinedButton(
                                onClick = {
                                    onBanUser(u.id)
                                    notice = "User ${u.displayName} has been restricted."
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Ban", color = MutedRed, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = GoldAccent, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(title, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
