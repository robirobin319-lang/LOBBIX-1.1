package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.model.GameType
import com.example.model.LeaderboardEntry
import com.example.model.LeaderboardTimeframe
import com.example.state.LobbixUiState
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixBronze
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixMuted
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSilverMedal
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixWhite

@Composable
fun LeaderboardScreen(
    uiState: LobbixUiState,
    onSelectGame: (GameType) -> Unit,
    onSelectTimeframe: (LeaderboardTimeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    val games = listOf(GameType.FREE_FIRE, GameType.LUDO, GameType.CARROM)
    val ranked = remember(uiState.leaderboardEntries, uiState.leaderboardGame, uiState.leaderboardTimeframe) {
        uiState.leaderboardEntries
            .filter { it.game == uiState.leaderboardGame }
            .sortedByDescending {
                if (uiState.leaderboardTimeframe == LeaderboardTimeframe.WEEKLY) it.weeklyPoints else it.allTimePoints
            }
    }

    val top3 = ranked.take(3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ARENA LEADERBOARD",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LobbixWhite
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    games.forEach { game ->
                        val selected = uiState.leaderboardGame == game
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectGame(game) },
                            label = { Text(game.displayName, style = MaterialTheme.typography.labelLarge) },
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
                            modifier = Modifier.testTag("lb_game_${game.name.lowercase()}")
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LeaderboardTimeframe.entries.forEach { tf ->
                        val selected = uiState.leaderboardTimeframe == tf
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectTimeframe(tf) },
                            label = { Text(tf.displayName, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LobbixCrimsonContainer,
                                selectedLabelColor = LobbixCrimsonBright,
                                containerColor = LobbixSurface,
                                labelColor = LobbixSilver
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = LobbixBorder,
                                selectedBorderColor = LobbixCrimson
                            ),
                            modifier = Modifier.testTag("lb_time_${tf.name.lowercase()}")
                        )
                    }
                }
            }
        }

        if (top3.isNotEmpty()) {
            item {
                TopThreePodium(
                    entries = top3,
                    timeframe = uiState.leaderboardTimeframe,
                    metricLabel = if (uiState.leaderboardGame == GameType.FREE_FIRE) "Kills" else "Wins"
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("RANK & PLAYER", style = MaterialTheme.typography.labelSmall, color = LobbixMuted)
                Text(
                    text = if (uiState.leaderboardGame == GameType.FREE_FIRE) "MATCHES / KILLS / PTS" else "MATCHES / WINS / PTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = LobbixMuted
                )
            }
        }

        itemsIndexed(ranked, key = { _, item -> "${item.playerId}_${item.game}" }) { index, entry ->
            val rank = index + 1
            val points = if (uiState.leaderboardTimeframe == LeaderboardTimeframe.WEEKLY) {
                entry.weeklyPoints
            } else {
                entry.allTimePoints
            }
            val isCurrentUser = entry.playerId == uiState.currentPlayer.id

            Surface(
                color = if (isCurrentUser) LobbixCrimsonContainer.copy(alpha = 0.5f) else LobbixSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    if (isCurrentUser) LobbixCrimson else LobbixBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        val rankColor = when (rank) {
                            1 -> LobbixGold
                            2 -> LobbixSilverMedal
                            3 -> LobbixBronze
                            else -> LobbixSilver
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(LobbixSurfaceElevated)
                                .border(1.dp, rankColor.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#$rank",
                                style = MaterialTheme.typography.labelLarge,
                                color = rankColor,
                                fontFamily = JetBrainsMonoFamily
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${entry.squadTag} ${entry.playerName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = LobbixWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isCurrentUser) {
                                Text("YOU", style = MaterialTheme.typography.labelSmall, color = LobbixCrimsonBright)
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "${entry.matches}M",
                            style = MaterialTheme.typography.labelMedium,
                            color = LobbixSilver,
                            fontFamily = JetBrainsMonoFamily
                        )
                        Text(
                            text = "${entry.killsOrWins}${if (uiState.leaderboardGame == GameType.FREE_FIRE) "K" else "W"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = LobbixCrimsonBright,
                            fontFamily = JetBrainsMonoFamily
                        )
                        Surface(
                            color = LobbixSurfaceElevated,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$points pts",
                                style = MaterialTheme.typography.labelLarge,
                                color = LobbixGold,
                                fontFamily = JetBrainsMonoFamily,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopThreePodium(
    entries: List<LeaderboardEntry>,
    timeframe: LeaderboardTimeframe,
    metricLabel: String
) {
    val first = entries.getOrNull(0)
    val second = entries.getOrNull(1)
    val third = entries.getOrNull(2)

    Surface(
        color = LobbixSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(LobbixCrimson.copy(alpha = 0.14f), LobbixSurface)
                    )
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (second != null) {
                PodiumColumn(
                    rank = 2,
                    entry = second,
                    timeframe = timeframe,
                    metricLabel = metricLabel,
                    accentColor = LobbixSilverMedal,
                    podiumHeight = 74,
                    modifier = Modifier.weight(1f)
                )
            }
            if (first != null) {
                PodiumColumn(
                    rank = 1,
                    entry = first,
                    timeframe = timeframe,
                    metricLabel = metricLabel,
                    accentColor = LobbixGold,
                    podiumHeight = 102,
                    modifier = Modifier.weight(1.1f)
                )
            }
            if (third != null) {
                PodiumColumn(
                    rank = 3,
                    entry = third,
                    timeframe = timeframe,
                    metricLabel = metricLabel,
                    accentColor = LobbixBronze,
                    podiumHeight = 58,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    rank: Int,
    entry: LeaderboardEntry,
    timeframe: LeaderboardTimeframe,
    metricLabel: String,
    accentColor: Color,
    podiumHeight: Int,
    modifier: Modifier = Modifier
) {
    val pts = if (timeframe == LeaderboardTimeframe.WEEKLY) entry.weeklyPoints else entry.allTimePoints
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (rank == 1) Icons.Filled.WorkspacePremium else Icons.Filled.EmojiEvents,
            contentDescription = "Rank $rank",
            tint = accentColor,
            modifier = Modifier.size(if (rank == 1) 28.dp else 22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = entry.playerName,
            style = MaterialTheme.typography.labelLarge,
            color = LobbixWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${entry.killsOrWins} $metricLabel",
            style = MaterialTheme.typography.labelSmall,
            color = LobbixSilver,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            color = LobbixSurfaceElevated,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleLarge,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$pts pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = LobbixWhite,
                    fontFamily = JetBrainsMonoFamily
                )
            }
        }
    }
}
