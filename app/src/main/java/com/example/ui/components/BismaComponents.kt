package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.VipTier
import com.example.ui.theme.*

@Composable
fun UserAvatarWithFrame(
    avatarUrl: String?,
    displayName: String,
    frameId: String? = null,
    vipTier: VipTier = VipTier.NONE,
    size: Dp = 48.dp,
    isSpeaking: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val frameBorderBrush = when (frameId) {
        "frame_gold_crown" -> Brush.sweepGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFFFFF176), Color(0xFFFFD700)))
        "frame_cyber_neon" -> Brush.sweepGradient(listOf(NeonCyan, NeonMagenta, NeonPurple, NeonCyan))
        "frame_rose_romance" -> Brush.sweepGradient(listOf(Color(0xFFFF4081), Color(0xFFF48FB1), Color(0xFFC2185B), Color(0xFFFF4081)))
        "frame_vip_flame" -> Brush.sweepGradient(listOf(Color(0xFFFF3D00), Color(0xFFFF9100), Color(0xFFFFD700), Color(0xFFFF3D00)))
        else -> null
    }

    Box(
        modifier = Modifier
            .size(size)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Speaking ring glow
        if (isSpeaking) {
            Box(
                modifier = Modifier
                    .size(size + 8.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, SpeakingGlow.copy(alpha = glowAlpha), CircleShape)
            )
        }

        // Avatar Core
        Box(
            modifier = Modifier
                .size(size - 4.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(RoyalVioletCardAlt, RoyalVioletDark)
                    )
                )
                .then(
                    if (frameBorderBrush != null) {
                        Modifier.border(2.5.dp, frameBorderBrush, CircleShape)
                    } else {
                        Modifier.border(1.dp, RoyalVioletBorder, CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = displayName.take(1).uppercase(),
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.42f).sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Frame Top Badge Icon if equipped
        if (frameId == "frame_gold_crown") {
            Text(
                text = "👑",
                fontSize = (size.value * 0.32f).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-6).dp)
            )
        } else if (frameId == "frame_vip_flame") {
            Text(
                text = "🔥",
                fontSize = (size.value * 0.30f).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-6).dp)
            )
        }

        // VIP Tier bottom badge
        if (vipTier != VipTier.NONE) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(vipTier.badgeColor))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "V${vipTier.level}",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun VipBadge(vipTier: VipTier, modifier: Modifier = Modifier) {
    if (vipTier == VipTier.NONE) return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(vipTier.badgeColor), Color(vipTier.badgeColor).copy(alpha = 0.7f))
                )
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "👑", fontSize = 10.sp)
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = vipTier.title,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
fun LevelBadge(type: String, level: Int, modifier: Modifier = Modifier) {
    val (color, prefix) = when (type) {
        "RICH" -> Pair(Color(0xFFFFB300), "💰 Rich Lv.")
        "CHARM" -> Pair(Color(0xFFFF4081), "💖 Charm Lv.")
        else -> Pair(NeonPurpleLight, "⭐ Lv.")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$prefix$level",
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )
    }
}

@Composable
fun CoinChip(
    coins: Long,
    onClickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RoyalVioletCard)
            .border(1.dp, RoyalVioletBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClickAdd)
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🪙", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "%,d".format(coins),
            color = GoldAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(GoldAccent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Recharge Coins",
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun LuxuryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    isSecondary: Boolean = false
) {
    val brush = if (isSecondary) {
        Brush.horizontalGradient(listOf(RoyalVioletCardAlt, RoyalVioletCard))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFE040FB), Color(0xFF7C4DFF)))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .testTag("luxury_btn_${text.lowercase().replace(" ", "_")}"),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = RoyalVioletCard.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun BismaTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("top_bar_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (actions != null) {
            actions()
        }
    }
}
