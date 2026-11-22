package com.example.state

import com.example.model.AdminTab
import com.example.model.AppAnnouncement
import com.example.model.GameType
import com.example.model.GlobalAppConfig
import com.example.model.LeaderboardEntry
import com.example.model.LeaderboardTimeframe
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

data class LobbixUiState(
    val isAuthenticated: Boolean = false,
    val isAdminMode: Boolean = false,
    val activePlayerTab: PlayerNavTab = PlayerNavTab.HOME,
    val activeAdminTab: AdminTab = AdminTab.OVERVIEW,
    val currentPlayerId: String = "p1",
    val selectedGameFilter: GameType = GameType.ALL,
    val selectedSubModeFilter: String = "All Modes",
    val selectedFormatFilter: TeamFormat? = null,
    val myMatchesTab: MatchStatus = MatchStatus.UPCOMING,
    val leaderboardGame: GameType = GameType.FREE_FIRE,
    val leaderboardTimeframe: LeaderboardTimeframe = LeaderboardTimeframe.WEEKLY,
    val walletFilter: WalletFilter = WalletFilter.ALL,
    val appConfig: GlobalAppConfig = GlobalAppConfig(),
    val players: List<PlayerAccount> = LobbixSeedData.players,
    val matches: List<TournamentMatch> = LobbixSeedData.matches,
    val participantResults: List<MatchParticipantResult> = LobbixSeedData.participantResults,
    val transactions: List<WalletTransaction> = LobbixSeedData.transactions,
    val withdrawalRequests: List<WithdrawalRequest> = LobbixSeedData.withdrawalRequests,
    val supportTickets: List<SupportTicket> = LobbixSeedData.supportTickets,
    val leaderboardEntries: List<LeaderboardEntry> = LobbixSeedData.leaderboardEntries,
    val announcements: List<AppAnnouncement> = LobbixSeedData.announcements,
    val statusMessage: String? = null
) {
    val currentPlayer: PlayerAccount
        get() = players.firstOrNull { it.id == currentPlayerId } ?: players.first()

    val unreadAnnouncementsCount: Int
        get() = announcements.count { !it.isRead }

    val pendingWithdrawalsCount: Int
        get() = withdrawalRequests.count { it.status == WithdrawalStatus.PENDING }

    val pendingProofsCount: Int
        get() = participantResults.count { it.screenshotLabel != null && !it.isResultCredited }

    val openTicketsCount: Int
        get() = supportTickets.count { !it.isResolved }
}

object LobbixSeedData {
    val players = listOf(
        PlayerAccount(
            id = "p1",
            inGameName = "ViperX_GG",
            emailOrMobile = "viperx@lobbix.gg",
            freeFireUid = "2489104431",
            ludoId = "LDO-88214",
            carromId = "CRM-44109",
            depositBalance = 450,
            winningBalance = 800,
            matchesPlayed = 28,
            wins = 9,
            totalKills = 114,
            totalEarnings = 6450,
            isBanned = false,
            referralCode = "LOBBIX-VIPER99"
        ),
        PlayerAccount(
            id = "p2",
            inGameName = "ShadowReaper",
            emailOrMobile = "9820411220",
            freeFireUid = "1940228710",
            ludoId = "LDO-55190",
            carromId = "CRM-10293",
            depositBalance = 320,
            winningBalance = 1420,
            matchesPlayed = 42,
            wins = 15,
            totalKills = 186,
            totalEarnings = 11200,
            isBanned = false,
            referralCode = "LOBBIX-REAP77"
        ),
        PlayerAccount(
            id = "p3",
            inGameName = "CrimsonQueen",
            emailOrMobile = "queen@esports.in",
            freeFireUid = "3301984412",
            ludoId = "LDO-77412",
            carromId = "CRM-88210",
            depositBalance = 600,
            winningBalance = 950,
            matchesPlayed = 35,
            wins = 13,
            totalKills = 149,
            totalEarnings = 8900,
            isBanned = false,
            referralCode = "LOBBIX-QUEEN11"
        ),
        PlayerAccount(
            id = "p4",
            inGameName = "ApexRohan",
            emailOrMobile = "9711820045",
            freeFireUid = "4098127734",
            ludoId = "LDO-31904",
            carromId = "CRM-65421",
            depositBalance = 150,
            winningBalance = 410,
            matchesPlayed = 19,
            wins = 4,
            totalKills = 62,
            totalEarnings = 2750,
            isBanned = false,
            referralCode = "LOBBIX-APEX04"
        ),
        PlayerAccount(
            id = "p5",
            inGameName = "BlitzKaran",
            emailOrMobile = "karan.blitz@gmail.com",
            freeFireUid = "5501293847",
            ludoId = "LDO-90112",
            carromId = "CRM-71829",
            depositBalance = 100,
            winningBalance = 260,
            matchesPlayed = 14,
            wins = 3,
            totalKills = 45,
            totalEarnings = 1800,
            isBanned = false,
            referralCode = "LOBBIX-BLITZ5"
        ),
        PlayerAccount(
            id = "p6",
            inGameName = "ToxicSnipe",
            emailOrMobile = "9900112233",
            freeFireUid = "6120938471",
            ludoId = "LDO-11203",
            carromId = "CRM-33910",
            depositBalance = 40,
            winningBalance = 0,
            matchesPlayed = 7,
            wins = 0,
            totalKills = 9,
            totalEarnings = 0,
            isBanned = true,
            referralCode = "LOBBIX-TOX06"
        )
    )

    val matches = listOf(
        TournamentMatch(
            id = "m1",
            game = GameType.FREE_FIRE,
            mode = "Battle Royale",
            format = TeamFormat.SQUAD,
            title = "Crimson Cup Bermuda #402",
            dateTime = "Today, 8:30 PM IST",
            mapOrRules = "Bermuda • No Emulator • Lv.30+ Accounts Only",
            entryFee = 50,
            prizePool = 2000,
            perKillReward = 20,
            maxSlots = 48,
            joinedPlayerIds = listOf("p2", "p3", "p4", "p5") + List(34) { "bot_ff_$it" },
            status = MatchStatus.UPCOMING,
            roomId = "",
            roomPassword = "",
            isRoomLive = false,
            prizeBreakdown = listOf("1st Place: ₹1,000", "2nd Place: ₹500", "3rd Place: ₹300", "Per Kill: ₹20")
        ),
        TournamentMatch(
            id = "m2",
            game = GameType.FREE_FIRE,
            mode = "Clash Squad",
            format = TeamFormat.SQUAD,
            title = "Obsidian 4v4 Knockouts",
            dateTime = "LIVE NOW",
            mapOrRules = "Kalahari CS • Limited Ammo • No Grenades",
            entryFee = 100,
            prizePool = 1500,
            perKillReward = 25,
            maxSlots = 8,
            joinedPlayerIds = listOf("p1", "p2", "p3", "p4", "p5", "bot_cs_1", "bot_cs_2"),
            status = MatchStatus.LIVE,
            roomId = "FF948271",
            roomPassword = "LBX88",
            isRoomLive = true,
            prizeBreakdown = listOf("Winner Squad: ₹1,000", "Runner-Up: ₹300", "MVP Per Kill: ₹25")
        ),
        TournamentMatch(
            id = "m3",
            game = GameType.FREE_FIRE,
            mode = "Lone Wolf",
            format = TeamFormat.SOLO,
            title = "Midnight 1v1 Sniper Duel",
            dateTime = "Today, 10:15 PM IST",
            mapOrRules = "Iron Cage • AWM + Desert Eagle • Best of 5",
            entryFee = 30,
            prizePool = 500,
            perKillReward = 15,
            maxSlots = 16,
            joinedPlayerIds = listOf("p2", "p4", "bot_lw_1", "bot_lw_2", "bot_lw_3", "bot_lw_4"),
            status = MatchStatus.UPCOMING,
            prizeBreakdown = listOf("Champion: ₹350", "Runner-Up: ₹100", "Per Kill: ₹15")
        ),
        TournamentMatch(
            id = "m4",
            game = GameType.LUDO,
            mode = "1v1 Classic",
            format = TeamFormat.SOLO,
            title = "Ludo King Sprint #19",
            dateTime = "Today, 9:00 PM IST",
            mapOrRules = "Classic 4-Token • 60s Turn Timer • Screenshot Mandatory",
            entryFee = 40,
            prizePool = 75,
            perKillReward = 0,
            maxSlots = 2,
            joinedPlayerIds = listOf("p3"),
            status = MatchStatus.UPCOMING,
            prizeBreakdown = listOf("Winner Takes All: ₹75")
        ),
        TournamentMatch(
            id = "m5",
            game = GameType.LUDO,
            mode = "4-Player",
            format = TeamFormat.SQUAD,
            title = "Royal Dice 4P Showdown",
            dateTime = "Completed • 6:00 PM",
            mapOrRules = "Quick Mode • 2 Tokens Home Wins • Verified Screenshot",
            entryFee = 50,
            prizePool = 180,
            perKillReward = 0,
            maxSlots = 4,
            joinedPlayerIds = listOf("p1", "p2", "p3", "p4"),
            status = MatchStatus.COMPLETED,
            roomId = "LDO-77120",
            roomPassword = "DICE4",
            isRoomLive = true,
            prizeBreakdown = listOf("1st Place: ₹120", "2nd Place: ₹60")
        ),
        TournamentMatch(
            id = "m6",
            game = GameType.CARROM,
            mode = "1v1 Freestyle",
            format = TeamFormat.SOLO,
            title = "Carrom Striker Pro #08",
            dateTime = "Today, 9:45 PM IST",
            mapOrRules = "29-Point Freestyle • Queen Cover Required • Fair Play",
            entryFee = 60,
            prizePool = 110,
            perKillReward = 0,
            maxSlots = 2,
            joinedPlayerIds = listOf("p1"),
            status = MatchStatus.UPCOMING,
            roomId = "CRM-50219",
            roomPassword = "STRK9",
            isRoomLive = true,
            prizeBreakdown = listOf("Winner: ₹110")
        ),
        TournamentMatch(
            id = "m7",
            game = GameType.CARROM,
            mode = "Classic Board",
            format = TeamFormat.DUO,
            title = "Obsidian 2v2 Carrom Masters",
            dateTime = "Completed • Yesterday",
            mapOrRules = "Classic Board • Black & White • Best of 3 Boards",
            entryFee = 100,
            prizePool = 360,
            perKillReward = 0,
            maxSlots = 4,
            joinedPlayerIds = listOf("p1", "p2", "p3", "p5"),
            status = MatchStatus.COMPLETED,
            roomId = "CRM-11820",
            roomPassword = "BOARD",
            isRoomLive = true,
            prizeBreakdown = listOf("1st Duo: ₹260", "2nd Duo: ₹100")
        )
    )

    val participantResults = listOf(
        MatchParticipantResult(
            matchId = "m2",
            playerId = "p1",
            playerName = "ViperX_GG",
            playerUid = "2489104431"
        ),
        MatchParticipantResult(
            matchId = "m2",
            playerId = "p2",
            playerName = "ShadowReaper",
            playerUid = "1940228710"
        ),
        MatchParticipantResult(
            matchId = "m2",
            playerId = "p3",
            playerName = "CrimsonQueen",
            playerUid = "3301984412"
        ),
        MatchParticipantResult(
            matchId = "m5",
            playerId = "p1",
            playerName = "ViperX_GG",
            playerUid = "LDO-88214",
            screenshotLabel = "ludo_win_round_proof.png",
            submittedAt = "6:24 PM"
        ),
        MatchParticipantResult(
            matchId = "m5",
            playerId = "p2",
            playerName = "ShadowReaper",
            playerUid = "LDO-55190",
            screenshotLabel = "ludo_runnerup_board.png",
            submittedAt = "6:26 PM"
        ),
        MatchParticipantResult(
            matchId = "m7",
            playerId = "p1",
            playerName = "ViperX_GG",
            playerUid = "CRM-44109",
            screenshotLabel = "carrom_victory_board.jpg",
            submittedAt = "Yesterday",
            kills = 0,
            rank = 1,
            prizeCredited = 180,
            isResultCredited = true
        ),
        MatchParticipantResult(
            matchId = "m7",
            playerId = "p2",
            playerName = "ShadowReaper",
            playerUid = "CRM-10293",
            screenshotLabel = "carrom_board_final.jpg",
            submittedAt = "Yesterday",
            kills = 0,
            rank = 2,
            prizeCredited = 90,
            isResultCredited = true
        )
    )

    val transactions = listOf(
        WalletTransaction(
            id = "tx1",
            playerId = "p1",
            type = TransactionType.WINNING,
            amount = 250,
            isCredit = true,
            description = "Prize: Bermuda Blitz #398 (Rank #2 + 4 Kills)",
            timestamp = "Today, 3:10 PM"
        ),
        WalletTransaction(
            id = "tx2",
            playerId = "p1",
            type = TransactionType.ENTRY_FEE,
            amount = 100,
            isCredit = false,
            description = "Joined Obsidian 4v4 Knockouts",
            timestamp = "Today, 5:45 PM"
        ),
        WalletTransaction(
            id = "tx3",
            playerId = "p1",
            type = TransactionType.DEPOSIT,
            amount = 500,
            isCredit = true,
            description = "UPI Deposit (GPay ••8821)",
            timestamp = "Yesterday, 8:20 PM"
        ),
        WalletTransaction(
            id = "tx4",
            playerId = "p1",
            type = TransactionType.WITHDRAWAL,
            amount = 300,
            isCredit = false,
            description = "UPI Payout to viperx@okicici",
            timestamp = "2 days ago",
            statusLabel = "Paid"
        ),
        WalletTransaction(
            id = "tx5",
            playerId = "p2",
            type = TransactionType.WINNING,
            amount = 650,
            isCredit = true,
            description = "Prize: Solo Rush #389 (Rank #1)",
            timestamp = "Yesterday, 9:00 PM"
        )
    )

    val withdrawalRequests = listOf(
        WithdrawalRequest(
            id = "w1",
            playerId = "p2",
            playerName = "ShadowReaper",
            amount = 500,
            upiId = "shadowreaper@ybl",
            requestedAt = "18 mins ago",
            status = WithdrawalStatus.PENDING
        ),
        WithdrawalRequest(
            id = "w2",
            playerId = "p3",
            playerName = "CrimsonQueen",
            amount = 350,
            upiId = "queen.ff@okaxis",
            requestedAt = "42 mins ago",
            status = WithdrawalStatus.PENDING
        ),
        WithdrawalRequest(
            id = "w3",
            playerId = "p1",
            playerName = "ViperX_GG",
            amount = 300,
            upiId = "viperx@okicici",
            requestedAt = "2 days ago",
            status = WithdrawalStatus.APPROVED
        )
    )

    val supportTickets = listOf(
        SupportTicket(
            id = "t1",
            playerId = "p4",
            playerName = "ApexRohan",
            subject = "Kill count verification for Match #391",
            message = "I scored 6 kills in Bermuda Match #391 but only 4 were credited. Attached lobby screenshot.",
            createdAt = "35 mins ago",
            adminReply = null,
            isResolved = false
        ),
        SupportTicket(
            id = "t2",
            playerId = "p1",
            playerName = "ViperX_GG",
            subject = "Room ID timing question",
            message = "How many minutes before start time are Clash Squad Room IDs pushed?",
            createdAt = "Yesterday",
            adminReply = "Room ID & Password go live 15 minutes before match start in My Matches!",
            isResolved = true
        )
    )

    val leaderboardEntries = listOf(
        // Free Fire
        LeaderboardEntry("p2", "ShadowReaper", "[APEX]", GameType.FREE_FIRE, 42, 186, 1480, 6920),
        LeaderboardEntry("p3", "CrimsonQueen", "[VALK]", GameType.FREE_FIRE, 35, 149, 1340, 5810),
        LeaderboardEntry("p1", "ViperX_GG", "[LOBX]", GameType.FREE_FIRE, 28, 114, 1190, 4950),
        LeaderboardEntry("p4", "ApexRohan", "[NOVA]", GameType.FREE_FIRE, 19, 62, 820, 3110),
        LeaderboardEntry("p5", "BlitzKaran", "[RAGE]", GameType.FREE_FIRE, 14, 45, 640, 2280),
        // Ludo
        LeaderboardEntry("p3", "CrimsonQueen", "[ROYAL]", GameType.LUDO, 24, 18, 960, 4120),
        LeaderboardEntry("p1", "ViperX_GG", "[LOBX]", GameType.LUDO, 18, 13, 880, 3540),
        LeaderboardEntry("p2", "ShadowReaper", "[APEX]", GameType.LUDO, 15, 10, 740, 2980),
        LeaderboardEntry("p4", "ApexRohan", "[NOVA]", GameType.LUDO, 12, 6, 510, 1890),
        // Carrom
        LeaderboardEntry("p1", "ViperX_GG", "[LOBX]", GameType.CARROM, 16, 12, 920, 3760),
        LeaderboardEntry("p5", "BlitzKaran", "[RAGE]", GameType.CARROM, 14, 9, 810, 3120),
        LeaderboardEntry("p2", "ShadowReaper", "[APEX]", GameType.CARROM, 11, 7, 690, 2540),
        LeaderboardEntry("p3", "CrimsonQueen", "[VALK]", GameType.CARROM, 9, 5, 530, 1940)
    )

    val announcements = listOf(
        AppAnnouncement(
            id = "a1",
            title = "Season 4 Crimson Cup",
            message = "Top 3 Squads this week win an extra ₹5,000 bonus bounty. Keep uploading match proofs!",
            timestamp = "10m ago",
            isRead = false
        ),
        AppAnnouncement(
            id = "a2",
            title = "Instant UPI Payouts Active",
            message = "Withdrawals above ₹50 are processed within 15 minutes via UPI.",
            timestamp = "2h ago",
            isRead = false
        )
    )
}
