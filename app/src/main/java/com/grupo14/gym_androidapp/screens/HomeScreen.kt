package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.AdaptibleList
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.ui.theme.ErrorRed
import com.grupo14.gym_androidapp.ui.theme.FavoritePink
import com.grupo14.gym_androidapp.viewmodels.HomeRoutineUiState
import com.grupo14.gym_androidapp.viewmodels.HomeViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToRoutineRequested: (id: Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .fillMaxWidth()
        //.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.yourFavoriteRoutines),
            textAlign = TextAlign.Start,
            fontSize = 24.sp
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {

            val felipe = rememberMaterialDialogState()
            var florencio by remember { mutableStateOf(-1) }

            // Confirmation dialog for unfavoriting a routine
            MaterialDialog(
                dialogState = felipe,
                buttons = {
                    positiveButton(stringResource(id = R.string.ok)) {
                        viewModel.unfavoriteRoutine(florencio)
                        florencio = -1
                    }
                    negativeButton(stringResource(id = R.string.cancel)) {
                        florencio = -1
                    }
                }
            ) {
                title(res = R.string.confirmUnfavoriteDialogTitle)
                message(res = R.string.confirmUnfavoriteDialogMessage)
                message(
                    text = viewModel.uiState.favorites.find { it.routine.id == florencio }?.routine?.name
                        ?: ""
                )
            }

            AdaptibleList(
                items = viewModel.uiState.favorites,
                addLoadingIndicator = viewModel.uiState.isFetchingFavorites
            ) { routineState ->
                FavoriteRoutineCard(onNavigateToRoutineRequested, routineState) {
                    if (florencio < 0 && !viewModel.isUnfavoritingAny()) {
                        florencio = it
                        felipe.show()
                    }
                }
            }

            if (viewModel.uiState.fetchFavoritesErrorStringId != null) {
                Text(
                    text = stringResource(id = viewModel.uiState.fetchFavoritesErrorStringId!!),
                    color = ErrorRed
                )
            }

            if (viewModel.uiState.isFetchingFavorites) {
                /*CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )*/
            } else {
                if (viewModel.uiState.hasMoreFavoritesToFetch) {
                    viewModel.fetchMoreFavorites()
                } else if (viewModel.uiState.favorites.isEmpty() && viewModel.uiState.fetchFavoritesErrorStringId == null) {
                    Text(
                        text = stringResource(id = R.string.noFavoritesYet),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteRoutineCard(
    onNavigateToRoutineRequested: (id: Int) -> Unit,
    routineState: HomeRoutineUiState,
    onUnfavorited: (Int) -> Unit
) {
    val francisco = routineState.routine

    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToRoutineRequested(routineState.routine.id!!) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
            //.fillMaxWidth()
        ) {
            // Routine image
            Image(
                painter = painterResource(id = R.drawable.rutina),
                contentDescription = "routine",
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(70.dp)
                    .width(90.dp)
                    .clip(RoundedCornerShape(10))
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxHeight()
                    .weight(1.0f)
            ) {
                Text(
                    text = francisco.name!!,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = stringResource(id = R.string.by, "@${francisco.user!!.username!!}")
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (routineState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.scale(0.5f),
                        color = FavoritePink
                    )
                } else {
                    IconButton(onClick = { onUnfavorited(francisco.id!!) }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "toggleFavorite",
                            tint = FavoritePink
                        )
                    }
                }
            }
        }
    }
}