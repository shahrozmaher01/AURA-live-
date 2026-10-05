package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.BismaTopBar
import com.example.ui.components.UserAvatarWithFrame
import com.example.ui.components.VipBadge
import com.example.ui.theme.*

@Composable
fun SocialScreen(
    currentUser: User,
    allUsers: List<User>,
    friendIds: Set<Long>,
    followingIds: Set<Long>,
    friendRequests: List<FriendRequest>,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onToggleFollow: (Long) -> Unit,
    onOpenProfile: (User) -> Unit,
    onOpenAgency: () -> Unit,
    onOpenFamily: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Friends, 1: Following, 2: Requests

    val friendsList = remember(friendIds, allUsers) {
        allUsers.filter { friendIds.contains(it.id) }
    }
    val followingList = remember(followingIds, allUsers) {
        allUsers.filter { followingIds.contains(it.id) }
    }
    val pendingRequests = remember(friendRequests) {
        friendRequests.filter { it.status == FriendRequestStatus.PENDING }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "🤝 Social Network")

        // Family & Agency Banner shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenAgency),
                colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏢", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("My Agency", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Earnings & Hosts", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenFamily),
                colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("My Family", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Room Brotherhood", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tabs Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = RoyalVioletSurface,
            contentColor = NeonMagenta
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Friends (${friendsList.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Following (${followingList.size})") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Requests")
                        if (pendingRequests.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NeonPink)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${pendingRequests.size}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
            when (selectedTab) {
                0 -> {
                    if (friendsList.isEmpty()) {
                        item {
                            EmptyStateNotice(
                                title = "No Friends Yet",
                                subtitle = "Explore live voice rooms to make new friends and send requests!"
                            )
                        }
                    } else {
                        items(friendsList) { friend ->
                            UserListItem(
                                user = friend,
                                actionLabel = "View",
                                onAction = { onOpenProfile(friend) },
                                onCardClick = { onOpenProfile(friend) }
                            )
                        }
                    }
                }
                1 -> {
                    if (followingList.isEmpty()) {
                        item {
                            EmptyStateNotice(
                                title = "Not Following Anyone",
                                subtitle = "Follow your favorite room hosts to get notified when they go live!"
                            )
                        }
                    } else {
                        items(followingList) { targetUser ->
                            UserListItem(
                                user = targetUser,
                                actionLabel = "Following ✓",
                                isActionSecondary = true,
                                onAction = { onToggleFollow(targetUser.id) },
                                onCardClick = { onOpenProfile(targetUser) }
                            )
                        }
                    }
                }
                2 -> {
                    if (pendingRequests.isEmpty()) {
                        item {
                            EmptyStateNotice(
                                title = "No Pending Friend Requests",
                                subtitle = "Incoming friendship requests from other users will show up here."
                            )
                        }
                    } else {
                        items(pendingRequests) { req ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        UserAvatarWithFrame(
                                            avatarUrl = req.fromUserAvatar,
                                            displayName = req.fromUserName,
                                            size = 44.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = req.fromUserName,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Wants to be friends",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Row {
                                        Button(
                                            onClick = { onAcceptRequest(req.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        OutlinedButton(
                                            onClick = { onRejectRequest(req.id) },
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Decline", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessagesScreen(
    notifications: List<AppNotification>,
    onMarkRead: (String) -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
    onRejectFriendRequest: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "📬 Notifications & System")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    EmptyStateNotice(
                        title = "No Notifications",
                        subtitle = "You're all caught up with your friends and room events."
                    )
                }
            } else {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMarkRead(notif.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isRead) RoyalVioletCard else RoyalVioletCardAlt
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = notif.type.icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notif.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(NeonPink)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = notif.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                // Direct action if this notification has a friend request
                                if (notif.type == NotificationType.FRIEND_REQ && notif.relatedRequestId != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Button(
                                            onClick = { onAcceptFriendRequest(notif.relatedRequestId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        OutlinedButton(
                                            onClick = { onRejectFriendRequest(notif.relatedRequestId) },
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Decline", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    actionLabel: String,
    isActionSecondary: Boolean = false,
    onAction: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
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
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        VipBadge(vipTier = user.vipTier)
                    }
                    Text(
                        text = "ID: ${user.id} • ${user.bio}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActionSecondary) RoyalVioletCardAlt else NeonMagenta
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActionSecondary) TextSecondary else Color.White
                )
            }
        }
    }
}

@Composable
fun EmptyStateNotice(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🌌", fontSize = 42.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, color = TextMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
