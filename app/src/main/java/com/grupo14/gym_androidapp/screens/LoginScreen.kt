package com.grupo14.gym_androidapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.viewmodels.SessionViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: SessionViewModel
){
    if (viewModel.sessionUiState.fetchUserErrorStringId != null || viewModel.sessionUiState.fetchUserErrorString != null) {
        LoginScreenError(navController, viewModel)
    } else if(viewModel.sessionUiState.user != null) {
        navController.navigate("home")
    } else {
        LoginScreenLoaded(navController, viewModel)
    }
}

@Composable
fun LoginScreenLoaded(
    navController: NavHostController,
    viewModel: SessionViewModel
) {
    val context = LocalContext.current

    val userVal = remember { mutableStateOf("") }
    val passwordVal = remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

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

        OutlinedTextField(
            value = userVal.value,
            onValueChange = { userVal.value = it },
            label = { Text(text = "Usuario", color = Color.Gray) },
            placeholder = { Text(text = "Usuario") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.8f),
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
            value = passwordVal.value,
            onValueChange = { passwordVal.value = it},
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
            visualTransformation = if (passwordVisibility.value)
                VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth(0.8f),
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, null) },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
        )

        Button(
            onClick = {
                if (userVal.value.isEmpty()) {
                    Toast.makeText(context, "Por favor, ingrese un correo electrónico", Toast.LENGTH_SHORT).show()
                } else if (passwordVal.value.isEmpty()) {
                    Toast.makeText(context, "Por favor, ingrese una contraseña", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.loginUser(userVal.value, passwordVal.value)
                }
          },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            Text(
                "Ingresar",
                Modifier
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colors.secondary,
                fontSize = 25.sp
            )
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
                "¿No tienes una cuenta aún?",
                color = MaterialTheme.colors.secondary,
            )
            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text(
                    "¡Registrate!",
                    color = Color.Blue,
                )
            }
        }

    }
}

@Composable
fun LoginScreenError(
    navController : NavHostController,
    viewModel: SessionViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Text(
            text = stringResource(id = R.string.oops),
            fontSize = 50.sp,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        if (viewModel.sessionUiState.fetchUserErrorStringId != null) {
            Text(
                text = stringResource(viewModel.sessionUiState.fetchUserErrorStringId!!)
            )
        } else if(viewModel.sessionUiState.fetchUserErrorString != null) {
            Text(
                text = viewModel.sessionUiState.fetchUserErrorString!!
            )
        }

        Text(
            text = stringResource(id = R.string.tryAgainLater),
        )

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.tryAgain),
                color = Color.Black
            )
        }
    }
}