package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gender
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(1.0f, animationSpec = tween(300))
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(RoyalVioletDark, Color(0xFF1D0B3D), RoyalVioletDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            // Glowing Microphone Crest
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFE040FB), Color(0xFF7C4DFF), RoyalVioletCard)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Bisma Live",
                    tint = GoldAccent,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "BISMA LIVE",
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = 3.sp
            )

            Text(
                text = "Live Voice Chat & Social Lounge",
                color = GoldAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = NeonMagenta,
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.5.dp
            )
        }

        Text(
            text = "Premium Voice Social Community",
            color = TextMuted,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onLogin: (String, String) -> Boolean,
    onRegister: (String, String, Gender) -> Boolean
) {
    var isRegisterTab by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isForgotPasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(RoyalVioletDark, RoyalVioletSurface, RoyalVioletDark)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(NeonMagenta, RoyalVioletCardAlt))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BISMA LIVE",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 2.sp
            )

            Text(
                text = if (isRegisterTab) "Create your free voice identity" else "Sign in to your voice room",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RoyalVioletCard)
                    .padding(4.dp)
            ) {
                TabButton(
                    title = "Login",
                    selected = !isRegisterTab,
                    onClick = {
                        isRegisterTab = false
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    title = "Register",
                    selected = isRegisterTab,
                    onClick = {
                        isRegisterTab = true
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form inputs
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_username_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = RoyalVioletBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            if (isRegisterTab) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_display_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMagenta,
                        unfocusedBorderColor = RoyalVioletBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gender:", color = TextSecondary, fontSize = 14.sp)
                    Row {
                        FilterChip(
                            selected = selectedGender == Gender.MALE,
                            onClick = { selectedGender = Gender.MALE },
                            label = { Text("♂ Male") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = selectedGender == Gender.FEMALE,
                            onClick = { selectedGender = Gender.FEMALE },
                            label = { Text("♀ Female") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = RoyalVioletBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            if (!isRegisterTab) {
                TextButton(
                    onClick = { isForgotPasswordDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = NeonPurpleLight, fontSize = 12.sp)
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MutedRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LuxuryButton(
                text = if (isRegisterTab) "Create Account" else "Sign In",
                onClick = {
                    if (username.isBlank()) {
                        errorMessage = "Please enter username"
                        return@LuxuryButton
                    }
                    if (isRegisterTab) {
                        val name = displayName.ifBlank { username }
                        onRegister(username, name, selectedGender)
                        onLoginSuccess()
                    } else {
                        onLogin(username, password)
                        onLoginSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Demo Guest Login
            OutlinedButton(
                onClick = {
                    onLogin("guest_star", "pass")
                    onLoginSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("demo_quick_login_btn"),
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GoldAccent, NeonPurpleLight)))
            ) {
                Text("🌟 Quick Demo Mode (Shahroz Live)", color = GoldAccent, fontWeight = FontWeight.Bold)
            }
        }

        if (isForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { isForgotPasswordDialog = false },
                title = { Text("Reset Password") },
                text = { Text("A verification code has been sent to your registered account phone/email.") },
                confirmButton = {
                    TextButton(onClick = { isForgotPasswordDialog = false }) {
                        Text("OK", color = NeonMagenta)
                    }
                },
                containerColor = RoyalVioletCard
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) NeonPurpleLight else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.Black else TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
