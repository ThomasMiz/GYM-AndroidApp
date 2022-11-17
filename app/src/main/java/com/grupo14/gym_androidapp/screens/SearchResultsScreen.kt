package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.AdaptibleList
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.ui.theme.DifficultyRed
import com.grupo14.gym_androidapp.ui.theme.ErrorRed
import com.grupo14.gym_androidapp.ui.theme.StarYellow
import com.grupo14.gym_androidapp.viewmodels.SearchResultsViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun SearchResultsScreen(
    onNavigateToRoutineRequested: (routineId: Int) -> Unit,
    viewModel: SearchResultsViewModel
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.yourFavoriteRoutines), // TODO: Cambiar a search results
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            AdaptibleList(viewModel.uiState.routines) { routine ->
                RoutineCardEntry(routine, onNavigateToRoutineRequested)
            }

            if (viewModel.uiState.fetchRoutinesErrorStringId != null) {
                Text(
                    text = stringResource(id = viewModel.uiState.fetchRoutinesErrorStringId!!),
                    color = ErrorRed
                )
            }

            if (viewModel.uiState.isFetchingRoutines) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            } else {
                if (viewModel.uiState.hasMoreRoutinesToFetch) {
                    viewModel.fetchMoreRoutines()
                } else if (viewModel.uiState.routines.isEmpty() && viewModel.uiState.fetchRoutinesErrorStringId == null) {
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
private fun RoutineCardEntry(
    routine: RoutineApiModel,
    onNavigateToRoutineRequested: (routineId: Int) -> Unit,
) {
    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToRoutineRequested(routine.id!!) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
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
                    text = routine.name!!,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = stringResource(id = R.string.by, "@${routine.user!!.username!!}")
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Score text & icon
                Surface(
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .fillMaxHeight(),
                    elevation = 3.dp
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = if (routine.score == null || routine.score == 0f) " - " else routine.score.toString(),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )

                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "routineScore",
                            tint = StarYellow
                        )
                    }
                }

                // Difficulty text & icon
                Surface(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .fillMaxHeight(),
                    elevation = 3.dp,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = stringResource(routine.difficulty!!.stringResourceId),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.dumbbell),
                            contentDescription = "routineDifficulty",
                            tint = DifficultyRed,
                            modifier = Modifier.padding(end = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
