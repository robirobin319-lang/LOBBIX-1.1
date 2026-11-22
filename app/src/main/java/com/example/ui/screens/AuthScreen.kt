package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LobbixBlack
import com.example.ui.theme.LobbixBorder
import com.example.ui.theme.LobbixCrimson
import com.example.ui.theme.LobbixCrimsonBright
import com.example.ui.theme.LobbixSilver
import com.example.ui.theme.LobbixSurface
import com.example.ui.theme.LobbixSurfaceElevated
import com.example.ui.theme.LobbixSurfaceHigh
import com.example.ui.theme.LobbixWhite

@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit,
    onCreateAccount: (String, String, String) -> Unit,
    onContinueWithGoogle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCreateAccount by remember { mutableStateOf(false) }
    var inGameName by remember { mutableStateOf("") }
    var emailOrMobile by remember { mutableStateOf("viperx@lobbix.gg") }
    var password by remember { mutableStateOf("lobbix123") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        LobbixCrimson.copy(alpha = 0.22f),
                        LobbixBlack
                    ),
                    radius = 900f
                )
            )
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = LobbixCrimson,
                        spotColor = LobbixCrimson
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, LobbixCrimson.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_lobbix_crest),
                    contentDescription = "LOBBIX Shield Crown Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "LOBBIX",
                style = MaterialTheme.typography.displayLarge,
                color = LobbixWhite,
                letterSpacing = 4.sp
            )
            Text(
                text = "COMPETE. WIN. DOMINATE.",
                style = MaterialTheme.typography.labelLarge,
                color = LobbixCrimsonBright,
                letterSpacing = 2.5.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                color = LobbixSurface,
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, LobbixBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LobbixSurfaceElevated)
                            .padding(4.dp)
                    ) {
                        AuthModeTab(
                            title = "Login",
                            selected = !isCreateAccount,
                            onClick = { isCreateAccount = false },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("auth_tab_login")
                        )
                        AuthModeTab(
                            title = "Create Account",
                            selected = isCreateAccount,
                            onClick = { isCreateAccount = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("auth_tab_signup")
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LobbixCrimson,
                        unfocusedBorderColor = LobbixBorder,
                        focusedLabelColor = LobbixCrimsonBright,
                        unfocusedLabelColor = LobbixSilver,
                        focusedTextColor = LobbixWhite,
                        unfocusedTextColor = LobbixWhite,
                        cursorColor = LobbixCrimsonBright
                    )

                    if (isCreateAccount) {
                        OutlinedTextField(
                            value = inGameName,
                            onValueChange = { inGameName = it },
                            label = { Text("In-Game Name") },
                            leadingIcon = {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = LobbixCrimsonBright)
                            },
                            singleLine = true,
                            colors = fieldColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_ign_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = emailOrMobile,
                        onValueChange = { emailOrMobile = it },
                        label = { Text("Mobile / Email") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = LobbixSilver)
                        },
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = LobbixSilver)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (isCreateAccount) {
                                onCreateAccount(inGameName, emailOrMobile, password)
                            } else {
                                onLogin(emailOrMobile, password)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LobbixCrimson,
                            contentColor = LobbixWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(14.dp, RoundedCornerShape(14.dp), ambientColor = LobbixCrimson, spotColor = LobbixCrimson)
                            .testTag("auth_submit_button")
                    ) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCreateAccount) "Create Account & Claim ₹100" else "Enter Arena",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = LobbixBorder)
                        Text(
                            text = " OR ",
                            style = MaterialTheme.typography.labelSmall,
                            color = LobbixSilver,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = LobbixBorder)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onContinueWithGoogle,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, LobbixBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LobbixSurfaceElevated,
                            contentColor = LobbixWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("continue_google_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(LobbixWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFFDB4437),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleSmall,
                            color = LobbixWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthModeTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) LobbixCrimson else Color.Transparent,
        shape = RoundedCornerShape(9.dp),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) LobbixWhite else LobbixSilver
            )
        }
    }
}
