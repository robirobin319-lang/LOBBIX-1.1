package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameType
import com.example.model.MatchStatus
import com.example.model.TournamentMatch
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LobbixBlack
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixCrimsonContainer
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
fun LobbixTopHeader(
    walletBalance: Int,
    unreadCount: Int,
    isAdminMode: Boolean,
    onWalletClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onToggleAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LobbixBlack.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, LobbixBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_lobbix_crest),
                    contentDescription = "LOBBIX Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .border(1.dp, LobbixCrimson.copy(alpha = 0.6f), RoundedCornerShape(9.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "LOBBIX",
                        style = MaterialTheme.typography.titleLarge,
                        color = LobbixWhite,
                        maxLines = 1
                    )
                    Text(
                        text = if (isAdminMode) "OWNER CONSOLE" else "ARENA HUB",
                        style = MaterialTheme.typography.labelSmall,
                        color = LobbixCrimsonBright,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = LobbixSurfaceElevated,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, LobbixCrimson.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .testTag("header_wallet_pill")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onWalletClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = "Wallet Balance",
                            tint = LobbixGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "₹$walletBalance",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = JetBrainsMonoFamily,
                            color = LobbixWhite,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(modifier = Modifier.testTag("header_notifications_button")) {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Announcements",
                            tint = LobbixSilver
                        )
                    }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(LobbixCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unreadCount.toString(),
                                color = LobbixWhite,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    color = if (isAdminMode) LobbixCrimson else LobbixSurfaceHigh,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (isAdminMode) LobbixCrimsonBright else LobbixCrimson
                    ),
                    modifier = Modifier
                        .testTag("mode_switch_button")
                        .minimumInteractiveComponentSize()
                        .shadow(
                            elevation = if (isAdminMode) 12.dp else 4.dp,
                            shape = RoundedCornerShape(50),
                            ambientColor = LobbixCrimson,
                            spotColor = LobbixCrimson
                        )
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onToggleAdmin)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Filled.SportsEsports else Icons.Filled.AdminPanelSettings,
                            contentDescription = "Switch View Mode",
                            tint = LobbixWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAdminMode) "Player View" else "Owner Admin",
                            style = MaterialTheme.typography.labelMedium,
                            color = LobbixWhite,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentCard(
    match: TournamentMatch,
    isJoined: Boolean,
    onJoinClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerRes = when (match.game) {
        GameType.FREE_FIRE, GameType.ALL -> R.drawable.img_banner_freefire
        GameType.LUDO -> R.drawable.img_banner_ludo
        GameType.CARROM -> R.drawable.img_banner_carrom
    }
    val progress = if (match.maxSlots > 0) {
        match.filledSlots.toFloat() / match.maxSlots.toFloat()
    } else 0f

    Surface(
        color = LobbixSurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            if (match.status == MatchStatus.LIVE) LobbixCrimson.copy(alpha = 0.7f) else LobbixBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = LobbixCrimson.copy(alpha = 0.2f),
                spotColor = LobbixCrimson.copy(alpha = 0.25f)
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = "${match.game.displayName} Arena Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    LobbixBlack.copy(alpha = 0.35f),
                                    LobbixSurface
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(
                            text = match.game.displayName.uppercase(),
                            containerColor = LobbixCrimson,
                            contentColor = LobbixWhite
                        )
                        StatusBadge(
                            text = "${match.mode} • ${match.format.displayName}",
                            containerColor = LobbixBlack.copy(alpha = 0.78f),
                            contentColor = LobbixSilver
                        )
                    }

                    MatchStatusPill(status = match.status)
                }

                Text(
                    text = match.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = LobbixWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = LobbixCrimsonBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = match.dateTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = LobbixWhite
                    )
                    Text(
                        text = "•",
                        color = LobbixMuted
                    )
                    Text(
                        text = match.mapOrRules,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LobbixSilver,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LobbixSurfaceElevated)
                        .border(1.dp, LobbixBorder, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricColumn(
                        label = "PRIZE POOL",
                        value = "₹${match.prizePool}",
                        valueColor = LobbixGold
                    )
                    MetricColumn(
                        label = "ENTRY FEE",
                        value = "₹${match.entryFee}",
                        valueColor = LobbixWhite
                    )
                    MetricColumn(
                        label = if (match.perKillReward > 0) "PER KILL" else "MODE",
                        value = if (match.perKillReward > 0) "₹${match.perKillReward}" else match.format.displayName,
                        valueColor = LobbixCrimsonBright
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${match.filledSlots}/${match.maxSlots} Slots Filled",
                        style = MaterialTheme.typography.labelMedium,
                        color = LobbixSilver
                    )
                    Text(
                        text = if (match.availableSlots > 0) "${match.availableSlots} spots left" else "FULL",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (match.availableSlots <= 4) LobbixCrimsonBright else LobbixEmerald
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(LobbixSurfaceHigh)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0.05f, 1f))
                            .height(7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LobbixCrimson, LobbixCrimsonBright)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDetailsClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LobbixBorder),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("details_button_${match.id}")
                    ) {
                        Text(
                            text = "Details & Rules",
                            style = MaterialTheme.typography.labelLarge,
                            color = LobbixSilver,
                            maxLines = 1
                        )
                    }

                    val canJoin = !isJoined &&
                        match.availableSlots > 0 &&
                        (match.status == MatchStatus.UPCOMING || match.status == MatchStatus.LIVE)

                    Button(
                        onClick = onJoinClick,
                        enabled = canJoin,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LobbixCrimson,
                            contentColor = LobbixWhite,
                            disabledContainerColor = if (isJoined) LobbixEmeraldContainer else LobbixSurfaceHigh,
                            disabledContentColor = if (isJoined) LobbixEmerald else LobbixMuted
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("join_button_${match.id}")
                    ) {
                        if (isJoined) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Joined",
                                style = MaterialTheme.typography.labelLarge
                            )
                        } else {
                            Text(
                                text = when {
                                    match.status == MatchStatus.COMPLETED -> "Completed"
                                    match.status == MatchStatus.CANCELLED -> "Cancelled"
                                    match.availableSlots <= 0 -> "Slots Full"
                                    else -> "Join • ₹${match.entryFee}"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LobbixMuted,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = JetBrainsMonoFamily,
            color = valueColor
        )
    }
}

@Composable
fun MatchStatusPill(status: MatchStatus) {
    val (bg, fg) = when (status) {
        MatchStatus.LIVE -> LobbixCrimsonContainer to LobbixCrimsonBright
        MatchStatus.UPCOMING -> LobbixSurfaceHigh to LobbixWhite
        MatchStatus.COMPLETED -> LobbixEmeraldContainer to LobbixEmerald
        MatchStatus.CANCELLED -> Color(0xFF2B1B1D) to LobbixMuted
    }
    StatusBadge(
        text = if (status == MatchStatus.LIVE) "● LIVE" else status.displayName.uppercase(),
        containerColor = bg,
        contentColor = fg
    )
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1
        )
    }
}

@Composable
fun UpiQrMockCard(
    amount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LobbixWhite,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(modifier = Modifier.size(116.dp)) {
                val grid = 11
                val cell = size.width / grid
                val pattern = listOf(
                    "11111011111",
                    "10001010001",
                    "10101010101",
                    "10001010001",
                    "11111011111",
                    "00000101010",
                    "11111010110",
                    "10001001001",
                    "10101011011",
                    "10001010100",
                    "11111011011"
                )
                pattern.forEachIndexed { r, row ->
                    row.forEachIndexed { c, ch ->
                        if (ch == '1') {
                            val isAccent = (r in 2..2 && c in 2..2) || (r == 5 && c == 5)
                            drawRect(
                                color = if (isAccent) LobbixCrimson else LobbixBlack,
                                topLeft = Offset(c * cell, r * cell),
                                size = Size(cell - 1f, cell - 1f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "UPI: lobbix.arena@okicici",
                style = MaterialTheme.typography.labelSmall,
                color = LobbixBlack,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Scan & Pay ₹$amount",
                style = MaterialTheme.typography.labelMedium,
                color = LobbixCrimson
            )
        }
    }
}

@Composable
fun LobbixEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LobbixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LobbixBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = LobbixCrimson,
                modifier = Modifier.size(38.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = LobbixWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = LobbixSilver
            )
        }
    }
}
