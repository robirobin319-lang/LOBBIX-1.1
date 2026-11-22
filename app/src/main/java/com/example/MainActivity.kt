package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PlayerNavTab
import com.example.state.LobbixUiState
import com.example.state.LobbixViewModel
import com.example.ui.components.LobbixTopHeader
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.LobbixBlack
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
import com.example.ui.theme.LobbixMuted
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixTheme
import com.example.ui.theme.LobbixWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            LobbixTheme {
                LobbixAppRoot()
            }
        }
    }
}

@Composable
fun LobbixAppRoot(
    viewModel: LobbixViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            viewModel.clearStatusMessage()
        }
    }

    if (!uiState.isAuthenticated) {
        Scaffold(
            containerColor = LobbixBlack,
            snackbarHost = { LobbixSnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            AuthScreen(
                onLogin = viewModel::loginWithCredentials,
                onCreateAccount = viewModel::createAccount,
                onContinueWithGoogle = viewModel::continueWithGoogle,
                modifier = Modifier.padding(innerPadding)
            )
        }
        return
    }

    if (!uiState.isAdminMode && uiState.activePlayerTab != PlayerNavTab.HOME) {
        BackHandler {
            viewModel.selectPlayerTab(PlayerNavTab.HOME)
        }
    }

    Scaffold(
        containerColor = LobbixBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { LobbixSnackbarHost(snackbarHostState) },
        topBar = {
            LobbixTopHeader(
                walletBalance = uiState.currentPlayer.totalBalance,
                unreadCount = uiState.unreadAnnouncementsCount,
                isAdminMode = uiState.isAdminMode,
                onWalletClick = { viewModel.selectPlayerTab(PlayerNavTab.WALLET) },
                onNotificationsClick = {
                    viewModel.markAnnouncementsRead()
                    showNotificationsDialog = true
                },
                onToggleAdmin = viewModel::toggleAdminMode
            )
        },
        bottomBar = {
            if (!uiState.isAdminMode) {
                LobbixBottomNavBar(
                    selectedTab = uiState.activePlayerTab,
                    onSelectTab = viewModel::selectPlayerTab
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LobbixBlack)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxSize()
            ) {
                if (uiState.isAdminMode) {
                    AdminDashboardScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onExitAdmin = { viewModel.toggleAdminMode() }
                    )
                } else {
                    PlayerTabContent(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            containerColor = LobbixSurface,
            titleContentColor = LobbixWhite,
            title = { Text("Arena Announcements", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.announcements.forEach { item ->
                        Surface(
                            color = LobbixSurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, LobbixBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.title, style = MaterialTheme.typography.titleSmall, color = LobbixCrimsonBright)
                                    Text(item.timestamp, style = MaterialTheme.typography.labelSmall, color = LobbixMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.message, style = MaterialTheme.typography.bodyMedium, color = LobbixWhite)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Got it", color = LobbixCrimsonBright)
                }
            }
        )
    }
}

@Composable
private fun PlayerTabContent(
    uiState: LobbixUiState,
    viewModel: LobbixViewModel
) {
    when (uiState.activePlayerTab) {
        PlayerNavTab.HOME -> HomeScreen(
            uiState = uiState,
            onSelectGame = viewModel::selectGameFilter,
            onSelectMode = viewModel::selectSubModeFilter,
            onSelectFormat = viewModel::selectFormatFilter,
            onJoinMatch = viewModel::joinMatch,
            onCopyFeedback = viewModel::showStatus
        )
        PlayerNavTab.MY_MATCHES -> MyMatchesScreen(
            uiState = uiState,
            onSelectTab = viewModel::selectMyMatchesTab,
            onSubmitResult = viewModel::submitMatchResultScreenshot,
            onCopyFeedback = viewModel::showStatus
        )
        PlayerNavTab.LEADERBOARD -> LeaderboardScreen(
            uiState = uiState,
            onSelectGame = viewModel::selectLeaderboardGame,
            onSelectTimeframe = viewModel::selectLeaderboardTimeframe
        )
        PlayerNavTab.WALLET -> WalletScreen(
            uiState = uiState,
            onSelectFilter = viewModel::selectWalletFilter,
            onDeposit = viewModel::depositMoney,
            onWithdraw = viewModel::requestWithdrawal
        )
        PlayerNavTab.PROFILE -> ProfileScreen(
            uiState = uiState,
            onSaveGameIds = viewModel::updateInGameIds,
            onRaiseTicket = viewModel::raiseSupportTicket,
            onCopyFeedback = viewModel::showStatus,
            onLogout = viewModel::logout
        )
    }
}

@Composable
private fun LobbixBottomNavBar(
    selectedTab: PlayerNavTab,
    onSelectTab: (PlayerNavTab) -> Unit
) {
    NavigationBar(
        containerColor = LobbixSurface,
        contentColor = LobbixWhite,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 0.dp
    ) {
        PlayerNavTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val (selectedIcon, unselectedIcon) = when (tab) {
                PlayerNavTab.HOME -> Icons.Filled.Home to Icons.Outlined.Home
                PlayerNavTab.MY_MATCHES -> Icons.Filled.SportsEsports to Icons.Outlined.SportsEsports
                PlayerNavTab.LEADERBOARD -> Icons.Filled.Leaderboard to Icons.Outlined.Leaderboard
                PlayerNavTab.WALLET -> Icons.Filled.AccountBalanceWallet to Icons.Outlined.AccountBalanceWallet
                PlayerNavTab.PROFILE -> Icons.Filled.Person to Icons.Outlined.Person
            }

            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) selectedIcon else unselectedIcon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LobbixCrimsonBright,
                    selectedTextColor = LobbixWhite,
                    indicatorColor = LobbixCrimsonContainer,
                    unselectedIconColor = LobbixSilver,
                    unselectedTextColor = LobbixMuted
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
private fun LobbixSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = LobbixSurfaceElevated,
            contentColor = LobbixWhite,
            actionColor = LobbixCrimsonBright,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
