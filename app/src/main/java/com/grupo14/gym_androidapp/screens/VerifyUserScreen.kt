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
    val context = LocalContext.current
    if (viewModel.sessionUiState.isVerifying || viewModel.sessionUiState.sendingCode) {
        VerifyUserScreenLoaded(onNavigate, viewModel, true)
    } else if (viewModel.sessionUiState.userVerified) {
        viewModel.readyToLogin()
        Toast.makeText(
            context,
            stringResource(id = R.string.userVerifiedSuccessfully),
            Toast.LENGTH_SHORT
        ).show()
        onNavigate("login")
    } else if (viewModel.sessionUiState.codeSent) {
        viewModel.readyToVerify()
        Toast.makeText(
            context,
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
    val context = LocalContext.current

    var codeVal by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val resend = remember { mutableStateOf(false) }

    val keyboardOption =
        if (!resend.value) KeyboardOptions(imeAction = ImeAction.Next) else KeyboardOptions(
            imeAction = ImeAction.Done
        )
    val keyboardAction =
        if (!resend.value) KeyboardActions(onDone = null) else KeyboardActions(onDone = { focusManager.clearFocus() })
    val boxText =
        if (!resend.value) stringResource(id = R.string.insertCodeToVerify) else stringResource(id = R.string.insertEmailToContinue)
    val buttonText =
        if (!resend.value) stringResource(id = R.string.verify) else stringResource(id = R.string.resend)
    val footerText =
        if (!resend.value) stringResource(id = R.string.didntReceiveEmailCode) else stringResource(
            id = R.string.hasReceivedCode
        )
    val footerLink =
        if (!resend.value) stringResource(id = R.string.resend) else stringResource(id = R.string.verifyHere)


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
                text = boxText,
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
            keyboardOptions = keyboardOption,
            keyboardActions = keyboardAction
        )
        if (!resend.value) {
            OutlinedTextField(
                value = codeVal,
                onValueChange = { if (!loading) codeVal = it },
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
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
        }

        val insertEmail = stringResource(id = R.string.pleaseInsertEmail)
        val insertVerificationCode = stringResource(id = R.string.pleaseInsertVerificationCode)
        Button(
            // Basic checks, improve them.
            onClick = {
                if (!loading) {
                    viewModel.emailVal = viewModel.emailVal.trim()
                    codeVal = codeVal.trim()

                    if (!resend.value) {
                        if (viewModel.emailVal.isEmpty()) {
                            Toast.makeText(
                                context, insertEmail, Toast.LENGTH_SHORT
                            ).show()
                        } else if (codeVal.isEmpty()) {
                            Toast.makeText(
                                context, insertVerificationCode, Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.verifyUser(viewModel.emailVal, codeVal) { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        if (viewModel.emailVal.isEmpty()) {
                            Toast.makeText(
                                context, insertEmail, Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.resendVerification(viewModel.emailVal) { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
                    buttonText,
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
                footerText,
                color = MaterialTheme.colors.secondary,
            )
            TextButton(onClick = { if (!loading) resend.value = !resend.value }) {
                Text(
                    footerLink,
                    color = Color.Blue,
                )
            }
        }

    }
}