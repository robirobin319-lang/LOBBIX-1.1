package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.PlayerAccount
import com.example.model.WithdrawalStatus
import com.example.state.LobbixUiState
import com.example.state.LobbixViewModel
import com.example.ui.components.LobbixEmptyState
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixAmber
import com.example.ui.theme.LobbixAmberContainer
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
import com.example.ui.theme.LobbixEmerald
import com.example.ui.theme.LobbixEmeraldContainer
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixWhite

@Composable
fun AdminUsersTab(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var adjustTargetPlayer by remember { mutableStateOf<PlayerAccount?>(null) }
    var historyTargetPlayer by remember { mutableStateOf<PlayerAccount?>(null) }

    val filteredPlayers = remember(uiState.players, searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) uiState.players
        else uiState.players.filter {
            it.inGameName.contains(q, ignoreCase = true) ||
                it.freeFireUid.contains(q, ignoreCase = true) ||
                it.emailOrMobile.contains(q, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("USER & WALLET CONTROL", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Name, UID, or Email") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = LobbixSilver) },
                singleLine = true,
                colors = adminTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_user_search_input")
            )
        }

        items(filteredPlayers, key = { it.id }) { player ->
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (player.isBanned) LobbixCrimson else LobbixBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(player.inGameName, style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                            Text("FF UID: ${player.freeFireUid} • ${player.emailOrMobile}", style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                            Text(
                                text = "Balance: ₹${player.totalBalance} (Win ₹${player.winningBalance})",
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = JetBrainsMonoFamily,
                                color = LobbixGold
                            )
                        }
                        StatusBadge(
                            text = if (player.isBanned) "BANNED" else "ACTIVE",
                            containerColor = if (player.isBanned) LobbixCrimsonContainer else LobbixEmeraldContainer,
                            contentColor = if (player.isBanned) LobbixCrimsonBright else LobbixEmerald
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { adjustTargetPlayer = player },
                            colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("admin_adjust_wallet_${player.id}")
                        ) {
                            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+/- Coins", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = { viewModel.toggleBanPlayer(player.id) },
                            border = BorderStroke(1.dp, if (player.isBanned) LobbixEmerald else LobbixCrimsonBright),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_ban_toggle_${player.id}")
                        ) {
                            Icon(
                                Icons.Filled.Block,
                                contentDescription = null,
                                tint = if (player.isBanned) LobbixEmerald else LobbixCrimsonBright,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (player.isBanned) "Unban" else "Ban",
                                color = if (player.isBanned) LobbixEmerald else LobbixCrimsonBright,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        OutlinedButton(
                            onClick = { historyTargetPlayer = player },
                            border = BorderStroke(1.dp, LobbixBorder),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_user_history_${player.id}")
                        ) {
                            Icon(Icons.Filled.History, contentDescription = null, tint = LobbixWhite, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("History", color = LobbixWhite, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }

    adjustTargetPlayer?.let { player ->
        var amountText by remember { mutableStateOf("100") }
        var reason by remember { mutableStateOf("Tournament Bonus") }
        AlertDialog(
            onDismissRequest = { adjustTargetPlayer = null },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            title = { Text("Adjust ${player.inGameName}'s Wallet", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Current Balance: ₹${player.totalBalance}", color = LobbixGold)
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter(Char::isDigit) },
                        label = { Text("Coin Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = adminTextFieldColors()
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason") },
                        colors = adminTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.adjustPlayerWallet(player.id, amountText.toIntOrNull() ?: 0, isCredit = false, reason)
                            adjustTargetPlayer = null
                        },
                        border = BorderStroke(1.dp, LobbixCrimsonBright)
                    ) {
                        Text("- Deduct", color = LobbixCrimsonBright)
                    }
                    Button(
                        onClick = {
                            viewModel.adjustPlayerWallet(player.id, amountText.toIntOrNull() ?: 0, isCredit = true, reason)
                            adjustTargetPlayer = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LobbixEmerald)
                    ) {
                        Text("+ Add Coins")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { adjustTargetPlayer = null }) {
                    Text("Cancel", color = LobbixSilver)
                }
            }
        )
    }

    historyTargetPlayer?.let { player ->
        val userTxs = uiState.transactions.filter { it.playerId == player.id }
        AlertDialog(
            onDismissRequest = { historyTargetPlayer = null },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            title = { Text("${player.inGameName} — History", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Matches: ${player.matchesPlayed} • Wins: ${player.wins} • Kills: ${player.totalKills}", color = LobbixGold)
                    HorizontalDivider(color = LobbixBorder)
                    if (userTxs.isEmpty()) {
                        Text("No transactions recorded yet.", color = LobbixSilver)
                    } else {
                        userTxs.take(6).forEach { tx ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(tx.description, style = MaterialTheme.typography.bodyMedium, color = LobbixWhite, modifier = Modifier.weight(1f))
                                Text(
                                    text = "${if (tx.isCredit) "+" else "-"}₹${tx.amount}",
                                    color = if (tx.isCredit) LobbixEmerald else LobbixCrimsonBright,
                                    fontFamily = JetBrainsMonoFamily
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { historyTargetPlayer = null }) {
                    Text("Close", color = LobbixWhite)
                }
            }
        )
    }
}

@Composable
fun AdminQueueAndSupportTab(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    var replyTicketId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("WITHDRAWALS QUEUE", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
        }

        if (uiState.withdrawalRequests.isEmpty()) {
            item {
                LobbixEmptyState(title = "No Withdrawal Requests", subtitle = "Player UPI payout requests appear here.")
            }
        } else {
            items(uiState.withdrawalRequests, key = { it.id }) { req ->
                Surface(
                    color = LobbixSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, LobbixBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${req.playerName} • ₹${req.amount}", style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                                Text("UPI: ${req.upiId} • ${req.requestedAt}", style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                            }
                            val (bg, fg) = when (req.status) {
                                WithdrawalStatus.PENDING -> LobbixAmberContainer to LobbixAmber
                                WithdrawalStatus.APPROVED -> LobbixEmeraldContainer to LobbixEmerald
                                WithdrawalStatus.REJECTED -> LobbixCrimsonContainer to LobbixCrimsonBright
                            }
                            StatusBadge(req.status.displayName.uppercase(), bg, fg)
                        }

                        if (req.status == WithdrawalStatus.PENDING) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.approveWithdrawal(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LobbixEmerald),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("approve_withdrawal_${req.id}")
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve (Paid)", style = MaterialTheme.typography.labelMedium)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.rejectWithdrawal(req.id) },
                                    border = BorderStroke(1.dp, LobbixCrimsonBright),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("reject_withdrawal_${req.id}")
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, tint = LobbixCrimsonBright, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reject & Refund", color = LobbixCrimsonBright, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("SUPPORT TICKETS INBOX", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
        }

        items(uiState.supportTickets, key = { it.id }) { ticket ->
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LobbixBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${ticket.playerName}: ${ticket.subject}", style = MaterialTheme.typography.titleSmall, color = LobbixWhite, modifier = Modifier.weight(1f))
                        StatusBadge(
                            text = if (ticket.isResolved) "RESOLVED" else "OPEN",
                            containerColor = if (ticket.isResolved) LobbixEmeraldContainer else LobbixCrimsonContainer,
                            contentColor = if (ticket.isResolved) LobbixEmerald else LobbixCrimsonBright
                        )
                    }
                    Text(ticket.message, style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                    if (ticket.adminReply != null) {
                        Text("Reply: ${ticket.adminReply}", style = MaterialTheme.typography.bodyMedium, color = LobbixGold)
                    }
                    if (!ticket.isResolved) {
                        Button(
                            onClick = { replyTicketId = ticket.id },
                            colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reply_resolve_ticket_${ticket.id}")
                        ) {
                            Text("Reply & Resolve", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }

    replyTicketId?.let { ticketId ->
        var replyText by remember { mutableStateOf("Verified! Your account & rewards have been updated.") }
        AlertDialog(
            onDismissRequest = { replyTicketId = null },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            title = { Text("Reply & Resolve Ticket", style = MaterialTheme.typography.titleLarge) },
            text = {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Admin Reply") },
                    minLines = 3,
                    colors = adminTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_ticket_reply_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.replyAndResolveTicket(ticketId, replyText)
                        replyTicketId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                    modifier = Modifier.testTag("confirm_resolve_ticket_button")
                ) {
                    Text("Send & Resolve")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyTicketId = null }) {
                    Text("Cancel", color = LobbixSilver)
                }
            }
        )
    }
}
