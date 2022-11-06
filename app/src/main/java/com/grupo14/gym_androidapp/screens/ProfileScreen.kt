package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.grupo14.gym_androidapp.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun ProfileScreen() {
    // Text(stringResource(R.string.profile))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(stringResource(R.string.profile))
        Text(stringResource(R.string.profile))
        Text(stringResource(R.string.profile))
    }
}