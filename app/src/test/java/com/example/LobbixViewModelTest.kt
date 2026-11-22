package com.example

import com.example.model.GameType
import com.example.model.MatchStatus
import com.example.model.TeamFormat
import com.example.model.WithdrawalStatus
import com.example.state.LobbixViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LobbixViewModelTest {

    @Test
    fun player_join_match_deducts_balance_and_updates_slots() {
        val vm = LobbixViewModel()
        vm.continueWithGoogle()
        val initialBalance = vm.uiState.value.currentPlayer.totalBalance
        val matchBefore = vm.uiState.value.matches.first { it.id == "m1" }
        val slotsBefore = matchBefore.availableSlots

        vm.joinMatch("m1")

        val stateAfter = vm.uiState.value
        assertEquals(initialBalance - matchBefore.entryFee, stateAfter.currentPlayer.totalBalance)
        val matchAfter = stateAfter.matches.first { it.id == "m1" }
        assertEquals(slotsBefore - 1, matchAfter.availableSlots)
        assertTrue(matchAfter.joinedPlayerIds.contains(stateAfter.currentPlayer.id))
    }

    @Test
    fun admin_create_match_and_push_room_updates_player_view() {
        val vm = LobbixViewModel()
        vm.continueWithGoogle()

        vm.createTournament(
            game = GameType.FREE_FIRE,
            mode = "Battle Royale",
            format = TeamFormat.SQUAD,
            title = "Test Cup #99",
            entryFee = 25,
            prizePool = 1000,
            perKillReward = 15,
            maxSlots = 48,
            dateTime = "Tonight 11 PM",
            mapOrRules = "Bermuda"
        )

        val created = vm.uiState.value.matches.first { it.title == "Test Cup #99" }
        vm.pushRoomDetailsLive(created.id, "RM9988", "PASS99")

        val updated = vm.uiState.value.matches.first { it.id == created.id }
        assertTrue(updated.isRoomLive)
        assertEquals("RM9988", updated.roomId)
        assertEquals("PASS99", updated.roomPassword)
    }

    @Test
    fun withdrawal_and_admin_reject_auto_refunds_player_wallet() {
        val vm = LobbixViewModel()
        vm.continueWithGoogle()
        val startBalance = vm.uiState.value.currentPlayer.totalBalance

        vm.requestWithdrawal(200, "viperx@okicici")
        assertEquals(startBalance - 200, vm.uiState.value.currentPlayer.totalBalance)

        val pendingReq = vm.uiState.value.withdrawalRequests.first {
            it.playerId == vm.uiState.value.currentPlayer.id && it.status == WithdrawalStatus.PENDING
        }
        vm.rejectWithdrawal(pendingReq.id)

        assertEquals(startBalance, vm.uiState.value.currentPlayer.totalBalance)
        assertEquals(
            WithdrawalStatus.REJECTED,
            vm.uiState.value.withdrawalRequests.first { it.id == pendingReq.id }.status
        )
    }

    @Test
    fun admin_credit_winnings_updates_wallet_and_leaderboard() {
        val vm = LobbixViewModel()
        vm.continueWithGoogle()
        val startWinning = vm.uiState.value.currentPlayer.winningBalance

        vm.calculateAndCreditWinnings(matchId = "m5", playerId = "p1", kills = 0, rank = 1)

        val playerAfter = vm.uiState.value.currentPlayer
        assertTrue(playerAfter.winningBalance > startWinning)
        val result = vm.uiState.value.participantResults.first { it.matchId == "m5" && it.playerId == "p1" }
        assertTrue(result.isResultCredited)
    }

    @Test
    fun admin_publish_notice_and_support_ticket_resolution() {
        val vm = LobbixViewModel()
        vm.continueWithGoogle()
        vm.publishNotice("Flash Bonus", "₹500 extra on Clash Squad")
        assertTrue(vm.uiState.value.appConfig.bannerNotice.contains("Flash Bonus"))

        vm.raiseSupportTicket("Missing Kill", "Check match m2")
        val ticket = vm.uiState.value.supportTickets.first { it.subject == "Missing Kill" }
        assertFalse(ticket.isResolved)

        vm.replyAndResolveTicket(ticket.id, "Credited manually!")
        val resolved = vm.uiState.value.supportTickets.first { it.id == ticket.id }
        assertTrue(resolved.isResolved)
        assertEquals("Credited manually!", resolved.adminReply)
    }
}
