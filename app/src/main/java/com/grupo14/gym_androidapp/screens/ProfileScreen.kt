package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.grupo14.gym_androidapp.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            //.background(Color(255, 0, 0, 50)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 75.dp, vertical = 20.dp)
                //.background(Color(0, 255, 0, 127))

        ) {
            Image(
                painter = painterResource(R.drawable.profile_placeholder),
                contentDescription = "ProfilePic",
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10))
                    // .border(5.dp, Color.Gray, RoundedCornerShape(10))
            )
        }

        Text(
            text = "Felipe Piña",
            fontSize = 40.sp,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
        )

        Text (
            text = stringResource(R.string.profileSeggsLabel, "M"),
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
        )

        Text (
            text = stringResource(R.string.profileBithdateLabel, "18/10/2000"),
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
        )

        Button(
            onClick = {},
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "editProfile",
                tint = Color.Black
            )
            Text(
                text = stringResource(R.string.editProfile),
                modifier = Modifier.background(MaterialTheme.colors.primary),
                color = Color.Black
            )
        }

    }
}