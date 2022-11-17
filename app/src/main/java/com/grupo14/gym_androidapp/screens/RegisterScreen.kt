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
        Toast.makeText(context, "¡Usuario registrado con éxito!", Toast.LENGTH_SHORT).show()
        viewModel.passwordVal = ""
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
            .padding(30.dp)
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
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = viewModel.emailVal,
            onValueChange = { if (!loading) viewModel.emailVal = it },
            label = { Text(text = "Correo electrónico", color = Color.Gray) },
            placeholder = { Text(text = "Correo electrónico") },
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
            label = { Text(text = "Nombre de usuario", color = Color.Gray) },
            placeholder = { Text(text = "Nombre de usuario") },
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
            label = { Text(text = "Contraseña", color = Color.Gray) },
            placeholder = { Text(text = "Contraseña") },
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
            label = { Text(text = "Repetir contraseña", color = Color.Gray) },
            placeholder = { Text(text = "Repetir contraseña") },
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

        Button(
            // Basic checks, improve them.
            onClick = {
                if (!loading) {
                    viewModel.usernameVal = viewModel.usernameVal.trim()
                    viewModel.emailVal = viewModel.emailVal.trim()

                    if (viewModel.emailVal.isEmpty()) {
                        Toast.makeText(
                            context, "Por favor, ingrese un correo electrónico", Toast.LENGTH_SHORT
                        ).show()
                    } else if (viewModel.usernameVal.isEmpty()) {
                        Toast.makeText(
                            context, "Por favor, ingrese un nombre de usuario", Toast.LENGTH_SHORT
                        ).show()
                    } else if (viewModel.passwordVal.isEmpty()) {
                        Toast.makeText(
                            context, "Por favor, ingrese una contraseña", Toast.LENGTH_SHORT
                        ).show()
                    } else if (!viewModel.passwordVal.equals(repPasswordVal)) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT)
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
                    "Continuar",
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
                "¿Ya tienes una cuenta?",
                color = MaterialTheme.colors.secondary,
            )
            TextButton(onClick = { if (!loading) onNavigate("login") }) {
                Text(
                    "¡Ingresa ahora!",
                    color = Color.Blue,
                )
            }
        }

    }
}