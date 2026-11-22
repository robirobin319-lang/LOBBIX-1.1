package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.MatchParticipantResult
import com.example.model.MatchStatus
import com.example.state.LobbixUiState
import com.example.state.LobbixViewModel
import com.example.ui.components.LobbixEmptyState
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.StatusBadge
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixEmerald
import com.example.ui.theme.LobbixEmeraldContainer
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixWhite

@Composable
fun AdminRoomIdsTab(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    val activeMatches = remember(uiState.matches) {
        uiState.matches.filter { it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("ROOM ID & PASSWORD CONTROLLER", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
            Text(
                text = "Pushing credentials reveals Room ID & Password instantly in players' My Matches.",
                style = MaterialTheme.typography.bodyMedium,
                color = LobbixSilver
            )
        }

        if (activeMatches.isEmpty()) {
            item {
                LobbixEmptyState(
                    title = "No Active Matches",
                    subtitle = "Create or set a tournament to Upcoming/Live to push Room credentials."
                )
            }
        } else {
            items(activeMatches, key = { it.id }) { match ->
                var roomId by remember(match.id, match.roomId) { mutableStateOf(match.roomId) }
                var roomPass by remember(match.id, match.roomPassword) { mutableStateOf(match.roomPassword) }
                val fieldColors = adminTextFieldColors()

                Surface(
                    color = LobbixSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (match.isRoomLive) LobbixEmerald.copy(alpha = 0.55f) else LobbixBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(match.title, style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                                Text("${match.game.displayName} • ${match.filledSlots} Joined", style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                            }
                            if (match.isRoomLive) {
                                StatusBadge("CREDENTIALS LIVE", LobbixEmeraldContainer, LobbixEmerald)
                            } else {
                                MatchStatusPill(status = match.status)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = roomId,
                                onValueChange = { roomId = it },
                                label = { Text("Room ID") },
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_room_id_${match.id}")
                            )
                            OutlinedTextField(
                                value = roomPass,
                                onValueChange = { roomPass = it },
                                label = { Text("Password") },
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_room_pass_${match.id}")
                            )
                        }

                        Button(
                            onClick = { viewModel.pushRoomDetailsLive(match.id, roomId, roomPass) },
                            colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("push_room_live_${match.id}")
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Push Room Details Live", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminResultsTab(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    val matchesWithPlayers = remember(uiState.matches) {
        uiState.matches.filter { it.status != MatchStatus.CANCELLED }
    }
    var selectedMatchId by remember(matchesWithPlayers) {
        mutableStateOf(
            matchesWithPlayers.firstOrNull { it.status == MatchStatus.COMPLETED }?.id
                ?: matchesWithPlayers.firstOrNull()?.id
                ?: ""
        )
    }

    val selectedMatch = matchesWithPlayers.firstOrNull { it.id == selectedMatchId }
    val participants = remember(uiState.participantResults, uiState.players, selectedMatch) {
        if (selectedMatch == null) {
            emptyList()
        } else {
            val realPlayers = uiState.players.filter { selectedMatch.joinedPlayerIds.contains(it.id) }
            realPlayers.map { player ->
                uiState.participantResults.firstOrNull {
                    it.matchId == selectedMatch.id && it.playerId == player.id
                } ?: MatchParticipantResult(
                    matchId = selectedMatch.id,
                    playerId = player.id,
                    playerName = player.inGameName,
                    playerUid = player.freeFireUid
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("RESULTS, KILLS & PRIZE DISTRIBUTION", style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                matchesWithPlayers.forEach { match ->
                    FilterChip(
                        selected = match.id == selectedMatchId,
                        onClick = { selectedMatchId = match.id },
                        label = { Text(match.title, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LobbixCrimson,
                            selectedLabelColor = LobbixWhite,
                            containerColor = LobbixSurface,
                            labelColor = LobbixSilver
                        )
                    )
                }
            }
        }

        if (selectedMatch != null) {
            item {
                Surface(
                    color = LobbixSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LobbixBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Pool: ₹${selectedMatch.prizePool} (1st: 50%, 2nd: 25%, 3rd: 15%) + ₹${selectedMatch.perKillReward}/Kill",
                        style = MaterialTheme.typography.labelLarge,
                        color = LobbixGold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        if (participants.isEmpty()) {
            item {
                LobbixEmptyState(
                    title = "No Joined Players Yet",
                    subtitle = "Once players join this match, grade their kills & rank here to credit prizes."
                )
            }
        } else {
            items(participants, key = { "${it.matchId}_${it.playerId}" }) { participant ->
                var killsText by remember(participant.matchId, participant.playerId, participant.kills) {
                    mutableStateOf(participant.kills.toString())
                }
                var rankText by remember(participant.matchId, participant.playerId, participant.rank) {
                    mutableStateOf(if (participant.rank == 0) "1" else participant.rank.toString())
                }
                val fieldColors = adminTextFieldColors()

                Surface(
                    color = LobbixSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (participant.isResultCredited) LobbixEmerald.copy(alpha = 0.5f) else LobbixBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(participant.playerName, style = MaterialTheme.typography.titleMedium, color = LobbixWhite)
                                Text("UID: ${participant.playerUid}", style = MaterialTheme.typography.labelSmall, color = LobbixSilver)
                            }
                            if (participant.isResultCredited) {
                                StatusBadge("CREDITED ₹${participant.prizeCredited}", LobbixEmeraldContainer, LobbixEmerald)
                            }
                        }

                        if (participant.screenshotUri != null) {
                            AsyncImage(
                                model = participant.screenshotUri,
                                contentDescription = "Submitted Proof Screenshot",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, LobbixBorder, RoundedCornerShape(10.dp))
                            )
                        } else if (participant.screenshotLabel != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Image, contentDescription = null, tint = LobbixGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Proof Attached: ${participant.screenshotLabel} (${participant.submittedAt ?: "Recent"})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = LobbixGold
                                )
                            }
                        } else {
                            Text("No screenshot uploaded yet", style = MaterialTheme.typography.bodyMedium, color = LobbixSilver)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = killsText,
                                onValueChange = { killsText = it.filter(Char::isDigit) },
                                label = { Text("Kills") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("kills_input_${participant.playerId}")
                            )
                            OutlinedTextField(
                                value = rankText,
                                onValueChange = { rankText = it.filter(Char::isDigit) },
                                label = { Text("Rank #") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = fieldColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("rank_input_${participant.playerId}")
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.calculateAndCreditWinnings(
                                    matchId = participant.matchId,
                                    playerId = participant.playerId,
                                    kills = killsText.toIntOrNull() ?: 0,
                                    rank = rankText.toIntOrNull() ?: 1
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (participant.isResultCredited) LobbixSurfaceElevated else LobbixCrimson
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("credit_winnings_${participant.playerId}")
                        ) {
                            Icon(
                                imageVector = if (participant.isResultCredited) Icons.Filled.CheckCircle else Icons.Filled.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (participant.isResultCredited) "Recalculate & Update Winnings" else "Calculate & Credit Winnings",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
