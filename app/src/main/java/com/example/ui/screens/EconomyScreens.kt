package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.BismaTopBar
import com.example.ui.components.LuxuryButton
import com.example.ui.components.VipBadge
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    currentUser: User,
    rechargePackages: List<RechargePackage>,
    transactions: List<WalletTransaction>,
    onBack: () -> Unit,
    onRecharge: (RechargePackage) -> Boolean,
    onWithdrawal: (Long, String) -> Boolean
) {
    var showCashoutDialog by remember { mutableStateOf(false) }
    var rechargeSuccessMessage by remember { mutableStateOf<String?>(null) }
    var selectedTxFilter by remember { mutableStateOf(0) } // 0: All, 1: Recharges, 2: Gifts

    val filteredTx = remember(selectedTxFilter, transactions) {
        when (selectedTxFilter) {
            1 -> transactions.filter { it.type == TransactionType.RECHARGE }
            2 -> transactions.filter { it.type == TransactionType.GIFT_SENT || it.type == TransactionType.GIFT_RECEIVED }
            else -> transactions
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "My Wallet", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonPurpleLight)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Current Balance", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙", fontSize = 26.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "%,d".format(currentUser.coins),
                                        color = GoldAccent,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp
                                    )
                                }
                                Text("Available Coins", color = TextMuted, fontSize = 11.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💎", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "%,d".format(currentUser.diamonds),
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Text("Host Earnings", color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showCashoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalVioletCardAlt),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Withdraw Diamonds (\$$currentUser.diamonds/100)", color = NeonCyan, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (rechargeSuccessMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NeonPurple.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = rechargeSuccessMessage!!,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Recharge Packages Header
            item {
                Text(
                    text = "🪙 Coin Top-up Packages",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Grid of Recharge Packages
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    rechargePackages.chunked(2).forEach { rowPackages ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPackages.forEach { pack ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onRecharge(pack)
                                            rechargeSuccessMessage = "Successfully recharged ${pack.coins + pack.bonusCoins} Coins!"
                                        }
                                        .testTag("recharge_pack_${pack.id}"),
                                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                                    shape = RoundedCornerShape(14.dp),
                                    border = if (pack.isHot) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonPink))) else null
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (pack.isHot) {
                                            Text(
                                                text = "🔥 BEST VALUE",
                                                color = NeonPink,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                        Text(text = "🪙 %,d".format(pack.coins), color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        if (pack.bonusCoins > 0) {
                                            Text(text = "+%,d Bonus".format(pack.bonusCoins), color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(RoyalVioletCardAlt)
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "\$${pack.priceUsd}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Transaction History Header with Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📜 Transaction History", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row {
                        listOf("All", "Top-up", "Gifts").forEachIndexed { index, title ->
                            Text(
                                text = title,
                                color = if (selectedTxFilter == index) NeonMagenta else TextSecondary,
                                fontWeight = if (selectedTxFilter == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable { selectedTxFilter = index }
                                    .padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }

            if (filteredTx.isEmpty()) {
                item {
                    Text("No transactions recorded yet.", color = TextMuted, fontSize = 12.sp)
                }
            } else {
                items(filteredTx) { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(RoyalVioletCard)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.description, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(tx.type.title, color = TextSecondary, fontSize = 10.sp)
                        }
                        Text(
                            text = (if (tx.isCredit) "+" else "-") + " %,d 🪙".format(tx.amountCoins),
                            color = if (tx.isCredit) SpeakingGlow else GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Cashout / Withdrawal Dialog
        if (showCashoutDialog) {
            AlertDialog(
                onDismissRequest = { showCashoutDialog = false },
                title = { Text("Request Cashout", color = TextPrimary) },
                text = {
                    Column {
                        Text("Current Host Diamonds: 💎 %,d".format(currentUser.diamonds), color = NeonCyan)
                        Text("Cash equivalent: \$${currentUser.diamonds / 100}.00 USD", color = GoldAccent)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Select payout channel: Bank Transfer / Digital Wallet", color = TextSecondary, fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onWithdrawal(currentUser.diamonds.coerceAtMost(10000L), "Bank Transfer")
                            showCashoutDialog = false
                            rechargeSuccessMessage = "Cashout request submitted for processing!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                    ) {
                        Text("Confirm Payout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCashoutDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = RoyalVioletCard
            )
        }
    }
}

@Composable
fun VipScreen(
    currentUser: User,
    onBack: () -> Unit,
    onUpgrade: (VipTier) -> Boolean
) {
    var upgradeMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "VIP Privilege Center", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                    shape = RoundedCornerShape(18.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, Color(0xFFE040FB))))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👑", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Current Status: ${currentUser.vipTier.title}",
                            color = GoldAccent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Enjoy exclusive room privileges and animated badges",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (upgradeMessage != null) {
                item {
                    Text(text = upgradeMessage!!, color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Text("VIP Tiers & Benefits:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(listOf(VipTier.VIP_BRONZE, VipTier.VIP_SILVER, VipTier.VIP_GOLD, VipTier.VIP_PLATINUM, VipTier.VIP_DIAMOND)) { tier ->
                val isCurrent = currentUser.vipTier == tier
                val cost = when (tier) {
                    VipTier.VIP_BRONZE -> 3000L
                    VipTier.VIP_SILVER -> 8000L
                    VipTier.VIP_GOLD -> 18000L
                    VipTier.VIP_PLATINUM -> 35000L
                    VipTier.VIP_DIAMOND -> 60000L
                    else -> 0L
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                VipBadge(vipTier = tier)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tier.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Priority stage entry\n• Custom avatar frame\n• 1.5x Charm exp bonus",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RoyalVioletCardAlt)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Active ✓", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    val success = onUpgrade(tier)
                                    upgradeMessage = if (success) "Successfully activated ${tier.title}!" else "Insufficient Coins to activate ${tier.title}"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("upgrade_vip_${tier.level}")
                            ) {
                                Text("🪙 %,d".format(cost), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreScreen(
    currentUser: User,
    storeItems: List<StoreItem>,
    onBack: () -> Unit,
    onPurchase: (StoreItem) -> Boolean
) {
    var purchaseNotice by remember { mutableStateOf<String?>(null) }
    var selectedItemType by remember { mutableStateOf<ItemType?>(null) }

    val filteredItems = remember(selectedItemType, storeItems) {
        if (selectedItemType == null) storeItems else storeItems.filter { it.type == selectedItemType }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "Avatar & Frame Store", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Coins: 🪙 %,d".format(currentUser.coins), color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            }

            if (purchaseNotice != null) {
                item {
                    Text(text = purchaseNotice!!, color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            }

            items(filteredItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(item.previewGradientStart), Color(item.previewGradientEnd))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.iconEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(item.description, color = TextSecondary, fontSize = 11.sp)
                                Text("${item.durationDays} Days Duration", color = NeonCyan, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val success = onPurchase(item)
                                purchaseNotice = if (success) "Purchased ${item.name}! Added to Backpack." else "Insufficient Coins!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("buy_item_${item.id}")
                        ) {
                            Text("🪙 %,d".format(item.priceCoins), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackpackScreen(
    backpackItems: List<BackpackItem>,
    onBack: () -> Unit,
    onEquip: (BackpackItem) -> Unit,
    onUnequip: (BackpackItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        BismaTopBar(title = "My Backpack", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Equip frames and badges to customize your voice profile & room presence.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (backpackItems.isEmpty()) {
                item {
                    EmptyStateNotice(
                        title = "Your Backpack is Empty",
                        subtitle = "Visit the Avatar Store to purchase exclusive frames and room entrance effects!"
                    )
                }
            } else {
                items(backpackItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(RoyalVioletCardAlt),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.iconEmoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(item.type.title, color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            if (item.isEquipped) {
                                Button(
                                    onClick = { onUnequip(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalVioletBorder),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Unequip", color = GoldAccent, fontSize = 11.sp)
                                }
                            } else {
                                Button(
                                    onClick = { onEquip(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("equip_item_${item.id}")
                                ) {
                                    Text("Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
