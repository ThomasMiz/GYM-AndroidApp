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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import com.grupo14.gym_androidapp.R


@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current

    val emailVal = remember { mutableStateOf("") }
    val userVal = remember { mutableStateOf("")}
    val passwordVal = remember { mutableStateOf("") }
    val repPasswordVal = remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }
    val repPasswordVisibility = remember { mutableStateOf(false) }

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
            value = emailVal.value,
            onValueChange = { emailVal.value = it },
            label = { Text(text = "Correo electrónico") },
            placeholder = { Text(text = "Correo electrónico") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.8f),
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
            value = userVal.value,
            onValueChange = { userVal.value = it },
            label = { Text(text = "Nombre de usuario") },
            placeholder = { Text(text = "Nombre de usuario") },
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
            label = { Text(text = "Contraseña") },
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
        )

        OutlinedTextField(
            value = repPasswordVal.value,
            onValueChange = { repPasswordVal.value = it},
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
            label = { Text(text = "Repetir contraseña") },
            placeholder = { Text(text = "Repetir contraseña") },
            singleLine = true,
            visualTransformation = if (repPasswordVisibility.value)
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
            // Basic checks, improve them.
            onClick = {
                if (emailVal.value.isEmpty()) {
                    Toast.makeText(context, "Por favor, ingrese un correo electrónico", Toast.LENGTH_SHORT).show()
                } else if(userVal.value.isEmpty()) {
                    Toast.makeText(context, "Por favor, ingrese un nombre de usuario", Toast.LENGTH_SHORT).show()
                } else if (passwordVal.value.isEmpty()) {
                    Toast.makeText(context, "Por favor, ingrese una contraseña", Toast.LENGTH_SHORT).show()
                } else if(!passwordVal.value.equals(repPasswordVal.value)) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    navController.navigate("login")
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            Text(
                "Continuar",
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
                "¿Ya tienes una cuenta?",
                color = MaterialTheme.colors.secondary,
            )
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    "¡Ingresa ahora!",
                    color = Color.Blue,
                )
            }
        }

    }
}
