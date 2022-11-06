package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.R

@Composable
fun HomeScreen(
    onNavigateToOtherScreen: (id: Int) -> Unit
) {
    var id by remember { mutableStateOf(1234) }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(onClick = { onNavigateToOtherScreen(id) }) {
                Text(
                    text = stringResource(R.string.pedro),
                    fontSize = 30.sp
                )
            }

            TextField(
                value = id.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { newValue -> id = newValue.toIntOrNull() ?: 0 },
            )
        }
    }
}