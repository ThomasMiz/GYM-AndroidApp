package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.scale
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
import com.grupo14.gym_androidapp.viewmodels.ProfileViewModel
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onSignedOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        //.background(Color(255, 0, 0, 50)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (viewModel.uiState.isFetchingUser) {
            FullLoadingScreen()
        } else if (viewModel.uiState.user != null) {
            ProfileScreenLoaded(viewModel, onSignedOut)
        } else if (viewModel.uiState.fetchUserErrorStringId != null) {
            ProfileScreenError(viewModel)
        } else {
            // This should only run once, on the first compose of this screen
            viewModel.fetchCurrentUser()
        }
    }
}

@Composable
private fun ProfileScreenError(
    viewModel: ProfileViewModel
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

        if (viewModel.uiState.fetchUserErrorStringId != null) {
            Text(
                text = stringResource(viewModel.uiState.fetchUserErrorStringId!!)
            )
        }

        Text(
            text = stringResource(id = R.string.tryAgainLater),
        )

        Button(
            onClick = { viewModel.fetchCurrentUser() }, modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.tryAgain), color = Color.Black
            )
        }
    }
}

@Composable
private fun ProfileScreenLoaded(
    viewModel: ProfileViewModel,
    onSignedOut: () -> Unit
) {
    val user: UserApiModel = viewModel.uiState.user!!
    val fullName = "${user.firstName} ${user.lastName}"

    var fullnameEditing by remember { mutableStateOf(fullName) }
    var seggsIndexEditing by remember { mutableStateOf(user.gender!!.ordinal) }
    var birthdateEditing by remember { mutableStateOf(ConvertDateToLocalDate(user.birthdate!!)) }

    val seggsDialogState = rememberMaterialDialogState()
    val datepickerDialogState = rememberMaterialDialogState()
    val discardDialogState = rememberMaterialDialogState()
    val signoutDialogState = rememberMaterialDialogState()

    val genderOptionsList = createGendersList()

    var isSigningOut by remember { mutableStateOf(false) }

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

    if (!viewModel.uiState.isEditingUser) {
        Text(
            text = fullName, fontSize = 40.sp, modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "@" + user.username, modifier = Modifier.padding(bottom = 10.dp)
        )

        Text(
            text = stringResource(
                R.string.profileSeggsLabel, stringResource(id = user.gender!!.stringResourceId)
            ), fontSize = 24.sp, modifier = Modifier.padding(vertical = 10.dp)
        )

        Text(
            text = stringResource(R.string.profileBithdateLabel, formatDate(user.birthdate!!)),
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Button(
            onClick = {
                fullnameEditing = fullName
                seggsIndexEditing = user.gender!!.ordinal
                birthdateEditing = ConvertDateToLocalDate(user.birthdate)
                viewModel.startEditUser()
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

        OutlinedButton(
            onClick = { signoutDialogState.show() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colors.secondaryVariant,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
        ) {
            if (viewModel.uiState.isSigningOut) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.scale(0.5f)
                )
            } else {
                Text(
                    text = stringResource(R.string.signOut),
                    color = Color.Black
                )
            }
        }

        MaterialDialog(dialogState = signoutDialogState, buttons = {
            positiveButton(res = R.string.ok) {
                viewModel.signOut() {
                    onSignedOut()
                }
            }
            negativeButton(res = R.string.cancel)
        }) {
            title(res = R.string.confirmSignoutProfileDialogTitle)
            message(res = R.string.confirmSignoutProfileDialogMessage)
        }

    } else { // if (isEditing)
        Column(
            modifier = Modifier.padding(top = 10.dp, start = 50.dp, end = 50.dp)
        ) {
            TextField(
                value = fullnameEditing,
                onValueChange = { newValue -> fullnameEditing = newValue },
                //fontSize = 40.sp,
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                enabled = !viewModel.uiState.isPuttingUser
            )

            Button(
                onClick = { seggsDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                enabled = !viewModel.uiState.isPuttingUser
            ) {
                Text(
                    text = stringResource(
                        R.string.profileSeggsLabel, genderOptionsList[seggsIndexEditing]
                    ), color = Color.Black, modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { datepickerDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                enabled = !viewModel.uiState.isPuttingUser
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
            datepicker(
                initialDate = birthdateEditing, colors = DatePickerDefaults.colors(
                    headerBackgroundColor = MaterialTheme.colors.secondaryVariant,
                    calendarHeaderTextColor = MaterialTheme.colors.secondaryVariant,
                    dateActiveBackgroundColor = MaterialTheme.colors.secondaryVariant
                )
            ) { date ->
                birthdateEditing = date
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

        MaterialDialog(dialogState = discardDialogState, buttons = {
            positiveButton(res = R.string.yes) {
                viewModel.cancelEditUser()
            }
            negativeButton(res = R.string.no)
        }) {
            title(res = R.string.confirmDiscardProfileDialogTitle)
            message(res = R.string.confirmDiscardProfileDialogMessage)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            if (viewModel.uiState.isPuttingUser) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant
                )
            } else {
                IconButton(
                    onClick = {
                        fullnameEditing = fullnameEditing.trim()
                        val i = fullnameEditing.indexOf(' ')
                        viewModel.putCurrentUser(
                            user.copy(
                                firstName = if (i >= 0) fullnameEditing.substring(0, i)
                                    .trim() else fullnameEditing,
                                lastName = if (i >= 0) fullnameEditing.substring(i)
                                    .trim() else null,
                                birthdate = ConvertLocalDateToDate(birthdateEditing),
                                gender = Gender.values()[seggsIndexEditing]
                            )
                        )
                    }, modifier = Modifier.padding(end = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "acceptEdit",
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = { discardDialogState.show() },
                    modifier = Modifier.padding(start = 10.dp)
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
}