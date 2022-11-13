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
import com.grupo14.gym_androidapp.*
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.Gender
import com.grupo14.gym_androidapp.api.models.UserApiModel
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

    val user: UserApiModel = viewModel.loginUiState.user!!
    val fullName = "${user.firstName} ${user.lastName}"

    var fullnameEditing by remember { mutableStateOf(fullName) }
    var seggsIndexEditing by remember { mutableStateOf(user.gender.ordinal) }
    var birthdateEditing by remember { mutableStateOf(user.birthdate) }

    val seggsDialogState = rememberMaterialDialogState()
    val datepickerDialogState = rememberMaterialDialogState()

    val genderOptionsList = createGendersList()

    Box(
        modifier = Modifier.padding(horizontal = 75.dp, vertical = 20.dp)
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
            text = fullName,
            fontSize = 40.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "@" + user.username,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Text(
            text = stringResource(R.string.profileSeggsLabel, stringResource(id = user.gender.stringResourceId)),
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Text(
            text = stringResource(R.string.profileBithdateLabel, formatDate(user.birthdate)),
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Button(
            onClick = {
                fullnameEditing = fullName
                seggsIndexEditing = user.gender.ordinal
                birthdateEditing = user.birthdate
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
                value = fullnameEditing, onValueChange = { newValue -> fullnameEditing = newValue },
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
                        R.string.profileSeggsLabel, genderOptionsList[seggsIndexEditing]
                    ), color = Color.Black, modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { datepickerDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
            ) {

                Text(
                    text = stringResource(
                        R.string.profileBithdateLabel, formatDate(birthdateEditing)
                    ), color = Color.Black, modifier = Modifier.fillMaxWidth()
                )
            }
        }

        MaterialDialog(
            dialogState = datepickerDialogState,
            buttons = {
                positiveButton(text = stringResource(id = R.string.ok))
                negativeButton(text = stringResource(id = R.string.cancel))
            },
        ) {
            datepicker { date ->
                birthdateEditing = ConvertLocalDateToDate(date)
            }
        }

        MaterialDialog(dialogState = seggsDialogState, buttons = {
            negativeButton(stringResource(id = R.string.cancel))
        }) {
            listItemsSingleChoice(
                list = genderOptionsList,
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
                    // TODO: Save shit to API and update viewmodel
                    // username = usernameEditing
                    // seggsIndex = seggsIndexEditing
                    // birthdate = birthdateEditing
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