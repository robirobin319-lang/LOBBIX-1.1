package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockClock
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.MatchParticipantResult
import com.example.model.MatchStatus
import com.example.model.TournamentMatch
import com.example.state.LobbixUiState
import com.example.ui.components.LobbixEmptyState
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixEmerald
import com.example.ui.theme.LobbixEmeraldContainer
import com.example.ui.theme.LobbixGold
import com.example.ui.theme.LobbixMuted
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixSurfaceHigh
import com.example.ui.theme.LobbixWhite

@Composable
fun MyMatchesScreen(
    uiState: LobbixUiState,
    onSelectTab: (MatchStatus) -> Unit,
    onSubmitResult: (matchId: String, uriString: String?, label: String) -> Unit,
    onCopyFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(MatchStatus.UPCOMING, MatchStatus.LIVE, MatchStatus.COMPLETED)
    val selectedIndex = tabs.indexOf(uiState.myMatchesTab).coerceAtLeast(0)
    var uploadTargetMatch by remember { mutableStateOf<TournamentMatch?>(null) }

    val joinedMatches = remember(uiState.matches, uiState.currentPlayer.id, uiState.myMatchesTab) {
        uiState.matches.filter { match ->
            match.joinedPlayerIds.contains(uiState.currentPlayer.id) &&
                match.status == uiState.myMatchesTab
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = LobbixSurface,
            contentColor = LobbixWhite,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = LobbixCrimson
                )
            }
        ) {
            tabs.forEach { status ->
                val count = uiState.matches.count {
                    it.joinedPlayerIds.contains(uiState.currentPlayer.id) && it.status == status
                }
                Tab(
                    selected = uiState.myMatchesTab == status,
                    onClick = { onSelectTab(status) },
                    text = {
                        Text(
                            text = "${status.displayName} ($count)",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    selectedContentColor = LobbixCrimsonBright,
                    unselectedContentColor = LobbixSilver,
                    modifier = Modifier.testTag("mymatches_tab_${status.name.lowercase()}")
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (joinedMatches.isEmpty()) {
                item {
                    LobbixEmptyState(
                        title = "No ${uiState.myMatchesTab.displayName} Matches",
                        subtitle = "Join tournaments from Home to track Room IDs, schedules, and result payouts here."
                    )
                }
            } else {
                items(joinedMatches, key = { it.id }) { match ->
                    val participantResult = uiState.participantResults.firstOrNull {
                        it.matchId == match.id && it.playerId == uiState.currentPlayer.id
                    }
                    JoinedMatchCard(
                        match = match,
                        result = participantResult,
                        onCopyCredentials = {
                            onCopyFeedback("Room ID & Password copied!")
                        },
                        onUploadClick = { uploadTargetMatch = match }
                    )
                }
            }
        }
    }

    uploadTargetMatch?.let { match ->
        UploadResultModal(
            match = match,
            onDismiss = { uploadTargetMatch = null },
            onSubmit = { uriString, label ->
                onSubmitResult(match.id, uriString, label)
                uploadTargetMatch = null
            }
        )
    }
}

@Composable
private fun JoinedMatchCard(
    match: TournamentMatch,
    result: MatchParticipantResult?,
    onCopyCredentials: () -> Unit,
    onUploadClick: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Surface(
        color = LobbixSurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            if (match.status == MatchStatus.LIVE) LobbixCrimson else LobbixBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(match.game.displayName.uppercase(), LobbixCrimson, LobbixWhite)
                    StatusBadge("${match.mode} • ${match.format.displayName}", LobbixSurfaceHigh, LobbixSilver)
                }
                MatchStatusPill(status = match.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = match.title, style = MaterialTheme.typography.titleLarge, color = LobbixWhite)
            Text(
                text = "${match.dateTime} • Prize Pool ₹${match.prizePool}",
                style = MaterialTheme.typography.bodyMedium,
                color = LobbixSilver
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (match.isRoomLive && match.roomId.isNotBlank()) {
                Surface(
                    color = LobbixSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Key,
                                contentDescription = null,
                                tint = LobbixGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ROOM ID: ${match.roomId}",
                                    fontFamily = JetBrainsMonoFamily,
                                    color = LobbixWhite,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "PASSWORD: ${match.roomPassword}",
                                    fontFamily = JetBrainsMonoFamily,
                                    color = LobbixGold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                clipboard.setText(
                                    AnnotatedString("LOBBIX Room ID: ${match.roomId} | Pass: ${match.roomPassword}")
                                )
                                onCopyCredentials()
                            },
                            modifier = Modifier.testTag("copy_room_${match.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy Room Credentials",
                                tint = LobbixCrimsonBright
                            )
                        }
                    }
                }
            } else {
                Surface(
                    color = LobbixSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LobbixBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.LockClock, contentDescription = null, tint = LobbixSilver, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Room ID & Password will appear here once Admin pushes them live.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LobbixSilver
                        )
                    }
                }
            }

            if (match.status == MatchStatus.COMPLETED || match.status == MatchStatus.LIVE) {
                Spacer(modifier = Modifier.height(12.dp))
                if (result?.isResultCredited == true) {
                    Surface(
                        color = LobbixEmeraldContainer,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LobbixEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = LobbixGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verified: Rank #${result.rank} • ${result.kills} Kills • Credited ₹${result.prizeCredited}",
                                style = MaterialTheme.typography.labelLarge,
                                color = LobbixEmerald
                            )
                        }
                    }
                } else {
                    if (result?.screenshotLabel != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = LobbixEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proof Uploaded: ${result.screenshotLabel} (Pending Admin Review)",
                                style = MaterialTheme.typography.labelMedium,
                                color = LobbixEmerald
                            )
                        }
                    }
                    if (match.status == MatchStatus.COMPLETED) {
                        Button(
                            onClick = onUploadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LobbixCrimson,
                                contentColor = LobbixWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_result_button_${match.id}")
                        ) {
                            Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (result?.screenshotLabel != null) "Re-Upload Result / Screenshot" else "Upload Result / Screenshot",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadResultModal(
    match: TournamentMatch,
    onDismiss: () -> Unit,
    onSubmit: (uriString: String?, label: String) -> Unit
) {
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var noteLabel by remember { mutableStateOf("match_result_rank1_proof.png") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
            noteLabel = uri.lastPathSegment ?: "gallery_screenshot.png"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LobbixSurface,
        titleContentColor = LobbixWhite,
        textContentColor = LobbixSilver,
        title = { Text("Upload Result Proof", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Submit your end-of-match scoreboard screenshot for ${match.title}.",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (pickedUri != null) {
                    AsyncImage(
                        model = pickedUri,
                        contentDescription = "Selected Match Screenshot",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, LobbixCrimson, RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LobbixSurfaceElevated)
                            .border(1.dp, LobbixBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Image, contentDescription = null, tint = LobbixSilver)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("No gallery photo selected yet", style = MaterialTheme.typography.labelMedium, color = LobbixMuted)
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    border = BorderStroke(1.dp, LobbixCrimson),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_screenshot_button")
                ) {
                    Icon(Icons.Filled.Image, contentDescription = null, tint = LobbixCrimsonBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose Screenshot from Device", color = LobbixWhite)
                }

                OutlinedTextField(
                    value = noteLabel,
                    onValueChange = { noteLabel = it },
                    label = { Text("Proof Label / Kill Note") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LobbixCrimson,
                        unfocusedBorderColor = LobbixBorder,
                        focusedTextColor = LobbixWhite,
                        unfocusedTextColor = LobbixWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(pickedUri?.toString(), noteLabel.ifBlank { "match_proof.png" })
                },
                colors = ButtonDefaults.buttonColors(containerColor = LobbixCrimson),
                modifier = Modifier.testTag("submit_result_proof_button")
            ) {
                Text("Submit Proof")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LobbixSilver)
            }
        }
    )
}
