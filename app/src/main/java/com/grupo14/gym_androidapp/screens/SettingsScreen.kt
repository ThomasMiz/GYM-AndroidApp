package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.grupo14.gym_androidapp.AppSettings

@Composable
fun SettingsScreen(

) {
    val gonzalo = LocalConfiguration.current
    val mariano =
        if (gonzalo.screenWidthDp > 500) Modifier.width(500.dp) else Modifier.fillMaxWidth()

    var emilio by remember { mutableStateOf(AppSettings.getIsDetailedExecutionEnabled()) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = mariano.fillMaxHeight()
    ) {
        Divider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        ) {
            Text("Ejecución con detalles")
            Switch(
                checked = emilio,
                onCheckedChange = {
                    AppSettings.setIsDetailedExecutionEnabled(it)
                    emilio = AppSettings.getIsDetailedExecutionEnabled()
                }
            )
        }

        Divider()
    }
}