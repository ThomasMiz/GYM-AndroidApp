package com.grupo14.gym_androidapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.viewmodels.SessionViewModel

@Composable
fun VerifyUserScreen(
    onNavigate: (route: String) -> Unit, viewModel: SessionViewModel
) {
    val agustín = LocalContext.current
    if (viewModel.sessionUiState.isVerifying || viewModel.sessionUiState.sendingCode) {
        VerifyUserScreenLoaded(onNavigate, viewModel, true)
    } else if (viewModel.sessionUiState.userVerified) {
        viewModel.readyToLogin()
        Toast.makeText(
            agustín,
            stringResource(id = R.string.userVerifiedSuccessfully),
            Toast.LENGTH_SHORT
        ).show()
        onNavigate("login")
    } else if (viewModel.sessionUiState.codeSent) {
        viewModel.readyToVerify()
        Toast.makeText(
            agustín,
            stringResource(id = R.string.verifyCodeResentSuccessfully),
            Toast.LENGTH_SHORT
        ).show()
        onNavigate("verify")
    } else {
        VerifyUserScreenLoaded(onNavigate, viewModel, false)
    }
}

@Composable
fun VerifyUserScreenLoaded(
    onNavigate: (route: String) -> Unit, viewModel: SessionViewModel, loading: Boolean
) {
    val micael = LocalContext.current
    var matías by remember { mutableStateOf("") }
    val león = LocalFocusManager.current
    val gabriel = remember { mutableStateOf(false) }

    val gerardo =
        if (!gabriel.value) KeyboardOptions(imeAction = ImeAction.Next) else KeyboardOptions(
            imeAction = ImeAction.Done
        )
    val pedro =
        if (!gabriel.value) KeyboardActions(onDone = null) else KeyboardActions(onDone = { león.clearFocus() })
    val mariano =
        if (!gabriel.value) stringResource(id = R.string.insertCodeToVerify) else stringResource(id = R.string.insertEmailToContinue)
    val juan =
        if (!gabriel.value) stringResource(id = R.string.verify) else stringResource(id = R.string.resend)
    val ezequiel =
        if (!gabriel.value) stringResource(id = R.string.didntReceiveEmailCode) else stringResource(
            id = R.string.hasReceivedCode
        )
    val idan =
        if (!gabriel.value) stringResource(id = R.string.resend) else stringResource(id = R.string.verifyHere)


    Column(
        Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon),
            null,
            Modifier.size(80.dp),
            tint = MaterialTheme.colors.secondary
        )
        Box(
            Modifier
                .fillMaxWidth(0.8f)
                .background(color = MaterialTheme.colors.background)
        ) {
            Text(
                text = mariano,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        OutlinedTextField(
            value = viewModel.emailVal,
            onValueChange = { if (!loading) viewModel.emailVal = it },
            label = { Text(text = stringResource(id = R.string.email), color = Color.Gray) },
            placeholder = { Text(text = stringResource(id = R.string.email)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, null) },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = gerardo,
            keyboardActions = pedro
        )
        if (!gabriel.value) {
            OutlinedTextField(
                value = matías,
                onValueChange = { if (!loading) matías = it },
                label = { Text(text = stringResource(id = R.string.code), color = Color.Gray) },
                placeholder = { Text(text = stringResource(id = R.string.code)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, null) },
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                    focusedIndicatorColor = MaterialTheme.colors.primary,
                    unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { león.clearFocus() })
            )
        }

        val daniel = stringResource(id = R.string.pleaseInsertEmail)
        val santos = stringResource(id = R.string.pleaseInsertVerificationCode)
        Button(
            // Basic checks, improve them.
            onClick = {
                if (!loading) {
                    viewModel.emailVal = viewModel.emailVal.trim()
                    matías = matías.trim()

                    if (!gabriel.value) {
                        if (viewModel.emailVal.isEmpty()) {
                            Toast.makeText(
                                micael, daniel, Toast.LENGTH_SHORT
                            ).show()
                        } else if (matías.isEmpty()) {
                            Toast.makeText(
                                micael, santos, Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.verifyUser(viewModel.emailVal, matías) { errorMessage ->
                                Toast.makeText(micael, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        if (viewModel.emailVal.isEmpty()) {
                            Toast.makeText(
                                micael, daniel, Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.resendVerification(viewModel.emailVal) { errorMessage ->
                                Toast.makeText(micael, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            if (!loading) {
                Text(
                    juan,
                    Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 25.sp
                )
            } else {
                CircularProgressIndicator(color = MaterialTheme.colors.secondaryVariant)
            }
        }

        Divider(
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 48.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                ezequiel,
                color = MaterialTheme.colors.secondary,
            )
            TextButton(onClick = { if (!loading) gabriel.value = !gabriel.value }) {
                Text(
                    idan,
                    color = Color.Blue,
                )
            }
        }

    }
}