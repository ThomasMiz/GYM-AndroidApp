package com.grupo14.gym_androidapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.viewmodels.SessionViewModel

@Composable
fun RegisterScreen(
    onNavigate: (route: String) -> Unit, viewModel: SessionViewModel
) {
    val context = LocalContext.current
    if (viewModel.sessionUiState.isRegistering) {
        RegisterScreenLoaded(onNavigate, viewModel, true)
    } else if (viewModel.sessionUiState.isRegistered) {
        viewModel.readyToVerify()
        Toast.makeText(
            context,
            stringResource(id = R.string.userRegisteredSuccessfully),
            Toast.LENGTH_SHORT
        ).show()
        onNavigate("verify")
    } else {
        RegisterScreenLoaded(onNavigate, viewModel, false)
    }
}


@Composable
private fun RegisterScreenLoaded(
    onNavigate: (route: String) -> Unit, viewModel: SessionViewModel, loading: Boolean
) {
    val context = LocalContext.current

    var repPasswordVal by remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }
    val repPasswordVisibility = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        Modifier
            .padding(50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon),
            null,
            Modifier.size(80.dp),
            tint = MaterialTheme.colors.secondary
        )
    }
    Column(
        Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = viewModel.usernameVal,
            onValueChange = { if (!loading) viewModel.usernameVal = it },
            label = { Text(text = stringResource(id = R.string.username), color = Color.Gray) },
            placeholder = { Text(text = stringResource(id = R.string.username)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            leadingIcon = { Icon(imageVector = Icons.Default.Person, null) },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = viewModel.passwordVal,
            onValueChange = { if (!loading) viewModel.passwordVal = it },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility.value = !passwordVisibility.value
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.password_eye),
                        contentDescription = "password",
                        tint = if (passwordVisibility.value) Color.Black else Color.Gray
                    )
                }
            },
            label = { Text(text = stringResource(id = R.string.password), color = Color.Gray) },
            placeholder = { Text(text = stringResource(id = R.string.password)) },
            singleLine = true,
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f),
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, null) },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Password
            ),
        )

        OutlinedTextField(
            value = repPasswordVal,
            onValueChange = { if (!loading) repPasswordVal = it },
            trailingIcon = {
                IconButton(onClick = {
                    repPasswordVisibility.value = !repPasswordVisibility.value
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.password_eye),
                        contentDescription = "password",
                        tint = if (repPasswordVisibility.value) Color.Black else Color.Gray
                    )
                }
            },
            label = { Text(text = stringResource(R.string.repeatPassword), color = Color.Gray) },
            placeholder = { Text(text = stringResource(id = R.string.repeatPassword)) },
            singleLine = true,
            visualTransformation = if (repPasswordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f),
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, null) },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        val pleaseInsertEmail = stringResource(id = R.string.pleaseInsertEmail)
        val pleaseInsertUsername = stringResource(id = R.string.pleaseInsertUsername)
        val pleaseInsertPassword = stringResource(id = R.string.pleaseInsertPassword)
        val passwordsDontMatch = stringResource(id = R.string.passwordsDontMatch)
        Button(
            // Basic checks, improve them.
            onClick = {
                if (!loading) {
                    viewModel.usernameVal = viewModel.usernameVal.trim()
                    viewModel.emailVal = viewModel.emailVal.trim()

                    if (viewModel.emailVal.isEmpty()) {
                        Toast.makeText(
                            context, pleaseInsertEmail, Toast.LENGTH_SHORT
                        ).show()
                    } else if (viewModel.usernameVal.isEmpty()) {
                        Toast.makeText(
                            context, pleaseInsertUsername, Toast.LENGTH_SHORT
                        ).show()
                    } else if (viewModel.passwordVal.isEmpty()) {
                        Toast.makeText(
                            context, pleaseInsertPassword, Toast.LENGTH_SHORT
                        ).show()
                    } else if (!viewModel.passwordVal.equals(repPasswordVal)) {
                        Toast.makeText(context, passwordsDontMatch, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        viewModel.registerNewUser(
                            viewModel.emailVal, viewModel.usernameVal, viewModel.passwordVal
                        ) { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
                    stringResource(id = R.string.continueXD),
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
                stringResource(id = R.string.alreadyHaveAccount),
                color = MaterialTheme.colors.secondary,
            )
            TextButton(onClick = { if (!loading) onNavigate("login") }) {
                Text(
                    stringResource(id = R.string.loginNow),
                    color = Color.Blue,
                )
            }
        }
    }
}