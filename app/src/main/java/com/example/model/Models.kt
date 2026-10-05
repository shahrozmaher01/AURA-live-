package com.example.model

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class UserRole {
    USER, HOST, AGENCY_LEADER, ADMIN, OWNER
}

enum class VipTier(val level: Int, val title: String, val badgeColor: Long) {
    NONE(0, "Regular", 0xFF888888),
    VIP_BRONZE(1, "VIP Bronze", 0xFFCD7F32),
    VIP_SILVER(2, "VIP Silver", 0xFFC0C0C0),
    VIP_GOLD(3, "VIP Gold", 0xFFFFD700),
    VIP_PLATINUM(4, "VIP Platinum", 0xFFE5E4E2),
    VIP_DIAMOND(5, "SVIP Diamond", 0xFF00E5FF)
}

data class User(
    val id: Long,
    val username: String,
    val displayName: String,
    val avatarUrl: String = "",
    val gender: Gender = Gender.MALE,
    val bio: String = "Living in the moment on Bisma Live ✨",
    val userLevel: Int = 12,
    val userXp: Int = 2450,
    val richLevel: Int = 8,
    val richXp: Int = 6200,
    val charmLevel: Int = 15,
    val charmXp: Int = 15400,
    val vipTier: VipTier = VipTier.VIP_GOLD,
    val coins: Long = 18500,
    val diamonds: Long = 4200,
    val followersCount: Int = 342,
    val followingCount: Int = 89,
    val friendsCount: Int = 45,
    val equippedFrameId: String = "frame_gold_crown",
    val agencyId: Long? = 501,
    val agencyName: String? = "Star Galaxy Agency",
    val familyId: Long? = 101,
    val familyName: String? = "Royal Knights",
    val cpUserId: Long? = null,
    val role: UserRole = UserRole.ADMIN,
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class RoomCategory(val displayName: String) {
    CHAT("💬 Chit-Chat"),
    MUSIC("🎵 Music & Singing"),
    GAMING("🎮 Gaming"),
    PARTY("🎉 Party"),
    DATING("💖 Romance & Dating"),
    FRIENDS("🌟 Friendship")
}

data class RoomSeat(
    val seatIndex: Int,
    val seatNumber: Int,
    val userId: Long? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val vipTier: VipTier = VipTier.NONE,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isSpeaking: Boolean = false,
    val frameId: String? = null
)

data class VoiceRoom(
    val id: Long,
    val title: String,
    val announcement: String = "Welcome to our Bisma Live room! Be kind and enjoy the music 🎙️",
    val hostUserId: Long,
    val hostName: String,
    val hostAvatar: String,
    val hostVip: VipTier = VipTier.VIP_GOLD,
    val category: RoomCategory = RoomCategory.CHAT,
    val seatCount: Int = 10, // 8, 10, 15, 20
    val isLocked: Boolean = false,
    val password: String = "",
    val isMutedAll: Boolean = false,
    val backgroundTheme: String = "theme_galaxy",
    val activeMembersCount: Int = 28,
    val seats: List<RoomSeat> = emptyList(),
    val totalCoinsReceived: Long = 124500,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MessageType {
    TEXT, SYSTEM, GIFT, ENTER, LEAVE
}

data class RoomChatMessage(
    val id: String,
    val roomId: Long,
    val senderId: Long,
    val senderName: String,
    val senderAvatar: String,
    val senderVip: VipTier = VipTier.NONE,
    val type: MessageType = MessageType.TEXT,
    val content: String,
    val giftId: String? = null,
    val giftName: String? = null,
    val giftCount: Int = 1,
    val targetUserName: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GiftCategory(val title: String) {
    POPULAR("🔥 Popular"),
    LUXURY("💎 Luxury"),
    ROMANCE("🌹 Romance"),
    SPECIAL("👑 Special")
}

data class Gift(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val priceCoins: Long,
    val category: GiftCategory,
    val description: String,
    val animationColor: Long
)

data class MomentComment(
    val id: String,
    val authorId: Long,
    val authorName: String,
    val text: String,
    val authorAvatar: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Moment(
    val id: String,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String,
    val authorVip: VipTier = VipTier.NONE,
    val text: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val comments: List<MomentComment> = emptyList()
)

enum class TransactionType(val title: String) {
    RECHARGE("Wallet Recharge"),
    GIFT_SENT("Gift Sent"),
    GIFT_RECEIVED("Gift Received"),
    GAME_WIN("Game Reward"),
    GAME_BET("Game Stake"),
    WITHDRAWAL("Host Cashout"),
    STORE_PURCHASE("Store Purchase")
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val amountCoins: Long,
    val isCredit: Boolean,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class RechargePackage(
    val id: String,
    val coins: Long,
    val bonusCoins: Long = 0,
    val priceUsd: Double,
    val isHot: Boolean = false
)

enum class ItemType(val title: String) {
    FRAME("Avatar Frame"),
    ENTRANCE("Entrance Effect"),
    BADGE("Chat Badge"),
    ROOM_THEME("Room Theme")
}

data class StoreItem(
    val id: String,
    val name: String,
    val type: ItemType,
    val priceCoins: Long,
    val durationDays: Int = 30,
    val iconEmoji: String,
    val previewGradientStart: Long,
    val previewGradientEnd: Long,
    val description: String
)

data class BackpackItem(
    val id: String,
    val storeItemId: String,
    val name: String,
    val type: ItemType,
    val iconEmoji: String,
    val isEquipped: Boolean = false,
    val acquiredAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
)

data class Agency(
    val id: Long,
    val name: String,
    val code: String,
    val leaderUserId: Long,
    val leaderName: String,
    val description: String,
    val level: Int = 5,
    val memberCount: Int = 42,
    val totalEarningsCoins: Long = 1890000,
    val announcement: String = "Welcome to Star Galaxy Agency! Daily targets and weekly rewards."
)

data class Family(
    val id: Long,
    val name: String,
    val leaderUserId: Long,
    val leaderName: String,
    val badge: String = "👑 KNIGHTS",
    val level: Int = 4,
    val memberCount: Int = 35,
    val announcement: String = "Loyalty, Honor, and Unity. Bisma Live's finest family!"
)

enum class NotificationType(val icon: String) {
    FRIEND_REQ("🤝"),
    FOLLOW("👤"),
    GIFT("🎁"),
    SYSTEM("📢"),
    AGENCY("🏢"),
    FAMILY("🛡️")
}

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedUserId: Long? = null,
    val relatedRequestId: String? = null
)

enum class FriendRequestStatus {
    PENDING, ACCEPTED, REJECTED
}

data class FriendRequest(
    val id: String,
    val fromUserId: Long,
    val fromUserName: String,
    val fromUserAvatar: String,
    val toUserId: Long,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

data class SupportTicket(
    val id: String,
    val userId: Long,
    val category: String,
    val description: String,
    val status: String = "Open",
    val timestamp: Long = System.currentTimeMillis()
)
