package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.state.LobbixUiState
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
import com.example.ui.theme.LobbixEmerald
import com.example.ui.theme.LobbixEmeraldContainer
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixMuted
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixWhite

@Composable
fun ProfileScreen(
    uiState: LobbixUiState,
    onSaveGameIds: (String, String, String) -> Unit,
    onRaiseTicket: (String, String) -> Unit,
    onCopyFeedback: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val player = uiState.currentPlayer
    var ffUid by remember(player.id, player.freeFireUid) { mutableStateOf(player.freeFireUid) }
    var ludoId by remember(player.id, player.ludoId) { mutableStateOf(player.ludoId) }
    var carromId by remember(player.id, player.carromId) { mutableStateOf(player.carromId) }
    var showTicketModal by remember { mutableStateOf(false) }

    val clipboard = LocalClipboardManager.current
    val playerTickets = remember(uiState.supportTickets, player.id) {
        uiState.supportTickets.filter { it.playerId == player.id }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LobbixCrimson,
        unfocusedBorderColor = LobbixBorder,
        focusedTextColor = LobbixWhite,
        unfocusedTextColor = LobbixWhite,
        focusedLabelColor = LobbixCrimsonBright,
        unfocusedLabelColor = LobbixSilver
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LobbixBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(LobbixCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.inGameName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = LobbixWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = player.inGameName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = LobbixWhite
                            )
                            Text(
                                text = player.emailOrMobile,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LobbixSilver
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileStatTile("MATCHES", player.matchesPlayed.toString(), LobbixWhite, Modifier.weight(1f))
                        ProfileStatTile("WINS", player.wins.toString(), LobbixCrimsonBright, Modifier.weight(1f))
                        ProfileStatTile("EARNINGS", "₹${player.totalEarnings}", LobbixGold, Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, LobbixBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("EDIT IN-GAME IDS", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)

                    OutlinedTextField(
                        value = ffUid,
                        onValueChange = { ffUid = it },
                        label = { Text("Free Fire UID") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_ff_uid")
                    )
                    OutlinedTextField(
                        value = ludoId,
                        onValueChange = { ludoId = it },
                        label = { Text("Ludo King ID") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_ludo_id")
                    )
                    OutlinedTextField(
                        value = carromId,
                        onValueChange = { carromId = it },
                        label = { Text("Carrom Pool ID") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_carrom_id")
                    )

                    Button(
                        onClick = { onSaveGameIds(ffUid, ludoId, carromId) },
                        colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_game_ids_button")
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Game IDs", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        item {
            Surface(
                color = LobbixCrimsonContainer.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = LobbixGold, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("REFER & EARN ₹50", style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                            Text(
                                text = "Code: ${player.referralCode}",
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = JetBrainsMonoFamily,
                                color = LobbixGold
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(player.referralCode))
                            onCopyFeedback("Referral code ${player.referralCode} copied!")
                        },
                        modifier = Modifier.testTag("copy_referral_button")
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Referral Code", tint = LobbixWhite)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showTicketModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = LobbixSurfaceElevated),
                    border = BorderStroke(1.dp, LobbixBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("raise_ticket_button")
                ) {
                    Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = LobbixCrimsonBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Raise Support Ticket", color = LobbixWhite, style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onLogout,
                    border = BorderStroke(1.dp, LobbixCrimson),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("logout_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = LobbixCrimsonBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Logout", color = LobbixCrimsonBright, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (playerTickets.isNotEmpty()) {
            item {
                Text("YOUR SUPPORT TICKETS", style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
            }
            items(playerTickets, key = { it.id }) { ticket ->
                Surface(
                    color = LobbixSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LobbixBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ticket.subject, style = MaterialTheme.typography.titleSmall, color = LobbixWhite)
                            StatusBadge(
                                text = if (ticket.isResolved) "RESOLVED" else "OPEN",
                                containerColor = if (ticket.isResolved) LobbixEmeraldContainer else LobbixCrimsonContainer,
                                contentColor = if (ticket.isResolved) LobbixEmerald else LobbixCrimsonBright
                            )
                        }
                        Text(ticket.message, style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                        if (ticket.adminReply != null) {
                            Surface(
                                color = LobbixSurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Admin Reply: ${ticket.adminReply}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LobbixGold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTicketModal) {
        var subject by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showTicketModal = false },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            textContentColor = LobbixSilver,
            title = { Text("Raise Support Ticket", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ticket_subject_input")
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Describe your issue") },
                        minLines = 3,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ticket_message_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRaiseTicket(subject, message)
                        showTicketModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                    modifier = Modifier.testTag("submit_ticket_button")
                ) {
                    Text("Send Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTicketModal = false }) {
                    Text("Cancel", color = LobbixSilver)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatTile(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LobbixSurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LobbixBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontFamily = JetBrainsMonoFamily, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = LobbixMuted)
        }
    }
}
