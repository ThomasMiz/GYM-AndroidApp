package com.grupo14.gym_androidapp.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.grupo14.gym_androidapp.AppConfig
import com.grupo14.gym_androidapp.FullLoadingScreen
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.ui.theme.FavoritePink
import com.grupo14.gym_androidapp.viewmodels.RoutineViewModel

@Composable
fun RoutineScreen(
    viewModel: RoutineViewModel,
    routineId: Int
) {
    if (viewModel.uiState.fetchingRoutineId != routineId) {
        // This should only run once, on the first compose of this screen
        viewModel.fetchRoutine(routineId)
    } else if (viewModel.uiState.isFetchingRoutine) {
        FullLoadingScreen()
    } else if (viewModel.uiState.routine != null) {
        RoutineScreenLoaded(viewModel, routineId)
    } else if (viewModel.uiState.fetchRoutineErrorStringId != null) {
        RoutineScreenError(viewModel, routineId)
    } else {
        // This should only run once, on the first compose of this screen
        viewModel.fetchRoutine(routineId)
    }
}

@Composable
private fun RoutineScreenError(
    viewModel: RoutineViewModel,
    routineId: Int
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

        if (viewModel.uiState.fetchRoutineErrorStringId != null) {
            Text(
                text = stringResource(viewModel.uiState.fetchRoutineErrorStringId!!)
            )
        }

        Text(
            text = stringResource(id = R.string.tryAgainLater),
        )

        Button(
            onClick = { viewModel.fetchRoutine(routineId) },
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
private fun RoutineScreenLoaded(
    viewModel: RoutineViewModel,
    routineId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 30.dp, end = 30.dp, top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        val routine = viewModel.uiState.routine!!;

        Text(
            text = routine.name!!,
            fontSize = 40.sp,
            modifier = Modifier.padding(top = 10.dp),
            textAlign = TextAlign.Start
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.by, "@" + routine.user!!.username!!),
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (viewModel.uiState.isFetchinigFavorite) {
                    CircularProgressIndicator(
                        modifier = Modifier.scale(0.5f),
                        color = FavoritePink
                    )
                } else {
                    val heartColor =
                        if (viewModel.uiState.isFavorited) FavoritePink else Color.LightGray
                    val heartIcon =
                        if (viewModel.uiState.isFavorited) Icons.Filled.Favorite else Icons.Outlined.Favorite

                    val context = LocalContext.current
                    val favFailMessage = stringResource(id = R.string.toggleFavoriteFailed)
                    IconButton(onClick = {
                        viewModel.toggleFavorite(routineId) {
                            Toast.makeText(context, favFailMessage, Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = heartIcon,
                            contentDescription = "toggleFavorite",
                            tint = heartColor
                        )
                    }
                }

                val context = LocalContext.current
                IconButton(onClick = {
                    val link = "${AppConfig.BASE_URL}routine/${routineId}/"
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    // intent.putExtra(Intent.EXTRA_SUBJECT, "Your subject")
                    intent.putExtra(Intent.EXTRA_TEXT, link)
                    ContextCompat.startActivity(
                        context,
                        Intent.createChooser(intent, null),
                        null
                    )
                }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "share",
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}