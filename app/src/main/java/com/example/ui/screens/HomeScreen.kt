package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.model.GameCatalog
import com.example.model.GameType
import com.example.model.MatchStatus
import com.example.model.TeamFormat
import com.example.model.TournamentMatch
import com.example.state.LobbixUiState
import com.example.ui.components.LobbixEmptyState
import com.example.ui.components.TournamentCard
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixAmber
import com.example.ui.theme.LobbixAmberContainer
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixWhite

@Composable
fun HomeScreen(
    uiState: LobbixUiState,
    onSelectGame: (GameType) -> Unit,
    onSelectMode: (String) -> Unit,
    onSelectFormat: (TeamFormat?) -> Unit,
    onJoinMatch: (String) -> Unit,
    onCopyFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingJoinMatch by remember { mutableStateOf<TournamentMatch?>(null) }
    var detailsMatch by remember { mutableStateOf<TournamentMatch?>(null) }

    val filteredMatches = remember(
        uiState.matches,
        uiState.selectedGameFilter,
        uiState.selectedSubModeFilter,
        uiState.selectedFormatFilter
    ) {
        uiState.matches.filter { match ->
            val gameOk = uiState.selectedGameFilter == GameType.ALL || match.game == uiState.selectedGameFilter
            val modeOk = uiState.selectedSubModeFilter == "All Modes" || match.mode.equals(uiState.selectedSubModeFilter, ignoreCase = true)
            val formatOk = uiState.selectedFormatFilter == null || match.format == uiState.selectedFormatFilter
            gameOk && modeOk && formatOk && match.status != MatchStatus.CANCELLED
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (uiState.appConfig.maintenanceMode) {
            item {
                Surface(
                    color = LobbixAmberContainer,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LobbixAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = LobbixAmber)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Arena Maintenance Mode Active — Match joining is paused by Owner Admin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LobbixWhite
                        )
                    }
                }
            }
        }

        if (uiState.appConfig.bannerNotice.isNotBlank()) {
            item {
                Surface(
                    color = LobbixCrimsonContainer,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_announcement_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Campaign,
                            contentDescription = "Announcement",
                            tint = LobbixCrimsonBright,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.appConfig.bannerNotice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LobbixWhite
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TOURNAMENT LOBBIES",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LobbixWhite
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameType.entries.forEach { game ->
                        val selected = uiState.selectedGameFilter == game
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectGame(game) },
                            label = {
                                Text(
                                    text = game.displayName,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
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
                            modifier = Modifier.testTag("game_filter_${game.name.lowercase()}")
                        )
                    }
                }

                val subModes = GameCatalog.modesFor(uiState.selectedGameFilter)
                if (subModes.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subModes.forEach { mode ->
                            val selected = uiState.selectedSubModeFilter == mode
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectMode(mode) },
                                label = { Text(mode, style = MaterialTheme.typography.labelMedium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LobbixSurfaceElevated,
                                    selectedLabelColor = LobbixCrimsonBright,
                                    containerColor = LobbixSurface,
                                    labelColor = LobbixSilver
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selected,
                                    borderColor = LobbixBorder,
                                    selectedBorderColor = LobbixCrimson
                                )
                            )
                        }
                    }
                }

                val formats = GameCatalog.formatsFor(uiState.selectedGameFilter)
                if (formats.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Format:",
                            style = MaterialTheme.typography.labelMedium,
                            color = LobbixSilver
                        )
                        formats.forEach { fmt ->
                            val selected = uiState.selectedFormatFilter == fmt
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectFormat(fmt) },
                                label = { Text(fmt.displayName, style = MaterialTheme.typography.labelMedium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LobbixCrimsonContainer,
                                    selectedLabelColor = LobbixWhite,
                                    containerColor = LobbixSurface,
                                    labelColor = LobbixSilver
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selected,
                                    borderColor = LobbixBorder,
                                    selectedBorderColor = LobbixCrimsonBright
                                )
                            )
                        }
                    }
                }
            }
        }

        if (filteredMatches.isEmpty()) {
            item {
                LobbixEmptyState(
                    title = "No Matches Found",
                    subtitle = "Try another game mode or publish a new tournament from Owner Admin."
                )
            }
        } else {
            items(filteredMatches, key = { it.id }) { match ->
                val isJoined = match.joinedPlayerIds.contains(uiState.currentPlayer.id)
                TournamentCard(
                    match = match,
                    isJoined = isJoined,
                    onJoinClick = { pendingJoinMatch = match },
                    onDetailsClick = { detailsMatch = match }
                )
            }
        }
    }

    pendingJoinMatch?.let { match ->
        AlertDialog(
            onDismissRequest = { pendingJoinMatch = null },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            textContentColor = LobbixSilver,
            title = { Text("Confirm Match Entry", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${match.title} (${match.game.displayName} • ${match.mode})",
                        color = LobbixWhite,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("Entry Fee: ₹${match.entryFee} will be deducted from your wallet balance (Current: ₹${uiState.currentPlayer.totalBalance}).")
                    Text("Prize Pool: ₹${match.prizePool} • Available Slots: ${match.availableSlots}/${match.maxSlots}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onJoinMatch(match.id)
                        pendingJoinMatch = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                    modifier = Modifier.testTag("confirm_join_dialog_button")
                ) {
                    Text("Confirm & Join (₹${match.entryFee})")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingJoinMatch = null }) {
                    Text("Cancel", color = LobbixSilver)
                }
            }
        )
    }

    detailsMatch?.let { match ->
        val isJoined = match.joinedPlayerIds.contains(uiState.currentPlayer.id)
        val clipboard = LocalClipboardManager.current
        AlertDialog(
            onDismissRequest = { detailsMatch = null },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            textContentColor = LobbixSilver,
            title = { Text(match.title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${match.game.displayName} • ${match.mode} • ${match.format.displayName}",
                        color = LobbixCrimsonBright,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text("Schedule: ${match.dateTime}", color = LobbixWhite)
                    Text("Rules & Map: ${match.mapOrRules}")

                    HorizontalDivider(color = LobbixBorder)

                    Text("PRIZE BREAKDOWN", style = MaterialTheme.typography.labelLarge, color = LobbixGold)
                    match.prizeBreakdown.forEach { line ->
                        Text("• $line", color = LobbixWhite, style = MaterialTheme.typography.bodyMedium)
                    }

                    HorizontalDivider(color = LobbixBorder)

                    Text("ROOM CREDENTIALS", style = MaterialTheme.typography.labelLarge, color = LobbixWhite)
                    if (isJoined && match.isRoomLive && match.roomId.isNotBlank()) {
                        Surface(
                            color = LobbixSurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ID: ${match.roomId}",
                                        fontFamily = JetBrainsMonoFamily,
                                        color = LobbixWhite,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "PASS: ${match.roomPassword}",
                                        fontFamily = JetBrainsMonoFamily,
                                        color = LobbixGold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString("Room ID: ${match.roomId} | Pass: ${match.roomPassword}"))
                                        onCopyFeedback("Room ID & Password copied!")
                                    },
                                    modifier = Modifier.testTag("copy_room_details_modal")
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Room Credentials", tint = LobbixCrimsonBright)
                                }
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = LobbixSilver, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isJoined) "Waiting for Admin to push Room ID & Password." else "Join match to unlock live Room ID & Password.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LobbixSilver
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!isJoined && match.availableSlots > 0 && (match.status == MatchStatus.UPCOMING || match.status == MatchStatus.LIVE)) {
                    Button(
                        onClick = {
                            detailsMatch = null
                            pendingJoinMatch = match
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson)
                    ) {
                        Text("Join • ₹${match.entryFee}")
                    }
                } else {
                    TextButton(onClick = { detailsMatch = null }) {
                        Text("Close", color = LobbixWhite)
                    }
                }
            },
            dismissButton = {
                if (!isJoined) {
                    TextButton(onClick = { detailsMatch = null }) {
                        Text("Close", color = LobbixSilver)
                    }
                }
            }
        )
    }
}
