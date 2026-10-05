package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
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
fun ProfileScreen(
    currentUser: User,
    onEditProfileClick: () -> Unit,
    onWalletClick: () -> Unit,
    onVipClick: () -> Unit,
    onBackpackClick: () -> Unit,
    onStoreClick: () -> Unit,
    onAgencyClick: () -> Unit,
    onFamilyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSocialClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            ProfileHeaderCard(
                user = currentUser,
                onEditClick = onEditProfileClick,
                onSocialClick = onSocialClick
            )
        }

        // Levels & Status Card (User Level, Rich Level, Charm Level)
        item {
            LevelsOverviewCard(user = currentUser)
        }

        // Wallet Balance Quick View
        item {
            WalletBannerCard(
                coins = currentUser.coins,
                diamonds = currentUser.diamonds,
                onWalletClick = onWalletClick
            )
        }

        // Navigation Sections
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProfileMenuItem(icon = "👑", title = "VIP Membership Center", subtitle = currentUser.vipTier.title, onClick = onVipClick)
                ProfileMenuItem(icon = "🎒", title = "My Backpack", subtitle = "Equipped frames & effects", onClick = onBackpackClick)
                ProfileMenuItem(icon = "🛍️", title = "Avatar Store", subtitle = "Exclusive frames & themes", onClick = onStoreClick)
                ProfileMenuItem(icon = "🏢", title = "My Agency", subtitle = currentUser.agencyName ?: "Join or Create Agency", onClick = onAgencyClick)
                ProfileMenuItem(icon = "🛡️", title = "My Family", subtitle = currentUser.familyName ?: "Join or Create Family", onClick = onFamilyClick)
                ProfileMenuItem(icon = "⚙️", title = "Settings & Support", subtitle = "Preferences & Feedback", onClick = onSettingsClick)

                if (currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.OWNER) {
                    ProfileMenuItem(icon = "🛡️", title = "Admin & Moderation Panel", subtitle = "Role: ${currentUser.role}", onClick = onAdminClick)
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(
    user: User,
    onEditClick: () -> Unit,
    onSocialClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .statusBarsPadding(),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalVioletBorder, RoyalVioletCardAlt)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalVioletCardAlt)
                        .testTag("edit_profile_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Avatar with frame
            UserAvatarWithFrame(
                avatarUrl = user.avatarUrl,
                displayName = user.displayName,
                frameId = user.equippedFrameId,
                vipTier = user.vipTier,
                size = 76.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = user.displayName,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ID: ${user.id} • @${user.username}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                VipBadge(vipTier = user.vipTier)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.bio,
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Social Counters (Followers, Following, Friends)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RoyalVioletDark)
                    .clickable(onClick = onSocialClick)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialCounterItem(count = user.followingCount, label = "Following")
                SocialCounterItem(count = user.followersCount, label = "Followers")
                SocialCounterItem(count = user.friendsCount, label = "Friends")
            }
        }
    }
}

@Composable
fun SocialCounterItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun LevelsOverviewCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐ User Level", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    LevelBadge(type = "USER", level = user.userLevel)
                }
                Text(text = "${user.userXp} XP", color = NeonPurpleLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (user.userXp % 500) / 500f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonPurpleLight,
                trackColor = RoyalVioletDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rich Level Chip
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💰", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Rich Lv.${user.richLevel}", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${user.richXp} XP", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                // Charm Level Chip
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Charm Lv.${user.charmLevel}", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${user.charmXp} XP", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletBannerCard(
    coins: Long,
    diamonds: Long,
    onWalletClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onWalletClick),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCardAlt),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonPurpleLight)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👛", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("My Wallet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙 %,d Coins".format(coins), color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💎 %,d Diamonds".format(diamonds), color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Button(
                onClick = onWalletClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Recharge", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    currentUser: User,
    onBack: () -> Unit,
    onSave: (displayName: String, bio: String, gender: Gender, avatarUri: String) -> Unit
) {
    var displayName by remember { mutableStateOf(currentUser.displayName) }
    var bio by remember { mutableStateOf(currentUser.bio) }
    var gender by remember { mutableStateOf(currentUser.gender) }
    var avatarUri by remember { mutableStateOf(currentUser.avatarUrl) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            avatarUri = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "Edit Profile", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture with change button
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("change_avatar_btn"),
                contentAlignment = Alignment.BottomEnd
            ) {
                UserAvatarWithFrame(
                    avatarUrl = avatarUri,
                    displayName = displayName,
                    frameId = currentUser.equippedFrameId,
                    size = 90.dp
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(NeonMagenta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Picture",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap to choose from Gallery",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_display_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = RoyalVioletBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .testTag("edit_bio_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = RoyalVioletBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gender", color = TextSecondary, fontSize = 13.sp)
                Row {
                    FilterChip(
                        selected = gender == Gender.MALE,
                        onClick = { gender = Gender.MALE },
                        label = { Text("♂ Male") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = gender == Gender.FEMALE,
                        onClick = { gender = Gender.FEMALE },
                        label = { Text("♀ Female") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            LuxuryButton(
                text = "Save Profile Changes",
                onClick = {
                    onSave(displayName, bio, gender, avatarUri)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun UserProfileDetailDialog(
    user: User,
    isFollowing: Boolean,
    onDismiss: () -> Unit,
    onToggleFollow: () -> Unit,
    onSendFriendRequest: () -> Unit,
    onSendGift: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserAvatarWithFrame(
                    avatarUrl = user.avatarUrl,
                    displayName = user.displayName,
                    frameId = user.equippedFrameId,
                    vipTier = user.vipTier,
                    size = 72.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user.displayName,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "ID: ${user.id} • @${user.username}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (user.vipTier != VipTier.NONE) {
                        VipBadge(vipTier = user.vipTier)
                    }
                    LevelBadge(type = "CHARM", level = user.charmLevel)
                    LevelBadge(type = "RICH", level = user.richLevel)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = user.bio,
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialCounterItem(count = user.followingCount, label = "Following")
                    SocialCounterItem(count = user.followersCount, label = "Followers")
                    SocialCounterItem(count = user.friendsCount, label = "Friends")
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleFollow,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) RoyalVioletCardAlt else NeonMagenta
                        )
                    ) {
                        Text(if (isFollowing) "Following ✓" else "+ Follow", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onSendFriendRequest,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalVioletCardAlt)
                    ) {
                        Text("+ Friend", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSendGift,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Send Gift 🎁", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = RoyalVioletCard
    )
}
