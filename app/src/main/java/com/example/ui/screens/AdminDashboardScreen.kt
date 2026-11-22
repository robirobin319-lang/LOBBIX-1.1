package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminTab
import com.example.state.LobbixUiState
import com.example.state.LobbixViewModel
import com.example.ui.theme.LobbixBlack
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixWhite

@Composable
fun AdminDashboardScreen(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onExitAdmin)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LobbixBlack)
            .navigationBarsPadding()
    ) {
        Surface(
            color = LobbixSurface,
            border = BorderStroke(1.dp, LobbixBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminTab.entries.forEach { tab ->
                    val selected = uiState.activeAdminTab == tab
                    val badgeCount = when (tab) {
                        AdminTab.RESULTS -> uiState.pendingProofsCount
                        AdminTab.QUEUE -> uiState.pendingWithdrawalsCount + uiState.openTicketsCount
                        else -> 0
                    }
                    val icon = when (tab) {
                        AdminTab.OVERVIEW -> Icons.Filled.Dashboard
                        AdminTab.MATCHES -> Icons.Filled.SportsEsports
                        AdminTab.ROOMS -> Icons.Filled.Key
                        AdminTab.RESULTS -> Icons.Filled.EmojiEvents
                        AdminTab.USERS -> Icons.Filled.Group
                        AdminTab.QUEUE -> Icons.Filled.ReceiptLong
                    }

                    Surface(
                        color = if (selected) LobbixCrimson else LobbixSurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (selected) LobbixCrimson else LobbixBorder
                        ),
                        modifier = Modifier
                            .testTag("admin_tab_${tab.name.lowercase()}")
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectAdminTab(tab) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.label,
                                tint = if (selected) LobbixWhite else LobbixSilver,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) LobbixWhite else LobbixSilver
                            )
                            if (badgeCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) LobbixBlack else LobbixCrimson),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badgeCount.toString(),
                                        color = LobbixWhite,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState.activeAdminTab) {
                AdminTab.OVERVIEW -> AdminOverviewTab(uiState = uiState, viewModel = viewModel)
                AdminTab.MATCHES -> AdminMatchesTab(uiState = uiState, viewModel = viewModel)
                AdminTab.ROOMS -> AdminRoomIdsTab(uiState = uiState, viewModel = viewModel)
                AdminTab.RESULTS -> AdminResultsTab(uiState = uiState, viewModel = viewModel)
                AdminTab.USERS -> AdminUsersTab(uiState = uiState, viewModel = viewModel)
                AdminTab.QUEUE -> AdminQueueAndSupportTab(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}
