package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.GameCatalog
import com.example.model.GameType
import com.example.model.MatchStatus
import com.example.model.TeamFormat
import com.example.model.TournamentMatch
import com.example.state.LobbixUiState
import com.example.state.LobbixViewModel
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixAmber
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
import com.example.ui.theme.LobbixEmerald
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixMuted
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixSurfaceHigh
import com.example.ui.theme.LobbixWhite

@Composable
fun AdminOverviewTab(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    val activeMatches = uiState.matches.count { it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE }
    val totalPool = uiState.matches.filter { it.status != MatchStatus.CANCELLED }.sumOf { it.prizePool }
    var noticeTitle by remember { mutableStateOf("Arena Flash Update") }
    var noticeMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("OWNER COMMAND CENTER", style = MaterialTheme.typography.headlineMedium, color = LobbixWhite)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminKpiCard("TOTAL USERS", uiState.players.size.toString(), LobbixWhite, Modifier.weight(1f))
                    AdminKpiCard("ACTIVE MATCHES", activeMatches.toString(), LobbixCrimsonBright, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminKpiCard("TOTAL PRIZE POOL", "₹$totalPool", LobbixGold, Modifier.weight(1f))
                    AdminKpiCard("PENDING PAYOUTS", uiState.pendingWithdrawalsCount.toString(), LobbixAmber, Modifier.weight(1f))
                }
                AdminKpiCard(
                    "PENDING RESULT PROOFS",
                    uiState.pendingProofsCount.toString(),
                    LobbixEmerald,
                    Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, LobbixBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("GLOBAL APP CONTROLS", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Maintenance Mode", style = MaterialTheme.typography.titleSmall, color = LobbixWhite)
                            Text("Pause tournament joins across Player App", style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                        }
                        Switch(
                            checked = uiState.appConfig.maintenanceMode,
                            onCheckedChange = { viewModel.toggleMaintenanceMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LobbixWhite,
                                checkedTrackColor = LobbixCrimson
                            ),
                            modifier = Modifier.testTag("admin_maintenance_switch")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Withdrawals", style = MaterialTheme.typography.titleSmall, color = LobbixWhite)
                            Text("Allow players to submit UPI payout requests", style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                        }
                        Switch(
                            checked = uiState.appConfig.withdrawalsEnabled,
                            onCheckedChange = { viewModel.toggleWithdrawalsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LobbixWhite,
                                checkedTrackColor = LobbixEmerald
                            ),
                            modifier = Modifier.testTag("admin_withdrawals_switch")
                        )
                    }
                }
            }
        }

        item {
            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Campaign, contentDescription = null, tint = LobbixCrimsonBright)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PUBLISH LIVE APP BANNER", style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                    }

                    val fieldColors = adminTextFieldColors()
                    OutlinedTextField(
                        value = noticeTitle,
                        onValueChange = { noticeTitle = it },
                        label = { Text("Banner Title") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noticeMessage,
                        onValueChange = { noticeMessage = it },
                        label = { Text("Announcement Message") },
                        placeholder = { Text("e.g., Double Kill Bonus on all Free Fire lobbies tonight!") },
                        minLines = 2,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_notice_input")
                    )

                    Button(
                        onClick = {
                            viewModel.publishNotice(noticeTitle, noticeMessage)
                            noticeMessage = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_publish_notice_button")
                    ) {
                        Text("Publish Notice Live", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMatchesTab(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    var showCreateForm by remember { mutableStateOf(false) }
    var editingMatch by remember { mutableStateOf<TournamentMatch?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOURNAMENT CONTROL", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
                Button(
                    onClick = { showCreateForm = !showCreateForm },
                    colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("admin_toggle_create_match")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showCreateForm) "Hide Form" else "Create Match")
                }
            }
        }

        if (showCreateForm) {
            item {
                CreateTournamentCard(
                    onPublish = { game, mode, format, title, fee, pool, killReward, slots, dateTime, rules ->
                        viewModel.createTournament(game, mode, format, title, fee, pool, killReward, slots, dateTime, rules)
                        showCreateForm = false
                    }
                )
            }
        }

        items(uiState.matches, key = { it.id }) { match ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                StatusBadge(match.game.displayName, LobbixCrimson, LobbixWhite)
                                StatusBadge("${match.mode} • ${match.format.displayName}", LobbixSurfaceHigh, LobbixSilver)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(match.title, style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                            Text(
                                text = "Entry ₹${match.entryFee} • Pool ₹${match.prizePool} • ${match.filledSlots}/${match.maxSlots} Slots",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LobbixSilver
                            )
                        }
                        MatchStatusPill(status = match.status)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(MatchStatus.UPCOMING, MatchStatus.LIVE, MatchStatus.COMPLETED).forEach { status ->
                            FilterChip(
                                selected = match.status == status,
                                onClick = { viewModel.updateMatchStatus(match.id, status) },
                                label = { Text("Set ${status.displayName}", style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LobbixCrimsonContainer,
                                    selectedLabelColor = LobbixCrimsonBright,
                                    containerColor = LobbixSurfaceElevated,
                                    labelColor = LobbixSilver
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { editingMatch = match },
                            border = BorderStroke(1.dp, LobbixBorder),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(15.dp), tint = LobbixWhite)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", color = LobbixWhite, style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { viewModel.cancelAndRefundMatch(match.id) },
                            border = BorderStroke(1.dp, LobbixAmber.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(15.dp), tint = LobbixAmber)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel & Refund", color = LobbixAmber, style = MaterialTheme.typography.labelMedium)
                        }
                        IconButton(
                            onClick = { viewModel.deleteMatch(match.id) },
                            modifier = Modifier.testTag("delete_match_${match.id}")
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Match", tint = LobbixCrimsonBright)
                        }
                    }
                }
            }
        }
    }

    editingMatch?.let { match ->
        EditMatchDialog(
            match = match,
            onDismiss = { editingMatch = null },
            onSave = { title, fee, pool, killReward, slots, dateTime ->
                viewModel.editTournament(match.id, title, fee, pool, killReward, slots, dateTime)
                editingMatch = null
            }
        )
    }
}

@Composable
private fun CreateTournamentCard(
    onPublish: (GameType, String, TeamFormat, String, Int, Int, Int, Int, String, String) -> Unit
) {
    var game by remember { mutableStateOf(GameType.FREE_FIRE) }
    var mode by remember { mutableStateOf("Battle Royale") }
    var format by remember { mutableStateOf(TeamFormat.SQUAD) }
    var title by remember { mutableStateOf("") }
    var entryFee by remember { mutableStateOf("50") }
    var prizePool by remember { mutableStateOf("2500") }
    var perKill by remember { mutableStateOf("20") }
    var maxSlots by remember { mutableStateOf("48") }
    var dateTime by remember { mutableStateOf("Tonight, 9:30 PM IST") }
    var rules by remember { mutableStateOf("Bermuda • Fair Play • Screenshot Required") }

    val availableModes = GameCatalog.modesFor(game).filter { it != "All Modes" }
    val fieldColors = adminTextFieldColors()

    Surface(
        color = LobbixSurfaceElevated,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, LobbixCrimson),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("CREATE NEW TOURNAMENT", style = MaterialTheme.typography.titleMedium, color = LobbixCrimsonBright)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(GameType.FREE_FIRE, GameType.LUDO, GameType.CARROM).forEach { g ->
                    FilterChip(
                        selected = game == g,
                        onClick = {
                            game = g
                            mode = GameCatalog.modesFor(g).firstOrNull { it != "All Modes" } ?: "Classic"
                        },
                        label = { Text(g.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LobbixCrimson,
                            selectedLabelColor = LobbixWhite
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableModes.forEach { m ->
                    FilterChip(
                        selected = mode == m,
                        onClick = { mode = m },
                        label = { Text(m) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LobbixCrimsonContainer,
                            selectedLabelColor = LobbixWhite
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TeamFormat.entries.forEach { fmt ->
                    FilterChip(
                        selected = format == fmt,
                        onClick = { format = fmt },
                        label = { Text(fmt.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LobbixCrimsonContainer,
                            selectedLabelColor = LobbixWhite
                        )
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tournament Title") },
                placeholder = { Text("e.g., Crimson Inferno #501") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_match_title_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = entryFee,
                    onValueChange = { entryFee = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Entry (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = prizePool,
                    onValueChange = { prizePool = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Prize Pool (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = perKill,
                    onValueChange = { perKill = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Per Kill (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = maxSlots,
                    onValueChange = { maxSlots = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Max Slots") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = dateTime,
                onValueChange = { dateTime = it },
                label = { Text("Date & Time") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rules,
                onValueChange = { rules = it },
                label = { Text("Map / Rules") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onPublish(
                        game,
                        mode,
                        format,
                        title.ifBlank { "${game.displayName} ${mode} Cup" },
                        entryFee.toIntOrNull() ?: 50,
                        prizePool.toIntOrNull() ?: 1500,
                        perKill.toIntOrNull() ?: 0,
                        maxSlots.toIntOrNull() ?: 32,
                        dateTime,
                        rules
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_publish_match_button")
            ) {
                Text("Publish Match", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun EditMatchDialog(
    match: TournamentMatch,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int, String) -> Unit
) {
    var title by remember { mutableStateOf(match.title) }
    var fee by remember { mutableStateOf(match.entryFee.toString()) }
    var pool by remember { mutableStateOf(match.prizePool.toString()) }
    var kill by remember { mutableStateOf(match.perKillReward.toString()) }
    var slots by remember { mutableStateOf(match.maxSlots.toString()) }
    var dateTime by remember { mutableStateOf(match.dateTime) }
    val fieldColors = adminTextFieldColors()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LobbixSurface,
        titleContentColor = LobbixWhite,
        title = { Text("Edit Match", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, colors = fieldColors)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fee, onValueChange = { fee = it.filter(Char::isDigit) }, label = { Text("Entry ₹") }, colors = fieldColors, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = pool, onValueChange = { pool = it.filter(Char::isDigit) }, label = { Text("Pool ₹") }, colors = fieldColors, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = kill, onValueChange = { kill = it.filter(Char::isDigit) }, label = { Text("Per Kill ₹") }, colors = fieldColors, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = slots, onValueChange = { slots = it.filter(Char::isDigit) }, label = { Text("Max Slots") }, colors = fieldColors, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = dateTime, onValueChange = { dateTime = it }, label = { Text("Schedule") }, colors = fieldColors)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        title,
                        fee.toIntOrNull() ?: match.entryFee,
                        pool.toIntOrNull() ?: match.prizePool,
                        kill.toIntOrNull() ?: match.perKillReward,
                        slots.toIntOrNull() ?: match.maxSlots,
                        dateTime
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = LobbixSilver) }
        }
    )
}

@Composable
private fun AdminKpiCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LobbixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LobbixBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = LobbixMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = JetBrainsMonoFamily,
                color = accent
            )
        }
    }
}

@Composable
fun adminTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LobbixCrimson,
    unfocusedBorderColor = LobbixBorder,
    focusedTextColor = LobbixWhite,
    unfocusedTextColor = LobbixWhite,
    focusedLabelColor = LobbixCrimsonBright,
    unfocusedLabelColor = LobbixSilver
)
