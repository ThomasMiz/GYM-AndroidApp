package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.GymViewModel
import com.grupo14.gym_androidapp.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun ProfileScreen(
    viewModel: GymViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        //.background(Color(255, 0, 0, 50)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (viewModel.loginUiState.isFetchingUser) {
            ProfileScreenLoading(viewModel)
        } else if (viewModel.loginUiState.user != null) {
            ProfileScreenLoaded(viewModel)
        } else if (viewModel.loginUiState.fetchUserErrorStringId != null) {
            ProfileScreenError(viewModel)
        } else {
            // This should only run once, on the first compose of this screen
            viewModel.fetchCurrentUser()
        }
    }
}

@Composable
fun ProfileScreenLoading(
    viewModel: GymViewModel
) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}

@Composable
fun ProfileScreenError(
    viewModel: GymViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(
            text = stringResource(id = R.string.oops),
            fontSize = 50.sp,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        if (viewModel.loginUiState.fetchUserErrorStringId != null) {
            Text(
                text = stringResource(viewModel.loginUiState.fetchUserErrorStringId!!)
            )
        }

        Text(
            text = stringResource(id = R.string.tryAgainLater),
        )

        Button(
            onClick = { viewModel.fetchCurrentUser() },
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.tryAgain),
                color = Color.Black
            )
        }
    }
}

@Composable
fun ProfileScreenLoaded(
    viewModel: GymViewModel
) {
    var isEditing by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("Felipe Piña") }
    var seggsIndex by remember { mutableStateOf(0) }
    var birthdate by remember { mutableStateOf(java.time.LocalDate.now()) }

    var usernameEditing by remember { mutableStateOf(username) }
    var seggsIndexEditing by remember { mutableStateOf(seggsIndex) }
    var birthdateEditing by remember { mutableStateOf(birthdate) }

    val seggsDialogState = rememberMaterialDialogState()
    val datepickerDialogState = rememberMaterialDialogState()

    val seggsOptionsList = listOf(
        stringResource(R.string.male),
        stringResource(R.string.female),
        stringResource(R.string.other)
    )

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

    if (!isEditing) {
        Text(
            text = username,
            fontSize = 40.sp,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
        )

        Text(
            text = stringResource(R.string.profileSeggsLabel, seggsOptionsList[seggsIndex]),
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
        )

        Text(
            text = stringResource(R.string.profileBithdateLabel, birthdate),
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
        )

        Button(
            onClick = {
                usernameEditing = username
                seggsIndexEditing = seggsIndex
                birthdateEditing = birthdate
                isEditing = true
            }, modifier = Modifier.padding(10.dp)
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
    } else { // if (isEditing)
        Column(
            modifier = Modifier.padding(top = 10.dp, start = 50.dp, end = 50.dp)
        ) {
            TextField(
                value = usernameEditing, onValueChange = { newValue -> usernameEditing = newValue },
                //fontSize = 40.sp,
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = { seggsDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
            ) {
                Text(
                    text = stringResource(
                        R.string.profileSeggsLabel, seggsOptionsList[seggsIndexEditing]
                    ), color = Color.Black, modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { datepickerDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
            ) {
                Text(
                    text = stringResource(
                        R.string.profileBithdateLabel, birthdateEditing.toString()
                    ), color = Color.Black, modifier = Modifier.fillMaxWidth()
                )
            }
        }

        MaterialDialog(
            dialogState = datepickerDialogState,
            buttons = {
                positiveButton("Ok")
                negativeButton("Cancel")
            },
        ) {
            datepicker { date ->
                birthdateEditing = date
            }
        }

        MaterialDialog(dialogState = seggsDialogState, buttons = {
            negativeButton("Cancel")
        }) {
            listItemsSingleChoice(
                list = seggsOptionsList,
                initialSelection = seggsIndexEditing,
                onChoiceChange = { selectedIndex ->
                    seggsIndexEditing = selectedIndex
                    seggsDialogState.hide()
                },
                waitForPositiveButton = false
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            IconButton(
                onClick = {
                    username = usernameEditing
                    seggsIndex = seggsIndexEditing
                    birthdate = birthdateEditing
                    isEditing = false
                }, modifier = Modifier.padding(end = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "acceptEdit",
                    tint = Color.Black
                )
            }
            IconButton(
                onClick = { isEditing = false }, modifier = Modifier.padding(start = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "cancelEdit",
                    tint = Color.Black
                )
            }
        }
    }
}