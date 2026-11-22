package com.example.state

import androidx.lifecycle.ViewModel
import com.example.model.AdminTab
import com.example.model.AppAnnouncement
import com.example.model.GameType
import com.example.model.LeaderboardEntry
import com.example.model.MatchParticipantResult
import com.example.model.MatchStatus
import com.example.model.PlayerAccount
import com.example.model.PlayerNavTab
import com.example.model.SupportTicket
import com.example.model.TeamFormat
import com.example.model.TournamentMatch
import com.example.model.TransactionType
import com.example.model.WalletFilter
import com.example.model.WalletTransaction
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.example.model.LeaderboardTimeframe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class LobbixViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LobbixUiState())
    val uiState: StateFlow<LobbixUiState> = _uiState.asStateFlow()

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun showStatus(message: String) {
        _uiState.update { it.copy(statusMessage = message) }
    }

    // --- AUTH ACTIONS ---

    fun loginWithCredentials(emailOrMobile: String, password: String) {
        if (emailOrMobile.isBlank() || password.isBlank()) {
            showStatus("Enter your Mobile/Email and Password to login.")
            return
        }
        val matched = _uiState.value.players.firstOrNull {
            it.emailOrMobile.equals(emailOrMobile.trim(), ignoreCase = true) ||
                it.inGameName.equals(emailOrMobile.trim(), ignoreCase = true)
        }
        if (matched != null) {
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentPlayerId = matched.id,
                    activePlayerTab = PlayerNavTab.HOME,
                    statusMessage = "Welcome back, ${matched.inGameName}!"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentPlayerId = "p1",
                    activePlayerTab = PlayerNavTab.HOME,
                    statusMessage = "Logged in as ViperX_GG!"
                )
            }
        }
    }

    fun createAccount(inGameName: String, emailOrMobile: String, password: String) {
        if (inGameName.isBlank() || emailOrMobile.isBlank() || password.length < 4) {
            showStatus("Fill In-Game Name, Mobile/Email, and a 4+ char password.")
            return
        }
        val newId = "p_${UUID.randomUUID().toString().take(6)}"
        val cleanTag = inGameName.trim().uppercase().take(5)
        val newPlayer = PlayerAccount(
            id = newId,
            inGameName = inGameName.trim(),
            emailOrMobile = emailOrMobile.trim(),
            freeFireUid = "FF${(10000000..99999999).random()}",
            ludoId = "LDO-${(10000..99999).random()}",
            carromId = "CRM-${(10000..99999).random()}",
            depositBalance = 100,
            winningBalance = 0,
            matchesPlayed = 0,
            wins = 0,
            totalKills = 0,
            totalEarnings = 0,
            isBanned = false,
            referralCode = "LOBBIX-$cleanTag"
        )
        val bonusTx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            playerId = newId,
            type = TransactionType.DEPOSIT,
            amount = 100,
            isCredit = true,
            description = "Welcome Arena Bonus",
            timestamp = "Just now"
        )
        _uiState.update { state ->
            state.copy(
                isAuthenticated = true,
                currentPlayerId = newId,
                activePlayerTab = PlayerNavTab.HOME,
                players = listOf(newPlayer) + state.players,
                transactions = listOf(bonusTx) + state.transactions,
                statusMessage = "Account created! ₹100 Welcome Bonus added."
            )
        }
    }

    fun continueWithGoogle() {
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                currentPlayerId = "p1",
                activePlayerTab = PlayerNavTab.HOME,
                statusMessage = "Signed in with Google as ViperX_GG"
            )
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                isAdminMode = false,
                activePlayerTab = PlayerNavTab.HOME,
                statusMessage = "Logged out safely."
            )
        }
    }

    // --- NAVIGATION & MODE SWITCHING ---

    fun toggleAdminMode() {
        _uiState.update {
            val nextMode = !it.isAdminMode
            it.copy(
                isAdminMode = nextMode,
                statusMessage = if (nextMode) "Switched to Owner Admin Panel" else "Switched to Player View"
            )
        }
    }

    fun selectPlayerTab(tab: PlayerNavTab) {
        _uiState.update { it.copy(activePlayerTab = tab, isAdminMode = false) }
    }

    fun selectAdminTab(tab: AdminTab) {
        _uiState.update { it.copy(activeAdminTab = tab, isAdminMode = true) }
    }

    fun markAnnouncementsRead() {
        _uiState.update { state ->
            state.copy(announcements = state.announcements.map { it.copy(isRead = true) })
        }
    }

    // --- PLAYER HOME & FILTERS ---

    fun selectGameFilter(game: GameType) {
        _uiState.update {
            it.copy(
                selectedGameFilter = game,
                selectedSubModeFilter = "All Modes",
                selectedFormatFilter = null
            )
        }
    }

    fun selectSubModeFilter(mode: String) {
        _uiState.update { it.copy(selectedSubModeFilter = mode) }
    }

    fun selectFormatFilter(format: TeamFormat?) {
        _uiState.update {
            it.copy(selectedFormatFilter = if (it.selectedFormatFilter == format) null else format)
        }
    }

    fun joinMatch(matchId: String) {
        val state = _uiState.value
        val player = state.currentPlayer
        val match = state.matches.firstOrNull { it.id == matchId } ?: return

        if (state.appConfig.maintenanceMode) {
            showStatus("Arena is in Maintenance Mode. Joining is paused by Admin.")
            return
        }
        if (player.isBanned) {
            showStatus("Your account is suspended. Contact Support.")
            return
        }
        if (match.status == MatchStatus.COMPLETED || match.status == MatchStatus.CANCELLED) {
            showStatus("This tournament is no longer accepting entries.")
            return
        }
        if (match.joinedPlayerIds.contains(player.id)) {
            showStatus("You already joined ${match.title}!")
            return
        }
        if (match.availableSlots <= 0) {
            showStatus("All slots are filled for ${match.title}.")
            return
        }
        if (player.totalBalance < match.entryFee) {
            showStatus("Insufficient balance (Need ₹${match.entryFee}). Add funds in Wallet!")
            return
        }

        val fromDeposit = minOf(player.depositBalance, match.entryFee)
        val fromWinning = match.entryFee - fromDeposit

        val updatedPlayer = player.copy(
            depositBalance = player.depositBalance - fromDeposit,
            winningBalance = player.winningBalance - fromWinning,
            matchesPlayed = player.matchesPlayed + 1
        )
        val updatedMatch = match.copy(
            joinedPlayerIds = match.joinedPlayerIds + player.id
        )
        val playerUid = when (match.game) {
            GameType.FREE_FIRE, GameType.ALL -> player.freeFireUid
            GameType.LUDO -> player.ludoId
            GameType.CARROM -> player.carromId
        }
        val newParticipant = MatchParticipantResult(
            matchId = match.id,
            playerId = player.id,
            playerName = player.inGameName,
            playerUid = playerUid
        )
        val newTx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            playerId = player.id,
            type = TransactionType.ENTRY_FEE,
            amount = match.entryFee,
            isCredit = false,
            description = "Joined ${match.title}",
            timestamp = "Just now"
        )

        _uiState.update { current ->
            current.copy(
                players = current.players.map { if (it.id == player.id) updatedPlayer else it },
                matches = current.matches.map { if (it.id == match.id) updatedMatch else it },
                participantResults = listOf(newParticipant) + current.participantResults,
                transactions = listOf(newTx) + current.transactions,
                statusMessage = "Joined ${match.title}! Check My Matches for Room ID."
            )
        }
    }

    // --- MY MATCHES ---

    fun selectMyMatchesTab(status: MatchStatus) {
        _uiState.update { it.copy(myMatchesTab = status) }
    }

    fun submitMatchResultScreenshot(matchId: String, uriString: String?, label: String) {
        val state = _uiState.value
        val player = state.currentPlayer
        val existing = state.participantResults.firstOrNull {
            it.matchId == matchId && it.playerId == player.id
        }
        val updatedList = if (existing != null) {
            state.participantResults.map {
                if (it.matchId == matchId && it.playerId == player.id) {
                    it.copy(
                        screenshotUri = uriString,
                        screenshotLabel = label,
                        submittedAt = "Just now"
                    )
                } else it
            }
        } else {
            val newEntry = MatchParticipantResult(
                matchId = matchId,
                playerId = player.id,
                playerName = player.inGameName,
                playerUid = player.freeFireUid,
                screenshotUri = uriString,
                screenshotLabel = label,
                submittedAt = "Just now"
            )
            listOf(newEntry) + state.participantResults
        }

        _uiState.update {
            it.copy(
                participantResults = updatedList,
                statusMessage = "Result screenshot submitted to Admin for verification!"
            )
        }
    }

    // --- LEADERBOARD ---

    fun selectLeaderboardGame(game: GameType) {
        _uiState.update { it.copy(leaderboardGame = game) }
    }

    fun selectLeaderboardTimeframe(timeframe: LeaderboardTimeframe) {
        _uiState.update { it.copy(leaderboardTimeframe = timeframe) }
    }

    // --- WALLET ---

    fun selectWalletFilter(filter: WalletFilter) {
        _uiState.update { it.copy(walletFilter = filter) }
    }

    fun depositMoney(amount: Int, methodLabel: String) {
        if (amount <= 0) {
            showStatus("Enter a valid deposit amount.")
            return
        }
        val state = _uiState.value
        val player = state.currentPlayer
        val updatedPlayer = player.copy(depositBalance = player.depositBalance + amount)
        val tx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            playerId = player.id,
            type = TransactionType.DEPOSIT,
            amount = amount,
            isCredit = true,
            description = "$methodLabel Deposit",
            timestamp = "Just now"
        )
        _uiState.update {
            it.copy(
                players = it.players.map { p -> if (p.id == player.id) updatedPlayer else p },
                transactions = listOf(tx) + it.transactions,
                statusMessage = "₹$amount added to your Deposit Balance!"
            )
        }
    }

    fun requestWithdrawal(amount: Int, upiId: String) {
        val state = _uiState.value
        val player = state.currentPlayer
        if (!state.appConfig.withdrawalsEnabled) {
            showStatus("Withdrawals are temporarily paused by Admin.")
            return
        }
        if (player.isBanned) {
            showStatus("Account suspended. Withdrawals disabled.")
            return
        }
        if (amount < 50) {
            showStatus("Minimum withdrawal amount is ₹50.")
            return
        }
        if (upiId.isBlank() || !upiId.contains("@")) {
            showStatus("Enter a valid UPI ID (e.g., player@okaxis).")
            return
        }
        if (player.totalBalance < amount) {
            showStatus("Insufficient balance for ₹$amount withdrawal.")
            return
        }

        val fromWinning = minOf(player.winningBalance, amount)
        val fromDeposit = amount - fromWinning
        val updatedPlayer = player.copy(
            winningBalance = player.winningBalance - fromWinning,
            depositBalance = player.depositBalance - fromDeposit
        )
        val req = WithdrawalRequest(
            id = "w_${UUID.randomUUID().toString().take(6)}",
            playerId = player.id,
            playerName = player.inGameName,
            amount = amount,
            upiId = upiId.trim(),
            requestedAt = "Just now",
            status = WithdrawalStatus.PENDING
        )
        val tx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            playerId = player.id,
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            isCredit = false,
            description = "UPI Withdrawal to ${upiId.trim()}",
            timestamp = "Just now",
            statusLabel = "Pending Admin"
        )

        _uiState.update {
            it.copy(
                players = it.players.map { p -> if (p.id == player.id) updatedPlayer else p },
                withdrawalRequests = listOf(req) + it.withdrawalRequests,
                transactions = listOf(tx) + it.transactions,
                statusMessage = "Withdrawal request for ₹$amount sent to Admin Queue!"
            )
        }
    }

    // --- PROFILE & SUPPORT ---

    fun updateInGameIds(freeFireUid: String, ludoId: String, carromId: String) {
        val player = _uiState.value.currentPlayer
        val updated = player.copy(
            freeFireUid = freeFireUid.trim().ifBlank { player.freeFireUid },
            ludoId = ludoId.trim().ifBlank { player.ludoId },
            carromId = carromId.trim().ifBlank { player.carromId }
        )
        _uiState.update { state ->
            state.copy(
                players = state.players.map { if (it.id == player.id) updated else it },
                statusMessage = "In-Game IDs saved!"
            )
        }
    }

    fun raiseSupportTicket(subject: String, message: String) {
        if (subject.isBlank() || message.isBlank()) {
            showStatus("Please enter both Subject and Message.")
            return
        }
        val player = _uiState.value.currentPlayer
        val ticket = SupportTicket(
            id = "t_${UUID.randomUUID().toString().take(6)}",
            playerId = player.id,
            playerName = player.inGameName,
            subject = subject.trim(),
            message = message.trim(),
            createdAt = "Just now"
        )
        _uiState.update {
            it.copy(
                supportTickets = listOf(ticket) + it.supportTickets,
                statusMessage = "Support ticket sent to Owner Admin!"
            )
        }
    }

    // --- ADMIN TAB 1: OVERVIEW & GLOBAL APP CONTROLS ---

    fun toggleMaintenanceMode(enabled: Boolean) {
        _uiState.update {
            it.copy(
                appConfig = it.appConfig.copy(maintenanceMode = enabled),
                statusMessage = if (enabled) "Maintenance Mode ENABLED" else "Maintenance Mode DISABLED"
            )
        }
    }

    fun toggleWithdrawalsEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                appConfig = it.appConfig.copy(withdrawalsEnabled = enabled),
                statusMessage = if (enabled) "Player Withdrawals ENABLED" else "Player Withdrawals PAUSED"
            )
        }
    }

    fun publishNotice(title: String, message: String) {
        if (message.isBlank()) {
            showStatus("Enter an announcement message to publish.")
            return
        }
        val cleanTitle = title.trim().ifBlank { "Admin Notice" }
        val cleanMsg = message.trim()
        val announcement = AppAnnouncement(
            id = "a_${UUID.randomUUID().toString().take(6)}",
            title = cleanTitle,
            message = cleanMsg,
            timestamp = "Just now",
            isRead = false
        )
        _uiState.update {
            it.copy(
                appConfig = it.appConfig.copy(bannerNotice = "$cleanTitle: $cleanMsg"),
                announcements = listOf(announcement) + it.announcements,
                statusMessage = "Live Notice published to Player Home & Bell!"
            )
        }
    }

    // --- ADMIN TAB 2: FULL MATCH CONTROL ---

    fun createTournament(
        game: GameType,
        mode: String,
        format: TeamFormat,
        title: String,
        entryFee: Int,
        prizePool: Int,
        perKillReward: Int,
        maxSlots: Int,
        dateTime: String,
        mapOrRules: String
    ) {
        if (title.isBlank() || maxSlots <= 0) {
            showStatus("Provide a valid Match Title and Max Slots.")
            return
        }
        val newMatch = TournamentMatch(
            id = "m_${UUID.randomUUID().toString().take(6)}",
            game = if (game == GameType.ALL) GameType.FREE_FIRE else game,
            mode = mode.ifBlank { "Battle Royale" },
            format = format,
            title = title.trim(),
            dateTime = dateTime.trim().ifBlank { "Today, 10:30 PM IST" },
            mapOrRules = mapOrRules.trim().ifBlank { "Standard Fair Play Rules • Screenshot Required" },
            entryFee = entryFee.coerceAtLeast(0),
            prizePool = prizePool.coerceAtLeast(0),
            perKillReward = perKillReward.coerceAtLeast(0),
            maxSlots = maxSlots.coerceAtLeast(2),
            joinedPlayerIds = emptyList(),
            status = MatchStatus.UPCOMING,
            prizeBreakdown = listOf(
                "1st Place: ₹${(prizePool * 0.5).toInt()}",
                "2nd Place: ₹${(prizePool * 0.3).toInt()}",
                "3rd Place: ₹${(prizePool * 0.2).toInt()}",
                if (perKillReward > 0) "Per Kill: ₹$perKillReward" else "Winner Bonus Included"
            )
        )
        _uiState.update {
            it.copy(
                matches = listOf(newMatch) + it.matches,
                statusMessage = "Published '${newMatch.title}' to Player Home!"
            )
        }
    }

    fun editTournament(
        matchId: String,
        title: String,
        entryFee: Int,
        prizePool: Int,
        perKillReward: Int,
        maxSlots: Int,
        dateTime: String
    ) {
        _uiState.update { state ->
            state.copy(
                matches = state.matches.map { match ->
                    if (match.id == matchId) {
                        match.copy(
                            title = title.trim().ifBlank { match.title },
                            entryFee = entryFee.coerceAtLeast(0),
                            prizePool = prizePool.coerceAtLeast(0),
                            perKillReward = perKillReward.coerceAtLeast(0),
                            maxSlots = maxSlots.coerceAtLeast(match.filledSlots.coerceAtLeast(2)),
                            dateTime = dateTime.trim().ifBlank { match.dateTime }
                        )
                    } else match
                },
                statusMessage = "Updated match details!"
            )
        }
    }

    fun updateMatchStatus(matchId: String, newStatus: MatchStatus) {
        _uiState.update { state ->
            state.copy(
                matches = state.matches.map {
                    if (it.id == matchId) it.copy(status = newStatus) else it
                },
                statusMessage = "Match status changed to ${newStatus.displayName}."
            )
        }
    }

    fun cancelAndRefundMatch(matchId: String) {
        val state = _uiState.value
        val match = state.matches.firstOrNull { it.id == matchId } ?: return
        if (match.status == MatchStatus.CANCELLED) {
            showStatus("Match is already cancelled.")
            return
        }

        val realJoinedIds = match.joinedPlayerIds.toSet()
        val refundTxs = mutableListOf<WalletTransaction>()
        val updatedPlayers = state.players.map { player ->
            if (realJoinedIds.contains(player.id) && match.entryFee > 0) {
                refundTxs.add(
                    WalletTransaction(
                        id = "tx_${UUID.randomUUID().toString().take(6)}",
                        playerId = player.id,
                        type = TransactionType.REFUND,
                        amount = match.entryFee,
                        isCredit = true,
                        description = "Refund: ${match.title} Cancelled",
                        timestamp = "Just now"
                    )
                )
                player.copy(depositBalance = player.depositBalance + match.entryFee)
            } else player
        }

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                matches = it.matches.map { m ->
                    if (m.id == matchId) m.copy(status = MatchStatus.CANCELLED) else m
                },
                transactions = refundTxs + it.transactions,
                statusMessage = "Cancelled '${match.title}' & refunded ${refundTxs.size} player(s)!"
            )
        }
    }

    fun deleteMatch(matchId: String) {
        _uiState.update { state ->
            state.copy(
                matches = state.matches.filterNot { it.id == matchId },
                participantResults = state.participantResults.filterNot { it.matchId == matchId },
                statusMessage = "Match deleted from LOBBIX."
            )
        }
    }

    // --- ADMIN TAB 3: ROOM ID & PASSWORD CONTROLLER ---

    fun pushRoomDetailsLive(matchId: String, roomId: String, roomPassword: String) {
        if (roomId.isBlank() || roomPassword.isBlank()) {
            showStatus("Enter both Room ID and Password.")
            return
        }
        _uiState.update { state ->
            state.copy(
                matches = state.matches.map { match ->
                    if (match.id == matchId) {
                        match.copy(
                            roomId = roomId.trim(),
                            roomPassword = roomPassword.trim(),
                            isRoomLive = true
                        )
                    } else match
                },
                statusMessage = "Room credentials pushed live to joined players!"
            )
        }
    }

    // --- ADMIN TAB 4: RESULTS, KILLS & PRIZE DISTRIBUTION ---

    fun calculateAndCreditWinnings(
        matchId: String,
        playerId: String,
        kills: Int,
        rank: Int
    ) {
        val state = _uiState.value
        val match = state.matches.firstOrNull { it.id == matchId } ?: return
        val player = state.players.firstOrNull { it.id == playerId } ?: return

        val rankBonus = when (rank) {
            1 -> (match.prizePool * 0.50).toInt()
            2 -> (match.prizePool * 0.25).toInt()
            3 -> (match.prizePool * 0.15).toInt()
            else -> 0
        }
        val killPrize = kills.coerceAtLeast(0) * match.perKillReward
        val totalPrize = rankBonus + killPrize

        val existingResult = state.participantResults.firstOrNull {
            it.matchId == matchId && it.playerId == playerId
        }
        val previousCredited = existingResult?.prizeCredited ?: 0
        val deltaPrize = totalPrize - previousCredited
        val previousKills = existingResult?.kills ?: 0
        val deltaKills = kills.coerceAtLeast(0) - previousKills
        val wasWinner = existingResult?.isResultCredited == true && existingResult.rank == 1
        val isNowWinner = rank == 1
        val deltaWins = when {
            !wasWinner && isNowWinner -> 1
            wasWinner && !isNowWinner -> -1
            else -> 0
        }

        val updatedPlayer = player.copy(
            winningBalance = (player.winningBalance + deltaPrize).coerceAtLeast(0),
            totalEarnings = (player.totalEarnings + deltaPrize).coerceAtLeast(0),
            totalKills = (player.totalKills + deltaKills).coerceAtLeast(0),
            wins = (player.wins + deltaWins).coerceAtLeast(0)
        )

        val updatedResult = (existingResult ?: MatchParticipantResult(
            matchId = matchId,
            playerId = playerId,
            playerName = player.inGameName,
            playerUid = player.freeFireUid
        )).copy(
            kills = kills.coerceAtLeast(0),
            rank = rank.coerceAtLeast(0),
            prizeCredited = totalPrize,
            isResultCredited = true
        )

        val updatedResultsList = if (existingResult != null) {
            state.participantResults.map {
                if (it.matchId == matchId && it.playerId == playerId) updatedResult else it
            }
        } else {
            listOf(updatedResult) + state.participantResults
        }

        val pointsGain = (kills.coerceAtLeast(0) * 20) + when (rank) {
            1 -> 150
            2 -> 90
            3 -> 50
            else -> 15
        }
        val existingLb = state.leaderboardEntries.firstOrNull {
            it.playerId == playerId && it.game == match.game
        }
        val updatedLeaderboard = if (existingLb != null) {
            state.leaderboardEntries.map { entry ->
                if (entry.playerId == playerId && entry.game == match.game) {
                    entry.copy(
                        matches = entry.matches + if (existingResult?.isResultCredited == true) 0 else 1,
                        killsOrWins = entry.killsOrWins + if (match.game == GameType.FREE_FIRE) deltaKills.coerceAtLeast(0) else maxOf(deltaWins, 0),
                        weeklyPoints = entry.weeklyPoints + if (existingResult?.isResultCredited == true) 0 else pointsGain,
                        allTimePoints = entry.allTimePoints + if (existingResult?.isResultCredited == true) 0 else pointsGain
                    )
                } else entry
            }
        } else {
            listOf(
                LeaderboardEntry(
                    playerId = player.id,
                    playerName = player.inGameName,
                    squadTag = "[LOBX]",
                    game = match.game,
                    matches = 1,
                    killsOrWins = if (match.game == GameType.FREE_FIRE) kills else if (rank == 1) 1 else 0,
                    weeklyPoints = pointsGain,
                    allTimePoints = pointsGain
                )
            ) + state.leaderboardEntries
        }

        val newTx = if (deltaPrize != 0) {
            WalletTransaction(
                id = "tx_${UUID.randomUUID().toString().take(6)}",
                playerId = player.id,
                type = TransactionType.WINNING,
                amount = kotlin.math.abs(deltaPrize),
                isCredit = deltaPrize > 0,
                description = "Prize: ${match.title} (Rank #$rank, $kills Kills)",
                timestamp = "Just now"
            )
        } else null

        _uiState.update { current ->
            current.copy(
                players = current.players.map { if (it.id == playerId) updatedPlayer else it },
                participantResults = updatedResultsList,
                leaderboardEntries = updatedLeaderboard,
                transactions = if (newTx != null) listOf(newTx) + current.transactions else current.transactions,
                statusMessage = "Credited ₹$totalPrize to ${player.inGameName} & updated Leaderboard!"
            )
        }
    }

    // --- ADMIN TAB 5: USER & WALLET CONTROL ---

    fun adjustPlayerWallet(playerId: String, amount: Int, isCredit: Boolean, reason: String) {
        if (amount <= 0) {
            showStatus("Enter a valid coin amount.")
            return
        }
        val state = _uiState.value
        val player = state.players.firstOrNull { it.id == playerId } ?: return
        val updatedPlayer = if (isCredit) {
            player.copy(depositBalance = player.depositBalance + amount)
        } else {
            val fromDep = minOf(player.depositBalance, amount)
            val rem = amount - fromDep
            val fromWin = minOf(player.winningBalance, rem)
            player.copy(
                depositBalance = player.depositBalance - fromDep,
                winningBalance = player.winningBalance - fromWin
            )
        }
        val tx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            playerId = playerId,
            type = TransactionType.ADMIN_ADJUST,
            amount = amount,
            isCredit = isCredit,
            description = reason.trim().ifBlank {
                if (isCredit) "Admin Bonus Credit" else "Admin Deduction"
            },
            timestamp = "Just now"
        )
        _uiState.update {
            it.copy(
                players = it.players.map { p -> if (p.id == playerId) updatedPlayer else p },
                transactions = listOf(tx) + it.transactions,
                statusMessage = "${if (isCredit) "Added" else "Deducted"} ₹$amount for ${player.inGameName}"
            )
        }
    }

    fun toggleBanPlayer(playerId: String) {
        _uiState.update { state ->
            var targetName = ""
            var nowBanned = false
            val updated = state.players.map {
                if (it.id == playerId) {
                    targetName = it.inGameName
                    nowBanned = !it.isBanned
                    it.copy(isBanned = nowBanned)
                } else it
            }
            state.copy(
                players = updated,
                statusMessage = if (nowBanned) "Banned $targetName" else "Unbanned $targetName"
            )
        }
    }

    // --- ADMIN TAB 6: WITHDRAWALS & SUPPORT ---

    fun approveWithdrawal(requestId: String) {
        val req = _uiState.value.withdrawalRequests.firstOrNull { it.id == requestId } ?: return
        if (req.status != WithdrawalStatus.PENDING) return
        _uiState.update { state ->
            state.copy(
                withdrawalRequests = state.withdrawalRequests.map {
                    if (it.id == requestId) it.copy(status = WithdrawalStatus.APPROVED) else it
                },
                statusMessage = "Approved ₹${req.amount} payout to ${req.playerName}!"
            )
        }
    }

    fun rejectWithdrawal(requestId: String) {
        val state = _uiState.value
        val req = state.withdrawalRequests.firstOrNull { it.id == requestId } ?: return
        if (req.status != WithdrawalStatus.PENDING) return
        val refundTx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            playerId = req.playerId,
            type = TransactionType.REFUND,
            amount = req.amount,
            isCredit = true,
            description = "Withdrawal Rejected & Refunded (${req.upiId})",
            timestamp = "Just now"
        )
        _uiState.update { current ->
            current.copy(
                players = current.players.map {
                    if (it.id == req.playerId) it.copy(winningBalance = it.winningBalance + req.amount) else it
                },
                withdrawalRequests = current.withdrawalRequests.map {
                    if (it.id == requestId) it.copy(status = WithdrawalStatus.REJECTED) else it
                },
                transactions = listOf(refundTx) + current.transactions,
                statusMessage = "Rejected & auto-refunded ₹${req.amount} to ${req.playerName}."
            )
        }
    }

    fun replyAndResolveTicket(ticketId: String, reply: String) {
        if (reply.isBlank()) {
            showStatus("Enter a reply before resolving.")
            return
        }
        _uiState.update { state ->
            state.copy(
                supportTickets = state.supportTickets.map {
                    if (it.id == ticketId) {
                        it.copy(adminReply = reply.trim(), isResolved = true)
                    } else it
                },
                statusMessage = "Ticket resolved & reply sent to player!"
            )
        }
    }
}
