package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.TransactionType
import com.example.model.WalletFilter
import com.example.model.WalletTransaction
import com.example.state.LobbixUiState
import com.example.ui.components.LobbixEmptyState
import com.example.ui.components.UpiQrMockCard
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
fun WalletScreen(
    uiState: LobbixUiState,
    onSelectFilter: (WalletFilter) -> Unit,
    onDeposit: (Int, String) -> Unit,
    onWithdraw: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDepositModal by remember { mutableStateOf(false) }
    var showWithdrawModal by remember { mutableStateOf(false) }
    val player = uiState.currentPlayer

    val filteredTransactions = remember(uiState.transactions, player.id, uiState.walletFilter) {
        uiState.transactions
            .filter { it.playerId == player.id }
            .filter { tx ->
                when (uiState.walletFilter) {
                    WalletFilter.ALL -> true
                    WalletFilter.DEPOSITS -> tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.REFUND || tx.type == TransactionType.ADMIN_ADJUST
                    WalletFilter.WINNINGS -> tx.type == TransactionType.WINNING
                    WalletFilter.WITHDRAWALS -> tx.type == TransactionType.WITHDRAWAL
                }
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(LobbixCrimsonContainer.copy(alpha = 0.7f), LobbixSurface)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = "TOTAL WALLET BALANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = LobbixSilver
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${player.totalBalance}",
                        style = MaterialTheme.typography.displayLarge,
                        fontFamily = JetBrainsMonoFamily,
                        color = LobbixWhite,
                        modifier = Modifier.testTag("wallet_total_balance")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BalanceSplitCard(
                            label = "WINNING CASH",
                            amount = "₹${player.winningBalance}",
                            accent = LobbixGold,
                            modifier = Modifier.weight(1f)
                        )
                        BalanceSplitCard(
                            label = "DEPOSIT BALANCE",
                            amount = "₹${player.depositBalance}",
                            accent = LobbixEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showDepositModal = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LobbixCrimson,
                                contentColor = LobbixWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("open_deposit_modal_button")
                        ) {
                            Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Money", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = { showWithdrawModal = true },
                            border = BorderStroke(1.dp, LobbixGold.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("open_withdraw_modal_button")
                        ) {
                            Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = LobbixGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw", style = MaterialTheme.typography.labelLarge, color = LobbixGold)
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TRANSACTION HISTORY",
                    style = MaterialTheme.typography.titleLarge,
                    color = LobbixWhite
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WalletFilter.entries.forEach { filter ->
                        val selected = uiState.walletFilter == filter
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectFilter(filter) },
                            label = { Text(filter.displayName, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LobbixCrimson,
                                selectedLabelColor = LobbixWhite,
                                containerColor = LobbixSurface,
                                labelColor = LobbixSilver
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = LobbixBorder,
                                selectedBorderColor = LobbixCrimsonBright
                            ),
                            modifier = Modifier.testTag("wallet_filter_${filter.name.lowercase()}")
                        )
                    }
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
            item {
                LobbixEmptyState(
                    title = "No Transactions Here",
                    subtitle = "Deposit funds, join a tournament, or win prizes to see activity."
                )
            }
        } else {
            items(filteredTransactions, key = { it.id }) { tx ->
                TransactionRow(tx = tx)
            }
        }
    }

    if (showDepositModal) {
        DepositModal(
            onDismiss = { showDepositModal = false },
            onConfirm = { amount, method ->
                onDeposit(amount, method)
                showDepositModal = false
            }
        )
    }

    if (showWithdrawModal) {
        WithdrawModal(
            availableBalance = player.totalBalance,
            withdrawalsEnabled = uiState.appConfig.withdrawalsEnabled,
            onDismiss = { showWithdrawModal = false },
            onConfirm = { amount, upi ->
                onWithdraw(amount, upi)
                showWithdrawModal = false
            }
        )
    }
}

@Composable
private fun BalanceSplitCard(
    label: String,
    amount: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LobbixSurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, LobbixBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = LobbixMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = JetBrainsMonoFamily,
                color = accent
            )
        }
    }
}

@Composable
private fun TransactionRow(tx: WalletTransaction) {
    Surface(
        color = LobbixSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, LobbixBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (tx.isCredit) LobbixEmeraldContainer else LobbixCrimsonContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.isCredit) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                        contentDescription = null,
                        tint = if (tx.isCredit) LobbixEmerald else LobbixCrimsonBright,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tx.description,
                        style = MaterialTheme.typography.titleSmall,
                        color = LobbixWhite
                    )
                    Text(
                        text = "${tx.type.displayName} • ${tx.timestamp}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LobbixMuted
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (tx.isCredit) "+" else "-"}₹${tx.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = JetBrainsMonoFamily,
                    color = if (tx.isCredit) LobbixEmerald else LobbixCrimsonBright
                )
                Text(
                    text = tx.statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = LobbixSilver
                )
            }
        }
    }
}

@Composable
private fun DepositModal(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    val quickAmounts = listOf(50, 100, 200, 500)
    var amountText by remember { mutableStateOf("100") }
    val parsedAmount = amountText.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LobbixSurface,
        titleContentColor = LobbixWhite,
        textContentColor = LobbixSilver,
        title = { Text("Add Money via UPI / QR", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.forEach { preset ->
                        FilterChip(
                            selected = parsedAmount == preset,
                            onClick = { amountText = preset.toString() },
                            label = { Text("₹$preset") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LobbixCrimson,
                                selectedLabelColor = LobbixWhite,
                                containerColor = LobbixSurfaceElevated,
                                labelColor = LobbixSilver
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                    label = { Text("Custom Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LobbixCrimson,
                        unfocusedBorderColor = LobbixBorder,
                        focusedTextColor = LobbixWhite,
                        unfocusedTextColor = LobbixWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_amount_input")
                )

                UpiQrMockCard(amount = parsedAmount)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(parsedAmount, "UPI Instant QR") },
                enabled = parsedAmount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                modifier = Modifier.testTag("confirm_deposit_button")
            ) {
                Text("Confirm Deposit ₹$parsedAmount")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LobbixSilver)
            }
        }
    )
}

@Composable
private fun WithdrawModal(
    availableBalance: Int,
    withdrawalsEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var amountText by remember { mutableStateOf("200") }
    var upiId by remember { mutableStateOf("viperx@okicici") }
    val parsedAmount = amountText.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LobbixSurface,
        titleContentColor = LobbixWhite,
        textContentColor = LobbixSilver,
        title = { Text("Withdraw to UPI", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Available Balance: ₹$availableBalance • Min ₹50",
                    style = MaterialTheme.typography.labelLarge,
                    color = LobbixGold
                )
                if (!withdrawalsEnabled) {
                    Text(
                        text = "Note: Admin has temporarily paused withdrawals.",
                        color = LobbixCrimsonBright,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LobbixCrimson,
                        unfocusedBorderColor = LobbixBorder,
                        focusedTextColor = LobbixWhite,
                        unfocusedTextColor = LobbixWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_input")
                )
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Your UPI ID") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LobbixCrimson,
                        unfocusedBorderColor = LobbixBorder,
                        focusedTextColor = LobbixWhite,
                        unfocusedTextColor = LobbixWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_upi_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(parsedAmount, upiId) },
                enabled = parsedAmount >= 50 && upiId.contains("@") && withdrawalsEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                modifier = Modifier.testTag("submit_withdrawal_button")
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LobbixSilver)
            }
        }
    )
}
