package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.*
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.Gender
import com.grupo14.gym_androidapp.api.models.UserApiModel
import com.grupo14.gym_androidapp.viewmodels.ProfileViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.time.LocalDate

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onSignedOut: () -> Unit
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
    val currentConfig = LocalConfiguration.current
    val columnWidthMod =
        if (currentConfig.screenWidthDp > 500) Modifier.width(500.dp) else Modifier.fillMaxWidth()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = columnWidthMod
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            //.background(Color(255, 0, 0, 50)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            val user: UserApiModel = viewModel.uiState.user!!

            var fullName = "${user.firstName?.trim() ?: ""} ${user.lastName?.trim() ?: ""}"
            if (fullName.isBlank())
                fullName = user.username!!

            var fullnameEditing by remember { mutableStateOf(fullName) }
            var seggsIndexEditing by remember { mutableStateOf(user.gender!!.ordinal) }
            var birthdateEditing by remember {
                mutableStateOf(user.birthdate?.let {
                    ConvertDateToLocalDate(
                        it
                    )
                })
            }

            val datepickerDialogState = rememberMaterialDialogState()
            val discardDialogState = rememberMaterialDialogState()
            val signoutDialogState = rememberMaterialDialogState()

            val genderOptionsList = createGendersList()

            Box(
                modifier = Modifier.padding(horizontal = 75.dp, vertical = 20.dp)
            ) {
                var profileImageMods =
                    if (currentConfig.screenWidthDp > 400) Modifier.width(400.dp) else Modifier.fillMaxWidth()

                if (viewModel.uiState.isEditingUser)
                    profileImageMods = profileImageMods.clickable {  }

                Image(
                    painter = painterResource(R.drawable.profile_placeholder),
                    contentDescription = "ProfilePic",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    modifier = profileImageMods.clip(RoundedCornerShape(10)),
                    colorFilter = if (viewModel.uiState.isEditingUser) ColorFilter.tint(Color.Gray, BlendMode.Multiply) else null
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
                        R.string.profileSeggsLabel,
                        stringResource(id = user.gender!!.stringResourceId)
                    ), fontSize = 24.sp, modifier = Modifier.padding(vertical = 10.dp)
                )

                Text(
                    text = stringResource(
                        R.string.profileBithdateLabel,
                        user.birthdate?.let { formatDate(it) }
                            ?: stringResource(id = R.string.unspecified)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Button(
                    onClick = {
                        fullnameEditing = fullName
                        seggsIndexEditing = user.gender!!.ordinal
                        birthdateEditing = user.birthdate?.let { ConvertDateToLocalDate(it) }
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
                    OutlinedTextField(
                        value = fullnameEditing,
                        onValueChange = { newValue -> fullnameEditing = newValue },
                        //fontSize = 40.sp,
                        modifier = Modifier
                            .padding(horizontal = 0.dp, vertical = 10.dp)
                            .fillMaxWidth(),
                        enabled = !viewModel.uiState.isPuttingUser,
                        label = { Text(text = stringResource(id = R.string.fullName)) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                    )

                    MyDropDownMenu(
                        elements = genderOptionsList,
                        selectedText = genderOptionsList[seggsIndexEditing],
                        label = stringResource(R.string.seggsPlaceholder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        seggsIndexEditing = genderOptionsList.indexOf(it)
                        if (seggsIndexEditing < 0)
                            seggsIndexEditing = genderOptionsList.lastIndex
                    }

                    Button(
                        onClick = { datepickerDialogState.show() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                        enabled = !viewModel.uiState.isPuttingUser,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.profileBithdateLabel,
                                birthdateEditing?.let { formatDate(it) }
                                    ?: stringResource(id = R.string.unspecified)
                            ),
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
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
                        initialDate = birthdateEditing ?: LocalDate.of(2000, 4, 20),
                        colors = DatePickerDefaults.colors(
                            headerBackgroundColor = MaterialTheme.colors.secondaryVariant,
                            calendarHeaderTextColor = MaterialTheme.colors.secondaryVariant,
                            dateActiveBackgroundColor = MaterialTheme.colors.secondaryVariant
                        )
                    ) { date ->
                        birthdateEditing = date
                    }
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
                                        birthdate = birthdateEditing?.let {
                                            ConvertLocalDateToDate(
                                                it
                                            )
                                        },
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
    }
}