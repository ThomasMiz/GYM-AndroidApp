package com.grupo14.gym_androidapp.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.grupo14.gym_androidapp.AdaptibleSimpleList
import com.grupo14.gym_androidapp.AppConfig
import com.grupo14.gym_androidapp.FullLoadingScreen
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.CycleExerciseApiModel
import com.grupo14.gym_androidapp.ui.theme.*
import com.grupo14.gym_androidapp.viewmodels.CycleUiState
import com.grupo14.gym_androidapp.viewmodels.RoutineViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun RoutineScreen(
    viewModel: RoutineViewModel,
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit
) {
    if (viewModel.uiState.fetchingRoutineId != routineId) {
        // This should only run once, on the first compose of this screen
        viewModel.start(routineId)
    } else if (viewModel.uiState.isFetchingRoutine) {
        FullLoadingScreen()
    } else if (viewModel.uiState.routine != null) {
        if (viewModel.uiState.isViewingDetails)
            RoutineDetailScreen(viewModel, routineId, onNavigateToRoutineExecutionRequested)
        else
            RoutineScreenLoaded(viewModel, routineId, onNavigateToRoutineExecutionRequested)
    } else if (viewModel.uiState.fetchRoutineErrorStringId != null) {
        RoutineScreenError(viewModel, routineId)
    } else {
        // This should only run once, on the first compose of this screen
        viewModel.start(routineId)
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
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit
) {
    val johnyy = LocalContext.current
    val patricio = LocalConfiguration.current
    val pedro =
        if (patricio.screenWidthDp > 500) Modifier.width(500.dp) else Modifier.fillMaxWidth()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = pedro
                .fillMaxHeight()
                .padding(start = 30.dp, end = 30.dp)
                .verticalScroll(rememberScrollState()),
            //.background(Color(255, 0, 0, 50)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            val ramiro = viewModel.uiState.routine!!

            Text(
                text = ramiro.name!!,
                fontSize = 40.sp,
                modifier = Modifier.padding(top = 20.dp),
                textAlign = TextAlign.Start
            )

            // Username, fav and share buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                // Routine name text
                Text(
                    text = stringResource(id = R.string.by, "@" + ramiro.user!!.username!!),
                )

                // Share and favorite buttons
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

                        val favFailMessage = stringResource(id = R.string.toggleFavoriteFailed)
                        IconButton(onClick = {
                            viewModel.toggleFavorite(routineId) {
                                Toast.makeText(johnyy, favFailMessage, Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                imageVector = heartIcon,
                                contentDescription = "toggleFavorite",
                                tint = heartColor
                            )
                        }
                    }

                    IconButton(onClick = {
                        val link = "${AppConfig.BASE_URL}routine/${routineId}/"
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        // intent.putExtra(Intent.EXTRA_SUBJECT, "Your subject")
                        intent.putExtra(Intent.EXTRA_TEXT, link)
                        ContextCompat.startActivity(
                            johnyy,
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

            // Routine image
            Image(
                painter = painterResource(id = R.drawable.rutina),
                contentDescription = "routine",
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10))
            )

            // Score and difficulty
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .requiredHeight(40.dp)
            ) {
                // Score text & icon
                Surface(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .fillMaxHeight(),
                    elevation = 3.dp
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = if (ramiro.score == null || ramiro.score == 0f) " - " else ramiro.score.toString(),
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
                        .padding(start = 5.dp)
                        .fillMaxHeight(),
                    elevation = 3.dp,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = stringResource(ramiro.difficulty!!.stringResourceId),
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

            if (ramiro.detail != null) {
                Text(
                    text = ramiro.detail,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            Divider(
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(60.dp)
            ) {
                // "Review this routine"
                Text(
                    text = stringResource(id = R.string.reviewThisRoutine)
                )

                val rom??n = rememberMaterialDialogState()

                // Star rating thing for the user to rate
                if (viewModel.uiState.isFetchingRating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.secondaryVariant,
                        modifier = Modifier.scale(0.5f)
                    )
                } else {
                    var rub??n by remember { mutableStateOf(viewModel.uiState.currentRating) }
                    RatingBar(
                        value = rub??n,
                        onValueChange = { newValue -> rub??n = newValue },
                        onRatingChanged = { newValue ->
                            if (newValue != viewModel.uiState.currentRating) {
                                rom??n.show()
                            }
                        },
                        config = RatingBarConfig().style(RatingBarStyle.HighLighted),
                    )

                    val ryan = stringResource(id = R.string.submitReviewFailed)
                    val sergio = stringResource(id = R.string.submitReviewSuccess)

                    // Confirmation dialog for submit review
                    MaterialDialog(
                        dialogState = rom??n,
                        buttons = {
                            positiveButton(stringResource(id = R.string.ok)) {
                                viewModel.setRating(
                                    routineId = routineId,
                                    newRating = rub??n,
                                    onSuccess = {
                                        rub??n = viewModel.uiState.currentRating
                                        Toast.makeText(
                                            johnyy,
                                            sergio,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    },
                                    onFailure = {
                                        rub??n = viewModel.uiState.currentRating
                                        Toast.makeText(
                                            johnyy,
                                            ryan,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                )
                            }
                            negativeButton(stringResource(id = R.string.cancel)) {
                                rub??n = viewModel.uiState.currentRating
                            }
                        }
                    ) {
                        title(res = R.string.confirmReviewDialogTitle)
                        message(
                            text = stringResource(
                                R.string.confirmReviewDialogMessage,
                                rub??n.toInt()
                            )
                        )
                        message(res = R.string.actionCantBeUndone)
                    }
                }
            }


            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 60.dp, end = 60.dp, top = 20.dp, bottom = 20.dp)
            ) {
                // Button for view details
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    onClick = { viewModel.switchToDetailView(routineId) }
                ) {
                    Text(
                        text = stringResource(id = R.string.viewDetails),
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }

                // Button for start
                StartRoutineButton(viewModel, routineId, onNavigateToRoutineExecutionRequested)
            }
        }
    }
}

@Composable
private fun RoutineDetailScreen(
    viewModel: RoutineViewModel,
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit
) {
    BackHandler() {
        viewModel.switchToNormalView()
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {
            Text(
                text = stringResource(
                    id = R.string.categoryDisplay,
                    viewModel.uiState.routine!!.category!!.name!!
                ), // TODO: Category name translation?
                modifier = Modifier.padding(vertical = 18.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        viewModel.uiState.cycleStates.forEach { cycle -> CycleView(cycle) }

        if (viewModel.uiState.fetchCyclesErrorStringId != null) {
            Text(
                text = stringResource(id = viewModel.uiState.fetchCyclesErrorStringId!!),
                color = ErrorRed
            )
        }

        if (viewModel.uiState.isFetchingCycles) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        } else if (viewModel.uiState.cycleStates.isEmpty() && viewModel.uiState.fetchCyclesErrorStringId == null) {
            Text(
                text = stringResource(id = R.string.routineHasNoCycles),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
            )
        }

        if (!viewModel.uiState.isFetchingCycles && viewModel.uiState.fetchCyclesErrorStringId == null) {
            Column(
                modifier = Modifier.padding(horizontal = 60.dp, vertical = 15.dp)
            ) {
                StartRoutineButton(viewModel, routineId, onNavigateToRoutineExecutionRequested)
            }
        }
    }
}

@Composable
private fun CycleView(cycle: CycleUiState) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colors.primary)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Text(
                text = cycle.cycle.name!!,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }

        val tadeo = cycle.cycle.repetitions!!
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 140.dp)
                .background(Color.DarkGray)
        ) {
            Text(
                text = stringResource(
                    id = if (tadeo == 1) R.string.repetitionDispaly else R.string.repetitionsDispaly,
                    tadeo
                ),
                color = Color.White,
                modifier = Modifier.padding(10.dp)
            )
        }
    }

    AdaptibleSimpleList(
        itemCount = cycle.exercises.size,
        itemPaddingDp = 0
    ) { index ->
        ExerciseView(cycle.exercises[index])
    }

    /*cycle.exercises.forEachIndexed { index, exercise ->
        ExerciseView(exercise)

        if (index < cycle.exercises.size - 1)
            Divider(color = Color.DarkGray)
    }*/

    if (cycle.fetchExercisesErrorStringId != null) {
        Text(
            text = stringResource(id = cycle.fetchExercisesErrorStringId),
            color = ErrorRed
        )
    }

    if (cycle.isFetchingExercises) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondaryVariant,
            modifier = Modifier.padding(top = 10.dp)
        )
    } else if (cycle.exercises.isEmpty() && cycle.fetchExercisesErrorStringId == null) {
        Text(
            text = stringResource(id = R.string.cycleHasNoExercises),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun ExerciseView(exercise: CycleExerciseApiModel) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.rutina),
            contentDescription = "exercise",
            alignment = Alignment.TopCenter,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .size(width = 140.dp, height = 100.dp)
                .padding(end = 10.dp)
        )

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = exercise.exercise!!.name!!,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (exercise.duration != null && exercise.duration > 0) {
                    Text(
                        text = stringResource(
                            if (exercise.duration == 1) R.string.secondDispaly else R.string.secondsDispaly,
                            exercise.duration
                        ),
                        color = GoldenBrown
                    )
                }

                if (exercise.repetitions != null && exercise.repetitions > 0) {
                    if (exercise.duration != null && exercise.duration > 0) {
                        Text(
                            text = " - ",
                            color = GoldenBrown
                        )
                    }

                    Text(
                        text = stringResource(
                            if (exercise.repetitions == 1) R.string.repetitionDispaly else R.string.repetitionsDispaly,
                            exercise.repetitions
                        ),
                        color = GoldenBrown
                    )
                }
            }
        }
    }
}

@Composable
fun StartRoutineButton(
    viewModel: RoutineViewModel,
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigateToRoutineExecutionRequested(routineId) }
    ) {
        Text(
            text = stringResource(id = R.string.startRoutine),
            modifier = Modifier.padding(vertical = 5.dp)
        )
    }
}