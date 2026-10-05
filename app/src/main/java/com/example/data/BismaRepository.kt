package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class BismaRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val prefs: SharedPreferences = context.getSharedPreferences("bisma_live_prefs", Context.MODE_PRIVATE)

    // Current User
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // All registered users (for social, search, etc.)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // Voice Rooms
    private val _rooms = MutableStateFlow<List<VoiceRoom>>(emptyList())
    val rooms: StateFlow<List<VoiceRoom>> = _rooms.asStateFlow()

    // Current Active Room
    private val _currentRoom = MutableStateFlow<VoiceRoom?>(null)
    val currentRoom: StateFlow<VoiceRoom?> = _currentRoom.asStateFlow()

    private val _roomMessages = MutableStateFlow<List<RoomChatMessage>>(emptyList())
    val roomMessages: StateFlow<List<RoomChatMessage>> = _roomMessages.asStateFlow()

    // Active Gift Animation for Overlay
    private val _activeGiftAnimation = MutableStateFlow<Gift?>(null)
    val activeGiftAnimation: StateFlow<Gift?> = _activeGiftAnimation.asStateFlow()

    // Gifts Catalog
    private val _gifts = MutableStateFlow<List<Gift>>(emptyList())
    val gifts: StateFlow<List<Gift>> = _gifts.asStateFlow()

    // Moments
    private val _moments = MutableStateFlow<List<Moment>>(emptyList())
    val moments: StateFlow<List<Moment>> = _moments.asStateFlow()

    // Backpack & Store
    private val _backpack = MutableStateFlow<List<BackpackItem>>(emptyList())
    val backpack: StateFlow<List<BackpackItem>> = _backpack.asStateFlow()

    private val _storeItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val storeItems: StateFlow<List<StoreItem>> = _storeItems.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Friend Requests
    private val _friendRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val friendRequests: StateFlow<List<FriendRequest>> = _friendRequests.asStateFlow()

    // Following User IDs
    private val _followingIds = MutableStateFlow<Set<Long>>(emptySet())
    val followingIds: StateFlow<Set<Long>> = _followingIds.asStateFlow()

    // Friends User IDs
    private val _friendIds = MutableStateFlow<Set<Long>>(emptySet())
    val friendIds: StateFlow<Set<Long>> = _friendIds.asStateFlow()

    // Wallet Transactions
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Recharge Packages
    val rechargePackages = listOf(
        RechargePackage("pack_1", 1000, 100, 0.99),
        RechargePackage("pack_2", 5500, 600, 4.99, isHot = true),
        RechargePackage("pack_3", 12000, 1500, 9.99),
        RechargePackage("pack_4", 30000, 4500, 24.99),
        RechargePackage("pack_5", 65000, 10000, 49.99, isHot = true),
        RechargePackage("pack_6", 140000, 25000, 99.99)
    )

    // Agencies & Families
    private val _agencies = MutableStateFlow<List<Agency>>(emptyList())
    val agencies: StateFlow<List<Agency>> = _agencies.asStateFlow()

    private val _families = MutableStateFlow<List<Family>>(emptyList())
    val families: StateFlow<List<Family>> = _families.asStateFlow()

    // Support Tickets
    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // Simulated background activity job (e.g. users chatting in room, speech indicators)
    private var roomSimulationJob: Job? = null

    init {
        initializeSampleData()
        loadSavedSession()
    }

    private fun initializeSampleData() {
        val defaultGifts = listOf(
            Gift("g_rose", "Lucky Rose", "🌹", 10, GiftCategory.POPULAR, "Send a sweet fragrant rose", 0xFFFF4081),
            Gift("g_perfume", "Diamond Perfume", "💎", 99, GiftCategory.POPULAR, "Sparkling aura fragrance", 0xFF00E5FF),
            Gift("g_ring", "Diamond Ring", "💍", 520, GiftCategory.ROMANCE, "Symbol of deep affection", 0xFFFFD700),
            Gift("g_car", "Cyber Sports Car", "🏎️", 1314, GiftCategory.LUXURY, "Roar onto the stage", 0xFFFF3D00),
            Gift("g_castle", "Royal Palace", "🏰", 5200, GiftCategory.LUXURY, "Crown the room in majesty", 0xFF9C27B0),
            Gift("g_rocket", "Star Rocket", "🚀", 9999, GiftCategory.SPECIAL, "Blast soundwave to top rank", 0xFFFF9100),
            Gift("g_crown", "SVIP Imperial Crown", "👑", 18888, GiftCategory.SPECIAL, "Full-screen luxury coronation", 0xFFFFD700),
            Gift("g_heart", "Cupid Heart", "💖", 66, GiftCategory.ROMANCE, "Spread positive warmth", 0xFFE91E63)
        )
        _gifts.value = defaultGifts

        val defaultStore = listOf(
            StoreItem("frame_gold_crown", "Imperial Gold Crown Frame", ItemType.FRAME, 2000, 30, "👑", 0xFFFFD700, 0xFFFFA000, "Shimmering imperial gold avatar frame."),
            StoreItem("frame_cyber_neon", "Cyber Neon Ring Frame", ItemType.FRAME, 1500, 30, "⚡", 0xFF00E5FF, 0xFF9D4EDD, "Pulsing neon cyberpunk aura border."),
            StoreItem("frame_rose_romance", "Rose Romance Frame", ItemType.FRAME, 1200, 30, "🌹", 0xFFFF4081, 0xFFC2185B, "Enchanted blooming petals border."),
            StoreItem("frame_vip_flame", "Dragon Flame Frame", ItemType.FRAME, 3500, 30, "🔥", 0xFFFF3D00, 0xFFFF9100, "Fierce animated flame edge for elite hosts."),
            StoreItem("ent_jet", "Private Cyber Jet", ItemType.ENTRANCE, 5000, 30, "✈️", 0xFF00B0FF, 0xFF304FFE, "Arrival animation with jet sound."),
            StoreItem("badge_superstar", "Superstar Chat Badge", ItemType.BADGE, 800, 30, "⭐", 0xFFFFEB3B, 0xFFF57F17, "Gold superstar chat icon.")
        )
        _storeItems.value = defaultStore

        val sampleUsers = listOf(
            User(100101, "alina_star", "Alina Star 🎤", "", Gender.FEMALE, "Official Bisma Host | Singing & Chill", 18, 4500, 14, 18000, 22, 34000, VipTier.VIP_DIAMOND, 45000, 89000, 1240, 210, 180, "frame_gold_crown", 501, "Star Galaxy Agency", 101, "Royal Knights", role = UserRole.HOST),
            User(100102, "dj_rayyan", "DJ Rayyan 🎧", "", Gender.MALE, "Night vibe beats & hip-hop party", 15, 3200, 11, 9500, 16, 21000, VipTier.VIP_PLATINUM, 22000, 31000, 920, 140, 95, "frame_cyber_neon", 501, "Star Galaxy Agency", role = UserRole.HOST),
            User(100103, "zoya_queen", "Queen Zoya 👑", "", Gender.FEMALE, "Chatting with good souls only", 14, 2800, 19, 32000, 12, 14500, VipTier.VIP_GOLD, 68000, 15000, 1580, 80, 110, "frame_vip_flame", 502, "Apex Talents", 101, "Royal Knights"),
            User(100104, "hamza_boss", "Hamza Boss 💼", "", Gender.MALE, "Investor & room patron", 20, 9000, 25, 78000, 8, 8000, VipTier.VIP_DIAMOND, 120000, 8500, 2400, 300, 210, "frame_gold_crown", role = UserRole.AGENCY_LEADER),
            User(100105, "maya_love", "Maya Romance 💕", "", Gender.FEMALE, "Looking for meaningful talk & laughter", 9, 1200, 6, 3200, 14, 18200, VipTier.VIP_SILVER, 8500, 12000, 640, 95, 75, "frame_rose_romance")
        )
        _users.value = sampleUsers

        // Seed sample Voice Rooms with different seat layouts (8, 10, 15, 20)
        _rooms.value = listOf(
            createSampleRoom(88201, "🌟 Midnight Karaoke & Acoustic Live", "Sing your heart out! Polite guests only.", sampleUsers[0], RoomCategory.MUSIC, 10, listOf(sampleUsers[0], sampleUsers[1], sampleUsers[2])),
            createSampleRoom(88202, "🎮 Chill Gaming Lounge & Squad Up", "Gaming banter, laughs and mini-games!", sampleUsers[1], RoomCategory.GAMING, 8, listOf(sampleUsers[1], sampleUsers[3])),
            createSampleRoom(88203, "☕ Late Night Tea & Deep Talks", "Share your thoughts, relax after a long day.", sampleUsers[2], RoomCategory.CHAT, 15, listOf(sampleUsers[2], sampleUsers[4])),
            createSampleRoom(88204, "👑 High Rollers & VIP Party Mega Hall", "Welcome to the 20-seat luxury party room!", sampleUsers[3], RoomCategory.PARTY, 20, listOf(sampleUsers[3], sampleUsers[0], sampleUsers[4]))
        )

        // Sample Moments
        _moments.value = listOf(
            Moment(
                id = "m_1",
                authorId = 100101,
                authorName = "Alina Star 🎤",
                authorAvatar = "",
                authorVip = VipTier.VIP_DIAMOND,
                text = "Huge thanks to everyone who joined our weekend voice concert! We reached #1 in trending rooms tonight! 💖🎉",
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                likesCount = 48,
                comments = listOf(
                    MomentComment(id = "c_1", authorId = 100102L, authorName = "DJ Rayyan 🎧", text = "Fire performance Alina! 🔥", timestamp = System.currentTimeMillis() - 3600000),
                    MomentComment(id = "c_2", authorId = 100103L, authorName = "Queen Zoya 👑", text = "Loved the acoustic songs so much ✨", timestamp = System.currentTimeMillis() - 1800000)
                )
            ),
            Moment(
                id = "m_2",
                authorId = 100103,
                authorName = "Queen Zoya 👑",
                authorAvatar = "",
                authorVip = VipTier.VIP_GOLD,
                text = "New week, new goals! Don't forget to smile and be kind to someone today 🌸✨",
                timestamp = System.currentTimeMillis() - 3600000 * 7,
                likesCount = 32
            )
        )

        _agencies.value = listOf(
            Agency(501, "Star Galaxy Agency", "STAR-01", 100104, "Hamza Boss 💼", "Premier talent and voice host agency on Bisma Live.", 7, 58, 4800000),
            Agency(502, "Apex Talents", "APEX-99", 100102, "DJ Rayyan 🎧", "Youthful music and podcast creators network.", 4, 32, 1950000)
        )

        _families.value = listOf(
            Family(101, "Royal Knights", 100104, "Hamza Boss 💼", "👑 KNIGHTS", 5, 42, "Strength through loyalty! Elite room family."),
            Family(102, "Golden Phoenix", 100101, "Alina Star 🎤", "🔥 PHOENIX", 4, 28, "Music lovers and joyful creators united.")
        )
    }

    private fun createSampleRoom(
        id: Long,
        title: String,
        announcement: String,
        host: User,
        category: RoomCategory,
        seatCount: Int,
        seatedUsers: List<User>
    ): VoiceRoom {
        val seats = mutableListOf<RoomSeat>()
        for (i in 0 until seatCount) {
            val user = seatedUsers.getOrNull(i)
            seats.add(
                RoomSeat(
                    seatIndex = i,
                    seatNumber = i + 1,
                    userId = user?.id,
                    userName = user?.displayName,
                    userAvatar = user?.avatarUrl,
                    vipTier = user?.vipTier ?: VipTier.NONE,
                    frameId = user?.equippedFrameId,
                    isMuted = false,
                    isLocked = false,
                    isSpeaking = i == 0
                )
            )
        }
        return VoiceRoom(
            id = id,
            title = title,
            announcement = announcement,
            hostUserId = host.id,
            hostName = host.displayName,
            hostAvatar = host.avatarUrl,
            hostVip = host.vipTier,
            category = category,
            seatCount = seatCount,
            activeMembersCount = seatCount + 14,
            seats = seats
        )
    }

    private fun loadSavedSession() {
        val savedUserId = prefs.getLong("saved_user_id", -1L)
        if (savedUserId != -1L) {
            // Restore user
            val restored = User(
                id = savedUserId,
                username = prefs.getString("username", "my_bisma") ?: "my_bisma",
                displayName = prefs.getString("display_name", "Bisma Star") ?: "Bisma Star",
                avatarUrl = prefs.getString("avatar_url", "") ?: "",
                gender = Gender.valueOf(prefs.getString("gender", Gender.MALE.name) ?: Gender.MALE.name),
                bio = prefs.getString("bio", "Loving Bisma Live 🌟") ?: "Loving Bisma Live 🌟",
                userLevel = prefs.getInt("user_level", 12),
                userXp = prefs.getInt("user_xp", 2800),
                richLevel = prefs.getInt("rich_level", 9),
                richXp = prefs.getInt("rich_xp", 7500),
                charmLevel = prefs.getInt("charm_level", 14),
                charmXp = prefs.getInt("charm_xp", 13200),
                vipTier = VipTier.entries.firstOrNull { it.name == prefs.getString("vip_tier", VipTier.VIP_GOLD.name) } ?: VipTier.VIP_GOLD,
                coins = prefs.getLong("coins", 25000L),
                diamonds = prefs.getLong("diamonds", 5400L),
                followersCount = prefs.getInt("followers", 412),
                followingCount = prefs.getInt("following", 76),
                friendsCount = prefs.getInt("friends", 38),
                equippedFrameId = prefs.getString("frame", "frame_gold_crown") ?: "frame_gold_crown",
                role = UserRole.valueOf(prefs.getString("role", UserRole.ADMIN.name) ?: UserRole.ADMIN.name)
            )
            _currentUser.value = restored
            _isLoggedIn.value = true
        } else {
            // Default demo primary user
            val defaultUser = User(
                id = 108420,
                username = "shahroz_live",
                displayName = "Shahroz ✨",
                avatarUrl = "",
                gender = Gender.MALE,
                bio = "Voice host & Bisma VIP Ambassador 👑🎙️",
                userLevel = 14,
                userXp = 3850,
                richLevel = 10,
                richXp = 9200,
                charmLevel = 16,
                charmXp = 17400,
                vipTier = VipTier.VIP_GOLD,
                coins = 32500,
                diamonds = 6800,
                followersCount = 520,
                followingCount = 42,
                friendsCount = 28,
                equippedFrameId = "frame_gold_crown",
                role = UserRole.ADMIN
            )
            _currentUser.value = defaultUser
            _isLoggedIn.value = true
            saveUserSession(defaultUser)
        }

        // Init backpack with owned default frame
        _backpack.value = listOf(
            BackpackItem("bp_1", "frame_gold_crown", "Imperial Gold Crown Frame", ItemType.FRAME, "👑", isEquipped = true),
            BackpackItem("bp_2", "badge_superstar", "Superstar Chat Badge", ItemType.BADGE, "⭐", isEquipped = true)
        )

        // Initial system notifications
        _notifications.value = listOf(
            AppNotification(
                id = "n_1",
                type = NotificationType.SYSTEM,
                title = "Welcome to Bisma Live!",
                message = "Enjoy crystal-clear voice rooms, multi-seat stages, and send luxury gifts to your friends!",
                timestamp = System.currentTimeMillis() - 86400000
            ),
            AppNotification(
                id = "n_2",
                type = NotificationType.FRIEND_REQ,
                title = "Friend Request",
                message = "Alina Star 🎤 sent you a friend request.",
                timestamp = System.currentTimeMillis() - 1800000,
                relatedUserId = 100101,
                relatedRequestId = "req_101"
            )
        )

        _friendRequests.value = listOf(
            FriendRequest("req_101", 100101, "Alina Star 🎤", "", 108420)
        )
    }

    private fun saveUserSession(user: User) {
        prefs.edit().apply {
            putLong("saved_user_id", user.id)
            putString("username", user.username)
            putString("display_name", user.displayName)
            putString("avatar_url", user.avatarUrl)
            putString("gender", user.gender.name)
            putString("bio", user.bio)
            putInt("user_level", user.userLevel)
            putInt("user_xp", user.userXp)
            putInt("rich_level", user.richLevel)
            putInt("rich_xp", user.richXp)
            putInt("charm_level", user.charmLevel)
            putInt("charm_xp", user.charmXp)
            putString("vip_tier", user.vipTier.name)
            putLong("coins", user.coins)
            putLong("diamonds", user.diamonds)
            putInt("followers", user.followersCount)
            putInt("following", user.followingCount)
            putInt("friends", user.friendsCount)
            putString("frame", user.equippedFrameId)
            putString("role", user.role.name)
            apply()
        }
    }

    // --- Authentication ---
    fun login(username: String, pass: String): Boolean {
        val user = _currentUser.value ?: User(
            id = (100000L..999999L).random(),
            username = username,
            displayName = username.replaceFirstChar { it.uppercase() }
        )
        val updated = user.copy(username = username)
        _currentUser.value = updated
        _isLoggedIn.value = true
        saveUserSession(updated)
        return true
    }

    fun register(username: String, displayName: String, gender: Gender): Boolean {
        val newUser = User(
            id = (100000L..999999L).random(),
            username = username,
            displayName = displayName,
            gender = gender,
            coins = 10000 // Welcome coins bonus!
        )
        _currentUser.value = newUser
        _isLoggedIn.value = true
        saveUserSession(newUser)
        recordTransaction(TransactionType.RECHARGE, 10000, true, "Welcome Starter Gift Coins")
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
        prefs.edit().clear().apply()
    }

    // --- Profile Editing ---
    fun updateProfile(displayName: String, bio: String, gender: Gender, avatarUri: String) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            displayName = displayName.ifBlank { current.displayName },
            bio = bio,
            gender = gender,
            avatarUrl = if (avatarUri.isNotBlank()) avatarUri else current.avatarUrl
        )
        _currentUser.value = updated
        saveUserSession(updated)
    }

    // --- Social (Follow & Friends) ---
    fun toggleFollow(targetUserId: Long): Boolean {
        val current = _currentUser.value ?: return false
        val following = _followingIds.value.toMutableSet()
        val isNowFollowing = if (following.contains(targetUserId)) {
            following.remove(targetUserId)
            val updatedMe = current.copy(followingCount = (current.followingCount - 1).coerceAtLeast(0))
            _currentUser.value = updatedMe
            saveUserSession(updatedMe)
            false
        } else {
            following.add(targetUserId)
            val updatedMe = current.copy(followingCount = current.followingCount + 1)
            _currentUser.value = updatedMe
            saveUserSession(updatedMe)

            // Send notification to target user
            _notifications.value = listOf(
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    type = NotificationType.FOLLOW,
                    title = "New Follower",
                    message = "${current.displayName} started following you!",
                    relatedUserId = current.id
                )
            ) + _notifications.value
            true
        }
        _followingIds.value = following
        return isNowFollowing
    }

    fun isFollowing(userId: Long): Boolean = _followingIds.value.contains(userId)

    fun sendFriendRequest(targetUserId: Long): Boolean {
        val current = _currentUser.value ?: return false
        val existing = _friendRequests.value.find { it.fromUserId == current.id && it.toUserId == targetUserId }
        if (existing != null) return false

        val req = FriendRequest(
            id = UUID.randomUUID().toString(),
            fromUserId = current.id,
            fromUserName = current.displayName,
            fromUserAvatar = current.avatarUrl,
            toUserId = targetUserId
        )
        _friendRequests.value = _friendRequests.value + req

        // Real notification
        _notifications.value = listOf(
            AppNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.FRIEND_REQ,
                title = "Friend Request",
                message = "${current.displayName} sent you a friend request.",
                relatedUserId = current.id,
                relatedRequestId = req.id
            )
        ) + _notifications.value
        return true
    }

    fun acceptFriendRequest(requestId: String) {
        val req = _friendRequests.value.find { it.id == requestId } ?: return
        _friendRequests.value = _friendRequests.value.map {
            if (it.id == requestId) it.copy(status = FriendRequestStatus.ACCEPTED) else it
        }
        val friends = _friendIds.value.toMutableSet()
        friends.add(req.fromUserId)
        _friendIds.value = friends

        val current = _currentUser.value
        if (current != null) {
            val updated = current.copy(friendsCount = current.friendsCount + 1)
            _currentUser.value = updated
            saveUserSession(updated)
        }

        // Notification of acceptance
        _notifications.value = listOf(
            AppNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.SYSTEM,
                title = "Friend Request Accepted",
                message = "You and ${req.fromUserName} are now friends!"
            )
        ) + _notifications.value
    }

    fun rejectFriendRequest(requestId: String) {
        _friendRequests.value = _friendRequests.value.filterNot { it.id == requestId }
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    // --- Voice Rooms ---
    fun createVoiceRoom(
        title: String,
        category: RoomCategory,
        seatCount: Int,
        announcement: String
    ): VoiceRoom {
        val host = _currentUser.value ?: error("Not logged in")
        val newRoomId = (80000L..89999L).random()
        val seats = (0 until seatCount).map { i ->
            if (i == 0) {
                // Host takes Seat 1
                RoomSeat(
                    seatIndex = 0,
                    seatNumber = 1,
                    userId = host.id,
                    userName = host.displayName,
                    userAvatar = host.avatarUrl,
                    vipTier = host.vipTier,
                    frameId = host.equippedFrameId,
                    isSpeaking = false
                )
            } else {
                RoomSeat(seatIndex = i, seatNumber = i + 1)
            }
        }
        val room = VoiceRoom(
            id = newRoomId,
            title = title.ifBlank { "${host.displayName}'s Lounge" },
            announcement = announcement.ifBlank { "Welcome to my room! Enjoy the music and chats 🎙️" },
            hostUserId = host.id,
            hostName = host.displayName,
            hostAvatar = host.avatarUrl,
            hostVip = host.vipTier,
            category = category,
            seatCount = seatCount,
            activeMembersCount = 1,
            seats = seats
        )
        _rooms.value = listOf(room) + _rooms.value
        enterRoom(room)
        return room
    }

    fun enterRoom(room: VoiceRoom) {
        _currentRoom.value = room
        val me = _currentUser.value ?: return

        // Post enter message
        val enterMsg = RoomChatMessage(
            id = UUID.randomUUID().toString(),
            roomId = room.id,
            senderId = me.id,
            senderName = me.displayName,
            senderAvatar = me.avatarUrl,
            senderVip = me.vipTier,
            type = MessageType.ENTER,
            content = "entered the room"
        )
        _roomMessages.value = listOf(enterMsg)

        startRoomSimulation(room.id)
    }

    fun leaveRoom() {
        val room = _currentRoom.value
        val me = _currentUser.value
        if (room != null && me != null) {
            // Vacate seat if seated
            val updatedSeats = room.seats.map { seat ->
                if (seat.userId == me.id) {
                    seat.copy(userId = null, userName = null, userAvatar = null, isSpeaking = false)
                } else seat
            }
            val updatedRoom = room.copy(
                seats = updatedSeats,
                activeMembersCount = (room.activeMembersCount - 1).coerceAtLeast(1)
            )
            updateRoomInList(updatedRoom)
        }
        roomSimulationJob?.cancel()
        _currentRoom.value = null
        _roomMessages.value = emptyList()
    }

    fun joinSeat(seatIndex: Int): Boolean {
        val room = _currentRoom.value ?: return false
        val me = _currentUser.value ?: return false
        val seat = room.seats.getOrNull(seatIndex) ?: return false

        if (seat.isLocked || seat.userId != null) return false

        // Remove from any prior seat
        val updatedSeats = room.seats.map { s ->
            if (s.userId == me.id) {
                s.copy(userId = null, userName = null, userAvatar = null, isSpeaking = false)
            } else if (s.seatIndex == seatIndex) {
                s.copy(
                    userId = me.id,
                    userName = me.displayName,
                    userAvatar = me.avatarUrl,
                    vipTier = me.vipTier,
                    frameId = me.equippedFrameId,
                    isSpeaking = false
                )
            } else s
        }

        val updatedRoom = room.copy(seats = updatedSeats)
        _currentRoom.value = updatedRoom
        updateRoomInList(updatedRoom)

        sendRoomMessage("sat on seat #${seat.seatNumber}", MessageType.SYSTEM)
        return true
    }

    fun leaveSeat(): Boolean {
        val room = _currentRoom.value ?: return false
        val me = _currentUser.value ?: return false

        val updatedSeats = room.seats.map { seat ->
            if (seat.userId == me.id) {
                seat.copy(userId = null, userName = null, userAvatar = null, isSpeaking = false)
            } else seat
        }
        val updatedRoom = room.copy(seats = updatedSeats)
        _currentRoom.value = updatedRoom
        updateRoomInList(updatedRoom)
        sendRoomMessage("stepped down from seat", MessageType.SYSTEM)
        return true
    }

    fun isUserOnSeat(userId: Long): Boolean {
        val room = _currentRoom.value ?: return false
        return room.seats.any { it.userId == userId }
    }

    fun toggleLockSeat(seatIndex: Int) {
        val room = _currentRoom.value ?: return
        val updatedSeats = room.seats.map { seat ->
            if (seat.seatIndex == seatIndex) {
                seat.copy(isLocked = !seat.isLocked)
            } else seat
        }
        val updatedRoom = room.copy(seats = updatedSeats)
        _currentRoom.value = updatedRoom
        updateRoomInList(updatedRoom)
    }

    fun toggleMuteSeat(seatIndex: Int) {
        val room = _currentRoom.value ?: return
        val updatedSeats = room.seats.map { seat ->
            if (seat.seatIndex == seatIndex) {
                seat.copy(isMuted = !seat.isMuted)
            } else seat
        }
        val updatedRoom = room.copy(seats = updatedSeats)
        _currentRoom.value = updatedRoom
        updateRoomInList(updatedRoom)
    }

    fun kickUserFromSeat(seatIndex: Int) {
        val room = _currentRoom.value ?: return
        val updatedSeats = room.seats.map { seat ->
            if (seat.seatIndex == seatIndex) {
                seat.copy(userId = null, userName = null, userAvatar = null, isSpeaking = false)
            } else seat
        }
        val updatedRoom = room.copy(seats = updatedSeats)
        _currentRoom.value = updatedRoom
        updateRoomInList(updatedRoom)
    }

    fun sendRoomMessage(text: String, type: MessageType = MessageType.TEXT) {
        val room = _currentRoom.value ?: return
        val me = _currentUser.value ?: return
        val msg = RoomChatMessage(
            id = UUID.randomUUID().toString(),
            roomId = room.id,
            senderId = me.id,
            senderName = me.displayName,
            senderAvatar = me.avatarUrl,
            senderVip = me.vipTier,
            type = type,
            content = text
        )
        _roomMessages.value = _roomMessages.value + msg
    }

    fun sendGift(gift: Gift, count: Int = 1, targetUserName: String? = null): Boolean {
        val current = _currentUser.value ?: return false
        val room = _currentRoom.value
        val totalCost = gift.priceCoins * count

        if (current.coins < totalCost) {
            return false // Insufficient funds
        }

        // Deduct coins & record transaction
        val remainingCoins = current.coins - totalCost
        val xpGain = (totalCost / 5).toInt().coerceAtLeast(1)
        val newRichXp = current.richXp + totalCost.toInt()
        val newRichLevel = (newRichXp / 1000) + 1

        val updated = current.copy(
            coins = remainingCoins,
            richXp = newRichXp,
            richLevel = newRichLevel,
            userXp = current.userXp + xpGain,
            userLevel = ((current.userXp + xpGain) / 500) + 1
        )
        _currentUser.value = updated
        saveUserSession(updated)

        recordTransaction(
            TransactionType.GIFT_SENT,
            totalCost,
            false,
            "Sent ${count}x ${gift.name} in Room #${room?.id ?: "Live"}"
        )

        // Trigger gift animation overlay
        _activeGiftAnimation.value = gift
        scope.launch {
            delay(3500)
            if (_activeGiftAnimation.value == gift) {
                _activeGiftAnimation.value = null
            }
        }

        // Broadcast gift message into room
        if (room != null) {
            val giftMsg = RoomChatMessage(
                id = UUID.randomUUID().toString(),
                roomId = room.id,
                senderId = current.id,
                senderName = current.displayName,
                senderAvatar = current.avatarUrl,
                senderVip = current.vipTier,
                type = MessageType.GIFT,
                content = "sent ${count}x ${gift.name} ${gift.iconEmoji}",
                giftId = gift.id,
                giftName = gift.name,
                giftCount = count,
                targetUserName = targetUserName ?: room.hostName
            )
            _roomMessages.value = _roomMessages.value + giftMsg

            val updatedRoom = room.copy(totalCoinsReceived = room.totalCoinsReceived + totalCost)
            _currentRoom.value = updatedRoom
            updateRoomInList(updatedRoom)
        }

        return true
    }

    private fun updateRoomInList(room: VoiceRoom) {
        _rooms.value = _rooms.value.map { if (it.id == room.id) room else it }
    }

    // Real-time simulated speech and ambient interactions inside the room
    private fun startRoomSimulation(roomId: Long) {
        roomSimulationJob?.cancel()
        roomSimulationJob = scope.launch {
            val sampleChatters = listOf(
                "Great music tonight! 🎵",
                "Hello everyone in the room! 👋",
                "Can someone play acoustic guitar? ✨",
                "Bisma Live is the best voice app! 🔥",
                "Welcome new members to the stage! 👑",
                "Sending love from London! 💖"
            )

            while (isActive) {
                delay(3000 + Random.nextLong(3500))
                val room = _currentRoom.value ?: break
                if (room.id != roomId) break

                // Randomly toggle speaking indicator on an occupied seat
                val occupiedSeats = room.seats.filter { it.userId != null }
                if (occupiedSeats.isNotEmpty()) {
                    val speakingSeat = occupiedSeats.random()
                    val newSeats = room.seats.map { s ->
                        if (s.seatIndex == speakingSeat.seatIndex) {
                            s.copy(isSpeaking = !s.isSpeaking)
                        } else {
                            s.copy(isSpeaking = false)
                        }
                    }
                    _currentRoom.value = room.copy(seats = newSeats)
                }

                // Occasionally generate an ambient chat comment from audience
                if (Random.nextInt(4) == 0) {
                    val randomUser = _users.value.random()
                    val chatMsg = RoomChatMessage(
                        id = UUID.randomUUID().toString(),
                        roomId = roomId,
                        senderId = randomUser.id,
                        senderName = randomUser.displayName,
                        senderAvatar = randomUser.avatarUrl,
                        senderVip = randomUser.vipTier,
                        type = MessageType.TEXT,
                        content = sampleChatters.random()
                    )
                    _roomMessages.value = (_roomMessages.value + chatMsg).takeLast(60)
                }
            }
        }
    }

    // --- Wallet & Recharge ---
    fun rechargeCoins(pack: RechargePackage): Boolean {
        val current = _currentUser.value ?: return false
        val totalAdded = pack.coins + pack.bonusCoins
        val updated = current.copy(
            coins = current.coins + totalAdded,
            richXp = current.richXp + (totalAdded / 2).toInt(),
            richLevel = ((current.richXp + (totalAdded / 2).toInt()) / 1000) + 1
        )
        _currentUser.value = updated
        saveUserSession(updated)

        recordTransaction(
            TransactionType.RECHARGE,
            totalAdded,
            true,
            "Recharged Package \$${pack.priceUsd} (+${pack.coins} Coins +${pack.bonusCoins} Bonus)"
        )
        return true
    }

    fun requestWithdrawal(diamondsAmount: Long, method: String): Boolean {
        val current = _currentUser.value ?: return false
        if (current.diamonds < diamondsAmount || diamondsAmount <= 0) return false

        val updated = current.copy(diamonds = current.diamonds - diamondsAmount)
        _currentUser.value = updated
        saveUserSession(updated)

        val cashEquivalent = (diamondsAmount / 100).toDouble()
        recordTransaction(
            TransactionType.WITHDRAWAL,
            diamondsAmount,
            false,
            "Host cashout request: \$$cashEquivalent via $method"
        )
        return true
    }

    private fun recordTransaction(type: TransactionType, amount: Long, isCredit: Boolean, desc: String) {
        val tx = WalletTransaction(
            id = "tx_" + System.currentTimeMillis().toString().takeLast(6),
            type = type,
            amountCoins = amount,
            isCredit = isCredit,
            description = desc
        )
        _transactions.value = listOf(tx) + _transactions.value
    }

    // --- VIP Upgrade ---
    fun upgradeVip(targetTier: VipTier): Boolean {
        val current = _currentUser.value ?: return false
        val cost = when (targetTier) {
            VipTier.VIP_BRONZE -> 3000L
            VipTier.VIP_SILVER -> 8000L
            VipTier.VIP_GOLD -> 18000L
            VipTier.VIP_PLATINUM -> 35000L
            VipTier.VIP_DIAMOND -> 60000L
            VipTier.NONE -> 0L
        }
        if (current.coins < cost) return false

        val updated = current.copy(
            coins = current.coins - cost,
            vipTier = targetTier
        )
        _currentUser.value = updated
        saveUserSession(updated)

        recordTransaction(TransactionType.STORE_PURCHASE, cost, false, "Upgraded to ${targetTier.title}")

        // Add celebratory notification
        _notifications.value = listOf(
            AppNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.SYSTEM,
                title = "VIP Activated!",
                message = "Congratulations! You have unlocked ${targetTier.title} benefits on Bisma Live."
            )
        ) + _notifications.value

        return true
    }

    // --- Store & Backpack ---
    fun purchaseStoreItem(item: StoreItem): Boolean {
        val current = _currentUser.value ?: return false
        if (current.coins < item.priceCoins) return false

        val updatedUser = current.copy(coins = current.coins - item.priceCoins)
        _currentUser.value = updatedUser
        saveUserSession(updatedUser)

        val bpItem = BackpackItem(
            id = "bp_" + UUID.randomUUID().toString().take(6),
            storeItemId = item.id,
            name = item.name,
            type = item.type,
            iconEmoji = item.iconEmoji,
            isEquipped = false
        )
        _backpack.value = _backpack.value + bpItem

        recordTransaction(TransactionType.STORE_PURCHASE, item.priceCoins, false, "Purchased ${item.name}")
        return true
    }

    fun equipBackpackItem(item: BackpackItem) {
        val current = _currentUser.value ?: return
        val updatedBp = _backpack.value.map {
            if (it.type == item.type) {
                it.copy(isEquipped = (it.id == item.id))
            } else it
        }
        _backpack.value = updatedBp

        if (item.type == ItemType.FRAME) {
            val updatedUser = current.copy(equippedFrameId = item.storeItemId)
            _currentUser.value = updatedUser
            saveUserSession(updatedUser)
        }
    }

    fun unequipBackpackItem(item: BackpackItem) {
        val current = _currentUser.value ?: return
        val updatedBp = _backpack.value.map {
            if (it.id == item.id) it.copy(isEquipped = false) else it
        }
        _backpack.value = updatedBp

        if (item.type == ItemType.FRAME && current.equippedFrameId == item.storeItemId) {
            val updatedUser = current.copy(equippedFrameId = "")
            _currentUser.value = updatedUser
            saveUserSession(updatedUser)
        }
    }

    // --- Moments ---
    fun publishMoment(text: String, imageUri: String?): Moment {
        val current = _currentUser.value ?: error("Not logged in")
        val moment = Moment(
            id = "m_" + UUID.randomUUID().toString().take(8),
            authorId = current.id,
            authorName = current.displayName,
            authorAvatar = current.avatarUrl,
            authorVip = current.vipTier,
            text = text,
            imageUri = imageUri,
            likesCount = 0
        )
        _moments.value = listOf(moment) + _moments.value

        // Award Activity XP
        val updated = current.copy(userXp = current.userXp + 50)
        _currentUser.value = updated
        saveUserSession(updated)

        return moment
    }

    fun toggleLikeMoment(momentId: String) {
        _moments.value = _moments.value.map { m ->
            if (m.id == momentId) {
                if (m.isLikedByMe) {
                    m.copy(isLikedByMe = false, likesCount = (m.likesCount - 1).coerceAtLeast(0))
                } else {
                    m.copy(isLikedByMe = true, likesCount = m.likesCount + 1)
                }
            } else m
        }
    }

    fun addMomentComment(momentId: String, text: String) {
        val me = _currentUser.value ?: return
        val comment = MomentComment(
            id = UUID.randomUUID().toString(),
            authorId = me.id,
            authorName = me.displayName,
            text = text,
            authorAvatar = me.avatarUrl
        )
        _moments.value = _moments.value.map { m ->
            if (m.id == momentId) {
                m.copy(comments = m.comments + comment)
            } else m
        }
    }

    fun deleteMoment(momentId: String) {
        _moments.value = _moments.value.filterNot { it.id == momentId }
    }

    // --- Games inside room (Lucky 77, Greedy Cat, Lucky Box, Greedy Lion) ---
    fun playRoomGame(gameName: String, betAmount: Long): Pair<Boolean, Long> {
        val current = _currentUser.value ?: return Pair(false, 0L)
        if (current.coins < betAmount || betAmount <= 0) return Pair(false, 0L)

        // Deduct bet
        recordTransaction(TransactionType.GAME_BET, betAmount, false, "$gameName Bet Stake")

        // Game odds calculation
        val outcome = Random.nextInt(100)
        val (isWin, multiplier) = when {
            outcome < 40 -> Pair(true, 2.0)
            outcome < 55 -> Pair(true, 3.5)
            outcome < 62 -> Pair(true, 7.7)
            else -> Pair(false, 0.0)
        }

        val winCoins = if (isWin) (betAmount * multiplier).toLong() else 0L
        val netChange = winCoins - betAmount

        val updated = current.copy(coins = current.coins + netChange)
        _currentUser.value = updated
        saveUserSession(updated)

        if (isWin) {
            recordTransaction(TransactionType.GAME_WIN, winCoins, true, "$gameName Win Multiplier (${multiplier}x)!")
            sendRoomMessage("won $winCoins coins in $gameName! 🎰🎉", MessageType.SYSTEM)
        }

        return Pair(isWin, winCoins)
    }

    // --- Support & Tickets ---
    fun submitSupportTicket(category: String, description: String): Boolean {
        val me = _currentUser.value ?: return false
        val ticket = SupportTicket(
            id = "tkt_" + UUID.randomUUID().toString().take(6),
            userId = me.id,
            category = category,
            description = description
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        return true
    }

    // --- Admin / Moderation ---
    fun adminBanUser(userId: Long) {
        _users.value = _users.value.map {
            if (it.id == userId) it.copy(isBanned = true) else it
        }
    }

    fun adminLockRoom(roomId: Long) {
        _rooms.value = _rooms.value.map {
            if (it.id == roomId) it.copy(isLocked = true) else it
        }
    }

    fun adminBroadcast(message: String) {
        _notifications.value = listOf(
            AppNotification(
                id = UUID.randomUUID().toString(),
                type = NotificationType.SYSTEM,
                title = "Official Admin Announcement",
                message = message
            )
        ) + _notifications.value
    }
}
