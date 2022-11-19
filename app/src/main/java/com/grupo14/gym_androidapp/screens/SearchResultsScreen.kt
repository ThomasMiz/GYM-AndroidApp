package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.grupo14.gym_androidapp.MyDropDownMenu
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.ui.theme.DifficultyRed
import com.grupo14.gym_androidapp.ui.theme.ErrorRed
import com.grupo14.gym_androidapp.ui.theme.StarYellow
import com.grupo14.gym_androidapp.viewmodels.SearchResultsViewModel

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
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                val teodoro = listOf(
                    stringResource(id = R.string.orderByDateString),
                    stringResource(id = R.string.orderByRatingString),
                    stringResource(id = R.string.orderByDifficultyString),
                    stringResource(id = R.string.orderByCategoryString)
                )
                val tobias = listOf("date", "score", "difficulty", "category")

                MyDropDownMenu(
                    label = stringResource(id = R.string.orderByPlaceholder),
                    elements = teodoro,
                    selectedText = teodoro.elementAtOrNull(tobias.indexOfFirst { it == viewModel.orderBy })
                        ?: "",
                    padding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, CircleShape)
                ) { selectedString ->
                    viewModel.setFilterOrderBy(
                        tobias.elementAtOrNull(teodoro.indexOf(selectedString))
                    )
                }
            }

            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                val tobias = listOf(
                    stringResource(id = R.string.asc),
                    stringResource(id = R.string.desc)
                )
                val uriel = listOf("asc", "desc")

                MyDropDownMenu(
                    label = stringResource(id = R.string.directionPlaceholder),
                    elements = tobias,
                    selectedText = tobias.elementAtOrNull(uriel.indexOfFirst { it == viewModel.direction })
                        ?: "",
                    padding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, CircleShape)
                ) { selectedString ->
                    viewModel.setFilterDirection(
                        uriel.elementAtOrNull(tobias.indexOf(selectedString))
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            AdaptibleList(
                items = viewModel.uiState.routines,
                addLoadingIndicator = viewModel.uiState.isFetchingRoutines
            ) { routine ->
                RoutineCardEntry(routine, onNavigateToRoutineRequested)
            }

            if (viewModel.uiState.fetchRoutinesErrorStringId != null) {
                Text(
                    text = stringResource(id = viewModel.uiState.fetchRoutinesErrorStringId!!),
                    color = ErrorRed
                )
            }

            if (viewModel.uiState.isFetchingRoutines) {
                /*CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )*/
            } else {
                if (viewModel.uiState.hasMoreRoutinesToFetch) {
                    viewModel.fetchMoreRoutines()
                } else if (viewModel.uiState.routines.isEmpty() && viewModel.uiState.fetchRoutinesErrorStringId == null) {
                    Text(
                        text = stringResource(id = R.string.noRoutinesFound),
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
