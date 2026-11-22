package com.example.model

enum class PlayerNavTab(val label: String) {
    HOME("Home"),
    MY_MATCHES("My Matches"),
    LEADERBOARD("Leaderboard"),
    WALLET("Wallet"),
    PROFILE("Profile")
}

enum class AdminTab(val label: String) {
    OVERVIEW("Overview"),
    MATCHES("Matches"),
    ROOMS("Room IDs"),
    RESULTS("Results"),
    USERS("Users"),
    QUEUE("Queue & Help")
}

enum class GameType(val displayName: String) {
    ALL("All Games"),
    FREE_FIRE("Free Fire"),
    LUDO("Ludo"),
    CARROM("Carrom")
}

enum class TeamFormat(val displayName: String) {
    SOLO("Solo"),
    DUO("Duo"),
    SQUAD("Squad")
}

enum class MatchStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    LIVE("Live"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class TransactionType(val displayName: String) {
    DEPOSIT("Deposit"),
    WINNING("Winning"),
    WITHDRAWAL("Withdrawal"),
    ENTRY_FEE("Match Entry"),
    REFUND("Refund"),
    ADMIN_ADJUST("Admin Credit")
}

enum class WithdrawalStatus(val displayName: String) {
    PENDING("Pending"),
    APPROVED("Paid"),
    REJECTED("Rejected")
}

enum class LeaderboardTimeframe(val displayName: String) {
    WEEKLY("Weekly"),
    ALL_TIME("All-Time")
}

enum class WalletFilter(val displayName: String) {
    ALL("All"),
    DEPOSITS("Deposits"),
    WINNINGS("Winnings"),
    WITHDRAWALS("Withdrawals")
}

data class TournamentMatch(
    val id: String,
    val game: GameType,
    val mode: String,
    val format: TeamFormat,
    val title: String,
    val dateTime: String,
    val mapOrRules: String,
    val entryFee: Int,
    val prizePool: Int,
    val perKillReward: Int,
    val maxSlots: Int,
    val joinedPlayerIds: List<String>,
    val status: MatchStatus,
    val roomId: String = "",
    val roomPassword: String = "",
    val isRoomLive: Boolean = false,
    val prizeBreakdown: List<String> = emptyList()
) {
    val filledSlots: Int get() = joinedPlayerIds.size.coerceAtMost(maxSlots)
    val availableSlots: Int get() = (maxSlots - filledSlots).coerceAtLeast(0)
}

data class MatchParticipantResult(
    val matchId: String,
    val playerId: String,
    val playerName: String,
    val playerUid: String,
    val screenshotUri: String? = null,
    val screenshotLabel: String? = null,
    val submittedAt: String? = null,
    val kills: Int = 0,
    val rank: Int = 0,
    val prizeCredited: Int = 0,
    val isResultCredited: Boolean = false
)

data class PlayerAccount(
    val id: String,
    val inGameName: String,
    val emailOrMobile: String,
    val freeFireUid: String,
    val ludoId: String,
    val carromId: String,
    val depositBalance: Int,
    val winningBalance: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val totalKills: Int,
    val totalEarnings: Int,
    val isBanned: Boolean = false,
    val referralCode: String
) {
    val totalBalance: Int get() = depositBalance + winningBalance
}

data class WalletTransaction(
    val id: String,
    val playerId: String,
    val type: TransactionType,
    val amount: Int,
    val isCredit: Boolean,
    val description: String,
    val timestamp: String,
    val statusLabel: String = "Completed"
)

data class WithdrawalRequest(
    val id: String,
    val playerId: String,
    val playerName: String,
    val amount: Int,
    val upiId: String,
    val requestedAt: String,
    val status: WithdrawalStatus = WithdrawalStatus.PENDING
)

data class SupportTicket(
    val id: String,
    val playerId: String,
    val playerName: String,
    val subject: String,
    val message: String,
    val createdAt: String,
    val adminReply: String? = null,
    val isResolved: Boolean = false
)

data class LeaderboardEntry(
    val playerId: String,
    val playerName: String,
    val squadTag: String,
    val game: GameType,
    val matches: Int,
    val killsOrWins: Int,
    val weeklyPoints: Int,
    val allTimePoints: Int
)

data class AppAnnouncement(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

data class GlobalAppConfig(
    val maintenanceMode: Boolean = false,
    val withdrawalsEnabled: Boolean = true,
    val bannerNotice: String = "Season 4 Crimson Cup is LIVE! Top 3 Squads win bonus ₹5,000 Arena Bounty."
)

object GameCatalog {
    fun modesFor(game: GameType): List<String> = when (game) {
        GameType.ALL -> emptyList()
        GameType.FREE_FIRE -> listOf("All Modes", "Battle Royale", "Clash Squad", "Lone Wolf")
        GameType.LUDO -> listOf("All Modes", "1v1 Classic", "Quick Mode", "4-Player")
        GameType.CARROM -> listOf("All Modes", "1v1 Freestyle", "Classic Board")
    }

    fun formatsFor(game: GameType): List<TeamFormat> = when (game) {
        GameType.FREE_FIRE -> listOf(TeamFormat.SOLO, TeamFormat.DUO, TeamFormat.SQUAD)
        GameType.LUDO -> listOf(TeamFormat.SOLO, TeamFormat.DUO, TeamFormat.SQUAD)
        GameType.CARROM -> listOf(TeamFormat.SOLO, TeamFormat.DUO)
        GameType.ALL -> emptyList()
    }
}
