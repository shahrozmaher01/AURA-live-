package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
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
import com.example.model.RoomCategory
import com.example.model.VoiceRoom
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*

@Composable
fun RoomsScreen(
    rooms: List<VoiceRoom>,
    onOpenRoom: (VoiceRoom) -> Unit,
    onCreateRoom: (String, RoomCategory, Int, String) -> Unit
) {
    var selectedSeatFilter by remember { mutableStateOf<Int?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredRooms = remember(selectedSeatFilter, rooms) {
        if (selectedSeatFilter == null) rooms else rooms.filter { it.seatCount == selectedSeatFilter }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalVioletDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "🎙️ Live Voice Hub",
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Seat Layout Filter Chips (8, 10, 15, 20 seats)
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSeatFilter == null,
                            onClick = { selectedSeatFilter = null },
                            label = { Text("All Stages") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonPurple,
                                selectedLabelColor = Color.White,
                                containerColor = RoyalVioletCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                    items(listOf(8, 10, 15, 20)) { seats ->
                        FilterChip(
                            selected = selectedSeatFilter == seats,
                            onClick = { selectedSeatFilter = seats },
                            label = { Text("$seats Seats") },
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

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filteredRooms.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active rooms in this category.", color = TextMuted)
                    }
                }
            } else {
                items(filteredRooms) { room ->
                    LiveRoomCard(
                        room = room,
                        onClick = { onOpenRoom(room) }
                    )
                }
            }
        }

        // Floating Action Button to Create Room
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = NeonMagenta,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .testTag("create_room_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Room")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Room", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // Create Room Dialog with mandatory Seat Layout preview (8, 10, 15, 20)
        if (showCreateDialog) {
            CreateRoomDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { title, category, seats, notice ->
                    showCreateDialog = false
                    onCreateRoom(title, category, seats, notice)
                }
            )
        }
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, RoomCategory, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notice by remember { mutableStateOf("Welcome to our voice room! Sing, chat and enjoy! 🎙️") }
    var selectedCategory by remember { mutableStateOf(RoomCategory.CHAT) }
    var selectedSeats by remember { mutableStateOf(10) } // Options: 8, 10, 15, 20

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Live Voice Room",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Room Title") },
                    placeholder = { Text("e.g. Acoustic Chill & Night Talks") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_room_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMagenta,
                        unfocusedBorderColor = RoyalVioletBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Category:", color = TextSecondary, fontSize = 12.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(RoomCategory.entries.toTypedArray()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Select Seat Layout (Required):", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(8, 10, 15, 20).forEach { count ->
                        val isSelected = selectedSeats == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonPurple else RoyalVioletCardAlt)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldAccent else RoyalVioletBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSeats = count }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("seat_option_$count"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count Seats",
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Seat Layout Preview Graphic
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RoyalVioletDark),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoyalVioletBorder, RoyalVioletCard)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Stage Layout Preview ($selectedSeats Seats)",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Render mini circles representing seats
                        val rows = when (selectedSeats) {
                            8 -> listOf(4, 4)
                            10 -> listOf(5, 5)
                            15 -> listOf(5, 5, 5)
                            20 -> listOf(5, 5, 5, 5)
                            else -> listOf(5, 5)
                        }

                        rows.forEachIndexed { rowIndex, countInRow ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                for (c in 1..countInRow) {
                                    val seatNum = rowIndex * 5 + c
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (seatNum == 1) GoldAccent else RoyalVioletCardAlt)
                                            .border(1.dp, RoyalVioletBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (seatNum == 1) "👑" else "$seatNum",
                                            color = if (seatNum == 1) Color.Black else TextPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            LuxuryButton(
                text = "Confirm & Launch",
                onClick = {
                    val finalTitle = title.ifBlank { "Voice Lounge & Music" }
                    onConfirm(finalTitle, selectedCategory, selectedSeats, notice)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = RoyalVioletCard
    )
}
