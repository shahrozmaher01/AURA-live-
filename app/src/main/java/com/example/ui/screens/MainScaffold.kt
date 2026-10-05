package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BismaAudioManager
import com.example.data.BismaRepository
import com.example.model.*
import com.example.ui.theme.*

enum class MainTab(val title: String) {
    HOME("Home"),
    SOCIAL("Friends"),
    ROOMS("Rooms"),
    MESSAGES("Messages"),
    PROFILE("Profile")
}

enum class SubScreen {
    NONE,
    VOICE_ROOM,
    SEARCH,
    EDIT_PROFILE,
    WALLET,
    VIP,
    STORE,
    BACKPACK,
    AGENCY,
    FAMILY,
    RANKINGS,
    SETTINGS,
    ADMIN
}

@Composable
fun MainScaffold(
    repository: BismaRepository,
    audioManager: BismaAudioManager,
    onLogout: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val allUsers by repository.users.collectAsState()
    val rooms by repository.rooms.collectAsState()
    val currentRoom by repository.currentRoom.collectAsState()
    val roomMessages by repository.roomMessages.collectAsState()
    val activeGiftAnimation by repository.activeGiftAnimation.collectAsState()
    val gifts by repository.gifts.collectAsState()
    val moments by repository.moments.collectAsState()
    val backpack by repository.backpack.collectAsState()
    val storeItems by repository.storeItems.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val friendRequests by repository.friendRequests.collectAsState()
    val followingIds by repository.followingIds.collectAsState()
    val friendIds by repository.friendIds.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val agencies by repository.agencies.collectAsState()
    val families by repository.families.collectAsState()

    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }
    var inspectingUser by remember { mutableStateOf<User?>(null) }

    val unreadNotificationsCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    // Auto-switch to voice room screen when currentRoom is set
    LaunchedEffect(currentRoom) {
        if (currentRoom != null) {
            currentSubScreen = SubScreen.VOICE_ROOM
        } else if (currentSubScreen == SubScreen.VOICE_ROOM) {
            currentSubScreen = SubScreen.NONE
        }
    }

    // BackHandler for sub-screens
    BackHandler(enabled = currentSubScreen != SubScreen.NONE) {
        if (currentSubScreen == SubScreen.VOICE_ROOM) {
            repository.leaveRoom()
        }
        currentSubScreen = SubScreen.NONE
    }

    val user = currentUser ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        if (currentSubScreen == SubScreen.VOICE_ROOM && currentRoom != null) {
            VoiceRoomScreen(
                room = currentRoom!!,
                currentUser = user,
                messages = roomMessages,
                activeGiftAnimation = activeGiftAnimation,
                gifts = gifts,
                audioManager = audioManager,
                onLeaveRoom = {
                    repository.leaveRoom()
                    currentSubScreen = SubScreen.NONE
                },
                onJoinSeat = { seatIdx -> repository.joinSeat(seatIdx) },
                onLeaveSeat = { repository.leaveSeat() },
                onSendMessage = { text -> repository.sendRoomMessage(text) },
                onSendGift = { gift, count, targetName -> repository.sendGift(gift, count, targetName) },
                onPlayGame = { game, bet -> repository.playRoomGame(game, bet) },
                onToggleLockSeat = { seatIdx -> repository.toggleLockSeat(seatIdx) },
                onToggleMuteSeat = { seatIdx -> repository.toggleMuteSeat(seatIdx) },
                onKickUser = { seatIdx -> repository.kickUserFromSeat(seatIdx) },
                onOpenProfile = { uid ->
                    val target = allUsers.find { it.id == uid }
                    if (target != null) inspectingUser = target
                },
                onToggleFollowHost = { uid -> repository.toggleFollow(uid) },
                isFollowingHost = repository.isFollowing(currentRoom!!.hostUserId)
            )
        } else if (currentSubScreen != SubScreen.NONE) {
            when (currentSubScreen) {
                SubScreen.SEARCH -> {
                    SearchScreen(
                        users = allUsers,
                        rooms = rooms,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onOpenUser = { target -> inspectingUser = target },
                        onOpenRoom = { room ->
                            repository.enterRoom(room)
                            currentSubScreen = SubScreen.VOICE_ROOM
                        }
                    )
                }
                SubScreen.EDIT_PROFILE -> {
                    EditProfileScreen(
                        currentUser = user,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onSave = { name, bio, gender, uri ->
                            repository.updateProfile(name, bio, gender, uri)
                        }
                    )
                }
                SubScreen.WALLET -> {
                    WalletScreen(
                        currentUser = user,
                        rechargePackages = repository.rechargePackages,
                        transactions = transactions,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onRecharge = { pack -> repository.rechargeCoins(pack) },
                        onWithdrawal = { amount, method -> repository.requestWithdrawal(amount, method) }
                    )
                }
                SubScreen.VIP -> {
                    VipScreen(
                        currentUser = user,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onUpgrade = { tier -> repository.upgradeVip(tier) }
                    )
                }
                SubScreen.STORE -> {
                    StoreScreen(
                        currentUser = user,
                        storeItems = storeItems,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onPurchase = { item -> repository.purchaseStoreItem(item) }
                    )
                }
                SubScreen.BACKPACK -> {
                    BackpackScreen(
                        backpackItems = backpack,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onEquip = { item -> repository.equipBackpackItem(item) },
                        onUnequip = { item -> repository.unequipBackpackItem(item) }
                    )
                }
                SubScreen.AGENCY -> {
                    AgencyScreen(
                        agency = agencies.find { it.id == user.agencyId },
                        allAgencies = agencies,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.FAMILY -> {
                    FamilyScreen(
                        family = families.find { it.id == user.familyId },
                        allFamilies = families,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.RANKINGS -> {
                    RankingsScreen(
                        users = allUsers,
                        rooms = rooms,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onOpenUser = { target -> inspectingUser = target },
                        onOpenRoom = { room ->
                            repository.enterRoom(room)
                            currentSubScreen = SubScreen.VOICE_ROOM
                        }
                    )
                }
                SubScreen.SETTINGS -> {
                    SettingsScreen(
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onLogout = {
                            repository.logout()
                            onLogout()
                        },
                        onSubmitTicket = { cat, desc -> repository.submitSupportTicket(cat, desc) }
                    )
                }
                SubScreen.ADMIN -> {
                    AdminScreen(
                        users = allUsers,
                        rooms = rooms,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onBanUser = { uid -> repository.adminBanUser(uid) },
                        onLockRoom = { rid -> repository.adminLockRoom(rid) },
                        onBroadcast = { msg -> repository.adminBroadcast(msg) }
                    )
                }
                else -> {}
            }
        } else {
            // Main Tabs Scaffold
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = RoyalVioletSurface,
                        contentColor = NeonMagenta,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("main_bottom_nav")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.HOME,
                            onClick = { currentTab = MainTab.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldAccent,
                                selectedTextColor = GoldAccent,
                                indicatorColor = RoyalVioletCardAlt,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.SOCIAL,
                            onClick = { currentTab = MainTab.SOCIAL },
                            icon = { Icon(Icons.Default.People, contentDescription = "Social") },
                            label = { Text("Friends", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldAccent,
                                selectedTextColor = GoldAccent,
                                indicatorColor = RoyalVioletCardAlt,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.ROOMS,
                            onClick = { currentTab = MainTab.ROOMS },
                            icon = { Icon(Icons.Default.Mic, contentDescription = "Rooms") },
                            label = { Text("Rooms", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldAccent,
                                selectedTextColor = GoldAccent,
                                indicatorColor = RoyalVioletCardAlt,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.MESSAGES,
                            onClick = { currentTab = MainTab.MESSAGES },
                            icon = {
                                BadgedBox(badge = {
                                    if (unreadNotificationsCount > 0) {
                                        Badge(containerColor = NeonPink) {
                                            Text("$unreadNotificationsCount")
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Messages")
                                }
                            },
                            label = { Text("Messages", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldAccent,
                                selectedTextColor = GoldAccent,
                                indicatorColor = RoyalVioletCardAlt,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.PROFILE,
                            onClick = { currentTab = MainTab.PROFILE },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldAccent,
                                selectedTextColor = GoldAccent,
                                indicatorColor = RoyalVioletCardAlt,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                currentUser = user,
                                rooms = rooms,
                                popularUsers = allUsers,
                                unreadNotificationsCount = unreadNotificationsCount,
                                onOpenRoom = { room ->
                                    repository.enterRoom(room)
                                    currentSubScreen = SubScreen.VOICE_ROOM
                                },
                                onOpenProfile = { target -> inspectingUser = target },
                                onOpenSearch = { currentSubScreen = SubScreen.SEARCH },
                                onOpenNotifications = { currentTab = MainTab.MESSAGES },
                                onOpenWallet = { currentSubScreen = SubScreen.WALLET },
                                onOpenRankings = { currentSubScreen = SubScreen.RANKINGS },
                                onOpenStore = { currentSubScreen = SubScreen.STORE },
                                onOpenBackpack = { currentSubScreen = SubScreen.BACKPACK },
                                onOpenAgency = { currentSubScreen = SubScreen.AGENCY },
                                onDailyCheckinClaim = {
                                    repository.rechargeCoins(RechargePackage("checkin", 500, 0, 0.0))
                                }
                            )
                        }
                        MainTab.SOCIAL -> {
                            SocialScreen(
                                currentUser = user,
                                allUsers = allUsers,
                                friendIds = friendIds,
                                followingIds = followingIds,
                                friendRequests = friendRequests,
                                onAcceptRequest = { reqId -> repository.acceptFriendRequest(reqId) },
                                onRejectRequest = { reqId -> repository.rejectFriendRequest(reqId) },
                                onToggleFollow = { uid -> repository.toggleFollow(uid) },
                                onOpenProfile = { target -> inspectingUser = target },
                                onOpenAgency = { currentSubScreen = SubScreen.AGENCY },
                                onOpenFamily = { currentSubScreen = SubScreen.FAMILY }
                            )
                        }
                        MainTab.ROOMS -> {
                            RoomsScreen(
                                rooms = rooms,
                                onOpenRoom = { room ->
                                    repository.enterRoom(room)
                                    currentSubScreen = SubScreen.VOICE_ROOM
                                },
                                onCreateRoom = { title, cat, seats, notice ->
                                    repository.createVoiceRoom(title, cat, seats, notice)
                                    currentSubScreen = SubScreen.VOICE_ROOM
                                }
                            )
                        }
                        MainTab.MESSAGES -> {
                            MessagesScreen(
                                notifications = notifications,
                                onMarkRead = { id -> repository.markNotificationRead(id) },
                                onAcceptFriendRequest = { id -> repository.acceptFriendRequest(id) },
                                onRejectFriendRequest = { id -> repository.rejectFriendRequest(id) }
                            )
                        }
                        MainTab.PROFILE -> {
                            ProfileScreen(
                                currentUser = user,
                                onEditProfileClick = { currentSubScreen = SubScreen.EDIT_PROFILE },
                                onWalletClick = { currentSubScreen = SubScreen.WALLET },
                                onVipClick = { currentSubScreen = SubScreen.VIP },
                                onBackpackClick = { currentSubScreen = SubScreen.BACKPACK },
                                onStoreClick = { currentSubScreen = SubScreen.STORE },
                                onAgencyClick = { currentSubScreen = SubScreen.AGENCY },
                                onFamilyClick = { currentSubScreen = SubScreen.FAMILY },
                                onSettingsClick = { currentSubScreen = SubScreen.SETTINGS },
                                onAdminClick = { currentSubScreen = SubScreen.ADMIN },
                                onSocialClick = { currentTab = MainTab.SOCIAL }
                            )
                        }
                    }
                }
            }
        }

        // Other User Profile Details Dialog
        if (inspectingUser != null) {
            val target = inspectingUser!!
            UserProfileDetailDialog(
                user = target,
                isFollowing = repository.isFollowing(target.id),
                onDismiss = { inspectingUser = null },
                onToggleFollow = { repository.toggleFollow(target.id) },
                onSendFriendRequest = {
                    repository.sendFriendRequest(target.id)
                    inspectingUser = null
                },
                onSendGift = {
                    val defaultGift = gifts.firstOrNull()
                    if (defaultGift != null) {
                        repository.sendGift(defaultGift, 1, target.displayName)
                    }
                    inspectingUser = null
                }
            )
        }
    }
}
