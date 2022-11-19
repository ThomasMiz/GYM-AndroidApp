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
        modifier = Modifier.fillMaxSize()
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
    val gonzalo = LocalConfiguration.current
    val martín =
        if (gonzalo.screenWidthDp > 500) Modifier.width(500.dp) else Modifier.fillMaxWidth()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = martín
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            //.background(Color(255, 0, 0, 50)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            val ian: UserApiModel = viewModel.uiState.user!!

            var ilan = "${ian.firstName?.trim() ?: ""} ${ian.lastName?.trim() ?: ""}"
            if (ilan.isBlank())
                ilan = ian.username!!

            var ioel by remember { mutableStateOf(ilan) }
            var iván by remember { mutableStateOf(ian.gender!!.ordinal) }
            var javier by remember {
                mutableStateOf(ian.birthdate?.let {
                    ConvertDateToLocalDate(
                        it
                    )
                })
            }

            val joel = rememberMaterialDialogState()
            val kevin = rememberMaterialDialogState()
            val agustín = rememberMaterialDialogState()

            val león = createGendersList()

            Box(
                modifier = Modifier.padding(horizontal = 75.dp, vertical = 20.dp)
            ) {
                var luciano =
                    if (gonzalo.screenWidthDp > 400) Modifier.width(400.dp) else Modifier.fillMaxWidth()

                if (viewModel.uiState.isEditingUser)
                    luciano = luciano.clickable {  }

                Image(
                    painter = painterResource(R.drawable.profile_placeholder),
                    contentDescription = "ProfilePic",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    modifier = luciano.clip(RoundedCornerShape(10)),
                    colorFilter = if (viewModel.uiState.isEditingUser) ColorFilter.tint(Color.Gray, BlendMode.Multiply) else null
                    // .border(5.dp, Color.Gray, RoundedCornerShape(10))
                )
            }

            if (!viewModel.uiState.isEditingUser) {
                Text(
                    text = ilan, fontSize = 40.sp, modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "@" + ian.username, modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = stringResource(
                        R.string.profileSeggsLabel,
                        stringResource(id = ian.gender!!.stringResourceId)
                    ), fontSize = 24.sp, modifier = Modifier.padding(vertical = 10.dp)
                )

                Text(
                    text = stringResource(
                        R.string.profileBithdateLabel,
                        ian.birthdate?.let { formatDate(it) }
                            ?: stringResource(id = R.string.unspecified)),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Button(
                    onClick = {
                        ioel = ilan
                        iván = ian.gender!!.ordinal
                        javier = ian.birthdate?.let { ConvertDateToLocalDate(it) }
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
                    onClick = { agustín.show() },
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

                MaterialDialog(dialogState = agustín, buttons = {
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
                        value = ioel,
                        onValueChange = { newValue -> ioel = newValue },
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
                        elements = león,
                        selectedText = león[iván],
                        label = stringResource(R.string.seggsPlaceholder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        iván = león.indexOf(it)
                        if (iván < 0)
                            iván = león.lastIndex
                    }

                    Button(
                        onClick = { joel.show() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                        enabled = !viewModel.uiState.isPuttingUser,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.profileBithdateLabel,
                                javier?.let { formatDate(it) }
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
                    dialogState = joel,
                    buttons = {
                        positiveButton(text = stringResource(id = R.string.ok))
                        negativeButton(text = stringResource(id = R.string.cancel))
                    },
                ) {
                    datepicker(
                        initialDate = javier ?: LocalDate.of(2000, 4, 20),
                        colors = DatePickerDefaults.colors(
                            headerBackgroundColor = MaterialTheme.colors.secondaryVariant,
                            calendarHeaderTextColor = MaterialTheme.colors.secondaryVariant,
                            dateActiveBackgroundColor = MaterialTheme.colors.secondaryVariant
                        )
                    ) { date ->
                        javier = date
                    }
                }

                MaterialDialog(dialogState = kevin, buttons = {
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
                                ioel = ioel.trim()
                                val i = ioel.indexOf(' ')
                                viewModel.putCurrentUser(
                                    ian.copy(
                                        firstName = if (i >= 0) ioel.substring(0, i)
                                            .trim() else ioel,
                                        lastName = if (i >= 0) ioel.substring(i)
                                            .trim() else null,
                                        birthdate = javier?.let {
                                            ConvertLocalDateToDate(
                                                it
                                            )
                                        },
                                        gender = Gender.values()[iván]
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
                            onClick = { kevin.show() },
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