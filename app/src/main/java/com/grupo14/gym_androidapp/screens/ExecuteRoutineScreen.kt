package com.grupo14.gym_androidapp.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.FullLoadingScreen
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.viewmodels.ExecuteRoutineViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@Composable
fun PreviewExecutionScreen(
    routineId: Int,
    onNavigateToExecutionMode: (routineId: Int, mode: Int) -> Unit,
    onNavigateToRoutineRequested: (routineId: Int) -> Unit
) {
    val alan = LocalContext.current
    var axel = 1
    Column(
        Modifier
            .padding(30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.ready),
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.h2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(10.dp)
                .align(CenterHorizontally)

        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.rutina),
                contentDescription = null,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10))
            )
        }

        Button(modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                axel = if (axel == 1) 2 else 1
                Toast.makeText(
                    alan, R.string.executionMode, Toast.LENGTH_SHORT
                ).show()
            }) {
            Text(
                text = stringResource(id = R.string.changeMode),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.button,
                textAlign = TextAlign.Center,
            )
        }

        Button(modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                when (axel) {
                    1 -> onNavigateToExecutionMode(routineId, axel)
                    2 -> onNavigateToExecutionMode(routineId, axel)
                }
            }) {
            Text(
                text = stringResource(id = R.string.startRoutine),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.button,
            )
        }

        Button(modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Red),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                onNavigateToRoutineRequested(routineId)
            }) {
            Text(
                text = stringResource(id = R.string.cancel),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.button,
            )
        }


    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ExecutionRoutineScreen1(
    viewModel: ExecuteRoutineViewModel,
    routineId: Int,
    onNavigateToRoutine: (id: Int) -> Unit,
    onNavigateToFinishScreen: (id: Int, seconds: Int) -> Unit,
) {
    val brian = LocalContext.current

    val daniel = viewModel.uiState
    val dario = viewModel.executionUiState

    val diego = rememberLazyListState()
    val guillermo = rememberCoroutineScope()

    var dustin by remember { mutableStateOf(false) }
    var dylan by remember { mutableStateOf(0) }

    val juan = rememberMaterialDialogState()
    MaterialDialog(dialogState = juan, buttons = {
        positiveButton(res = R.string.ok) {
            onNavigateToRoutine(routineId)
        }
        negativeButton(res = R.string.cancel)
    }) {
        title(res = R.string.cancelExecution)
        message(res = R.string.confirmCancelationRoutine)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            dylan++
        }
    }

    // Load whole routine
    if (daniel.currentRoutineId != routineId) {
        viewModel.fetchWholeRoutine(routineId = routineId, onFailure = {
            Toast.makeText(brian, R.string.fetchRoutinesFailed, Toast.LENGTH_SHORT).show()
        }, onFinish = { println("${viewModel.uiState}") })
    }

    if (daniel.isFetchingRoutine) {
        FullLoadingScreen()
    } else {
        // Error
        if (daniel.fetchRoutineErrorStringId != null) {
            ExecuteScreenError(viewModel = viewModel,
                routineId = routineId,
                onNavigateToRoutineExecutionRequested = { id -> onNavigateToRoutine(id) })
        }

        // Load the whole data in two parallel collections
        if (!viewModel.buildAuxCollections && daniel.cycleStates.isNotEmpty()) {
            daniel.cycleStates.forEach { cycle ->
                var esteban = 0
                if (cycle.exercises.isNotEmpty()) {
                    while (esteban < cycle.cycle.repetitions!!) {
                        cycle.exercises.forEach { exercise ->
                            dario.exercisesList.add(exercise)
                            dario.cyclesList.add(cycle.cycle)
                        }
                        esteban++
                    }
                }
            }
            viewModel.buildAuxCollections = true
        }

        // Render
        if (!dustin && dario.exercisesList.isNotEmpty() && dario.cyclesList.isNotEmpty()) {

            var animationPlayed by remember { mutableStateOf(false) }
            var lastTime by remember { mutableStateOf(0) }
            val excAnimationSec =
                dario.exercisesList[dario.exercisesListIndex.value].duration
            var animationDuration by remember { mutableStateOf(excAnimationSec?.times(1000) ?: 0) }

            val currTime = (if (animationPlayed) excAnimationSec else lastTime)?.let {
                animateIntAsState(
                    targetValue = it, animationSpec = tween(
                        durationMillis = if (animationPlayed) animationDuration else 0,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )
            }

            LaunchedEffect(key1 = true) {
                animationPlayed = true
            }



            Column(
                Modifier
                    .padding(horizontal = 10.dp, vertical = 25.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
                horizontalAlignment = CenterHorizontally
            ) {

                TopBar(Modifier.padding(top = 5.dp, bottom = 5.dp))

                Spacer(modifier = Modifier.padding(vertical = 20.dp))

                daniel.routine?.name?.let {
                    Text(
                        text = it.uppercase(),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 40.sp,
                        style = MaterialTheme.typography.h1,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                // Display all the exercises
                LazyColumn(
                    state = diego, modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    items(dario.exercisesList.size) { i ->
                        ExerciseDisplayList(
                            exerciseName = dario.exercisesList[i].exercise?.name,
                            cycleName = dario.cyclesList[i].name,
                            exerciseDescription = dario.exercisesList[i].exercise?.detail,
                            exerciseType = dario.exercisesList[i].exercise?.type,
                            selected = i == dario.exercisesListIndex.value,
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (currTime != null && dario.exercisesList[dario.exercisesListIndex.value].duration!! > 0) {
                        val seconds = (excAnimationSec?.minus(currTime.value))?.rem(60)?.let {
                            TimeUnit.SECONDS.toSeconds(it.toLong())
                        }
                        if (excAnimationSec != null && seconds != null) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = java.lang.StringBuilder().append(
                                    TimeUnit.SECONDS.toMinutes((excAnimationSec - currTime.value).toLong())
                                        .toString()
                                ).append(":").append(if (seconds < 10) "0" else "")
                                    .append(seconds.toString()).toString(),
                                color = MaterialTheme.colors.secondary,
                                style = MaterialTheme.typography.h3,
                                fontSize = 30.sp,
                            )
                        }
                    }

                    if (dario.exercisesList[dario.exercisesListIndex.value].repetitions!! > 0) {
                        Text(
                            text = dario.exercisesList[dario.exercisesListIndex.value].repetitions.toString() + " " + stringResource(
                                id = R.string.times
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary,
                            style = MaterialTheme.typography.h3,
                            fontSize = 30.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {

                    Button(modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .weight(1f)
                        .padding(10.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        enabled = dario.exercisesList.isNotEmpty() && dario.exercisesListIndex.value > 0,
                        onClick = {
                            viewModel.setExercisesListIndex(dario.exercisesListIndex.value - 1)
                            guillermo.launch {
                                diego.animateScrollToItem(
                                    index = dario.exercisesListIndex.value, -7
                                )
                            }
                            dustin = true
                        }) {
                        Icon(
                            Icons.Filled.FastRewind,
                            tint = MaterialTheme.colors.secondary,
                            contentDescription = null
                        )
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .weight(1f)
                        .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(18.dp),
                        enabled = dario.exercisesList[dario.exercisesListIndex.value].duration!! > 0,
                        onClick = {
                            if (animationPlayed) {
                                if (currTime != null) {
                                    lastTime = currTime.value
                                }
                                animationPlayed = false
                            } else {
                                if (excAnimationSec != null) {
                                    animationDuration = (excAnimationSec - lastTime) * 1000
                                }
                                animationPlayed = true
                            }
                        }) {
                        if (animationPlayed) Icon(
                            Icons.Filled.Pause,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondary
                        )
                        else Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondary
                        )
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .weight(1f)
                        .padding(10.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        onClick = {
                            if (dario.exercisesList.isNotEmpty() && dario.exercisesListIndex.value < dario.exercisesList.size - 1) {
                                viewModel.setExercisesListIndex(dario.exercisesListIndex.value + 1)
                                guillermo.launch {
                                    diego.animateScrollToItem(
                                        index = dario.exercisesListIndex.value,
                                        scrollOffset = -7
                                    )
                                }
                                dustin = true
                            } else {
                                onNavigateToFinishScreen(routineId, dylan)
                            }
                        }) {
                        Icon(
                            Icons.Filled.FastForward,
                            tint = MaterialTheme.colors.secondary,
                            contentDescription = null
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Button(modifier = Modifier.fillMaxWidth(0.5f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                        shape = RoundedCornerShape(18.dp),
                        onClick = {
                            juan.show()
                        }) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = MaterialTheme.colors.secondary,
                            style = MaterialTheme.typography.button,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else if (dustin) {
            dustin = false
        }
    }
}

@Composable
fun ExerciseDisplayList(
    exerciseName: String?,
    cycleName: String?,
    exerciseType: String?,
    exerciseDescription: String?,
    selected: Boolean = false,
) {
    val color: Color = when {
        selected -> Black
        else -> White
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14))
            .background(
                when {
                    selected -> MaterialTheme.colors.primary
                    else -> Gray
                }
            )
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "$cycleName: $exerciseName",
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.3f)
            )

            Divider(
                color = MaterialTheme.colors.secondary,
                thickness = 1.dp,
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = if (exerciseDescription != null) "$exerciseDescription" else stringResource(
                        id = R.string.NoDesc
                    ),
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
    Spacer(modifier = Modifier.padding(vertical = 5.dp))
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ExecutionRoutineScreen2(
    viewModel: ExecuteRoutineViewModel,
    routineId: Int,
    onNavigateToRoutine: (id: Int) -> Unit,
    onNavigateToFinishScreen: (id: Int, seconds: Int) -> Unit
) {
    val context = LocalContext.current

    var seconds by remember { mutableStateOf(0) }

    val execUiState = viewModel.executionUiState
    val routUiState = viewModel.uiState

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    val cancelDialogState = rememberMaterialDialogState()
    MaterialDialog(dialogState = cancelDialogState, buttons = {
        positiveButton(res = R.string.ok) {
            onNavigateToRoutine(routineId)
        }
        negativeButton(res = R.string.cancel)
    }) {
        title(res = R.string.cancelExecution)
        message(res = R.string.confirmCancelationRoutine)
    }

    /*
        0 -> no exc
        1 -> timeAndRepExc
        2 -> timeExc
        3 -> repExc
     */
    var currentExcType by remember { mutableStateOf(0) }

    if (routUiState.currentRoutineId != routineId) {
        viewModel.fetchWholeRoutine(routineId = routineId, onFailure = {
            Toast.makeText(context, R.string.fetchRoutinesFailed, Toast.LENGTH_SHORT).show()
        }, onFinish = { println("${viewModel.uiState}") })
    }

    if (routUiState.isFetchingRoutine) {
        FullLoadingScreen()
    } else {
        // Error
        if (routUiState.fetchRoutineErrorStringId != null) {
            ExecuteScreenError(viewModel = viewModel,
                routineId = routineId,
                onNavigateToRoutineExecutionRequested = { id -> onNavigateToRoutine(id) })
        }

        // Load the whole data in two parallel collections
        if (!viewModel.buildAuxCollections && routUiState.cycleStates.isNotEmpty()) {
            routUiState.cycleStates.forEach { cycle ->
                var i = 0
                if (cycle.exercises.isNotEmpty()) {
                    while (i < cycle.cycle.repetitions!!) {
                        cycle.exercises.forEach { exercise ->
                            execUiState.exercisesList.add(exercise)
                            execUiState.cyclesList.add(cycle.cycle)
                        }
                        i++
                    }
                }
            }
            viewModel.buildAuxCollections = true
            val exc = execUiState.exercisesList[execUiState.exercisesListIndex.value]

            if (exc.duration != 0 && exc.repetitions != 0) currentExcType = 1
            else if (exc.duration != 0) currentExcType = 2
            else if (exc.repetitions != 0) currentExcType = 3
            else {
                Toast.makeText(context, R.string.serverUnknownError, Toast.LENGTH_SHORT).show()
                onNavigateToRoutine(routineId)
            }
        }

        if (currentExcType == 1 && execUiState.exercisesList.isNotEmpty() && execUiState.cyclesList.isNotEmpty()) {

            val animDurationSec =
                execUiState.exercisesList[execUiState.exercisesListIndex.value].duration
            var animationPlayed by remember { mutableStateOf(false) }
            var lastPercentage by remember { mutableStateOf(1f) }
            var lastTime by remember { mutableStateOf(0) }
            var animationDuration by remember { mutableStateOf(animDurationSec?.times(1000) ?: 0) }
            LaunchedEffect(key1 = true) {
                animationPlayed = true
            }

            val currentPercentage = animateFloatAsState(
                targetValue = if (animationPlayed) 0f else lastPercentage, animationSpec = tween(
                    durationMillis = if (animationPlayed) animationDuration else 0,
                    delayMillis = 0,
                    easing = LinearEasing
                )
            )

            val currentTime = (if (animationPlayed) animDurationSec else lastTime)?.let {
                animateIntAsState(
                    targetValue = it, animationSpec = tween(
                        durationMillis = if (animationPlayed) animationDuration else 0,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )
            }

            Column(
                Modifier
                    .padding(horizontal = 10.dp, vertical = 25.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.Top),
                horizontalAlignment = CenterHorizontally
            ) {

                TopBar(Modifier.padding(top = 5.dp, bottom = 5.dp))

                routUiState.routine?.name?.let {
                    Text(
                        text = it.uppercase(),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 40.sp,
                        style = MaterialTheme.typography.h1,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rutina),
                        contentDescription = null,
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(90.dp)
                            .clip(RoundedCornerShape(10))
                    )
                }

                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        execUiState.cyclesList[execUiState.exercisesListIndex.value].name?.let {
                            Text(
                                text = it.uppercase(),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(modifier = Modifier.padding(horizontal = 25.dp))

                        execUiState.exercisesList[execUiState.exercisesListIndex.value].exercise?.name?.let {
                            Text(
                                text = it.uppercase(),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                    }

                    Divider(
                        color = MaterialTheme.colors.secondary,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val seconds = (animDurationSec?.minus(currentTime?.value!!))?.rem(
                            60
                        )?.let { TimeUnit.SECONDS.toSeconds(it.toLong()) }
                        if (currentTime != null && animDurationSec != null && seconds != null) {
                            Text(
                                text = java.lang.StringBuilder().append(
                                    TimeUnit.SECONDS.toMinutes((animDurationSec - currentTime.value).toLong())
                                        .toString()
                                ).append(":").append(if (seconds < 10) "0" else "")
                                    .append(seconds.toString()).toString(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.secondary,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(15.dp)
                            )
                        }

                        Text(
                            text = execUiState.exercisesList[execUiState.exercisesListIndex.value].repetitions.toString() + " " + stringResource(
                                R.string.times
                            ).uppercase(),
                            color = MaterialTheme.colors.secondary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.padding(15.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(264.dp)
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colors.primaryVariant),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 64.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp, Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = White),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    if (currentTime != null && animationPlayed) {
                                        lastTime = currentTime.value

                                        lastPercentage = currentPercentage.value
                                        animationPlayed = false
                                    } else {
                                        if (animDurationSec != null) {
                                            animationDuration = (animDurationSec - lastTime) * 1000
                                        }
                                        animationPlayed = true
                                    }
                                }) {
                                if (animationPlayed) {
                                    Icon(
                                        Icons.Filled.Pause,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = White),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0,
                                    onClick = {
                                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0) {
                                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value - 1)
                                            val exercise =
                                                execUiState.exercisesList[execUiState.exercisesListIndex.value]
                                            currentExcType =
                                                if (exercise.duration != 0 && exercise.repetitions != 0) {
                                                    4
                                                } else if (exercise.duration == 0) {
                                                    5
                                                } else {
                                                    6
                                                }
                                        }
                                    }) {
                                    Icon(
                                        Icons.Filled.FastRewind,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }

                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    onClick = {
                                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value < execUiState.exercisesList.size - 1) {
                                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value + 1)
                                            val exercise =
                                                execUiState.exercisesList[execUiState.exercisesListIndex.value]
                                            currentExcType =
                                                if (exercise.duration != 0 && exercise.repetitions != 0) {
                                                    4
                                                } else if (exercise.duration == 0) {
                                                    5
                                                } else {
                                                    6
                                                }
                                        } else {
                                            onNavigateToFinishScreen(routineId, seconds)
                                        }
                                    }) {
                                    Icon(
                                        Icons.Filled.FastForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }
                            }

                            Button(modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    cancelDialogState.show()
                                }) {
                                Text(
                                    text = stringResource(R.string.cancel).uppercase(),
                                    color = MaterialTheme.colors.secondary,
                                    style = MaterialTheme.typography.button,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }

                }

            }
        } else if (currentExcType == 2 && execUiState.exercisesList.isNotEmpty() && execUiState.cyclesList.isNotEmpty()) {

            Column(
                Modifier
                    .padding(horizontal = 10.dp, vertical = 25.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopBar(Modifier.padding(top = 5.dp, bottom = 5.dp))

                routUiState.routine?.name?.let {
                    Text(
                        text = it.uppercase(),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 40.sp,
                        style = MaterialTheme.typography.h1,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rutina),
                        contentDescription = null,
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(90.dp)
                            .clip(RoundedCornerShape(10))
                    )
                }

                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        execUiState.cyclesList[execUiState.exercisesListIndex.value].name?.let {
                            Text(
                                text = it.uppercase(),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(modifier = Modifier.padding(horizontal = 25.dp))

                        execUiState.exercisesList[execUiState.exercisesListIndex.value].exercise?.name?.let {
                            Text(
                                text = it.uppercase(),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                    }

                    Divider(
                        color = MaterialTheme.colors.secondary,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = execUiState.exercisesList[execUiState.exercisesListIndex.value].repetitions.toString() + " " + stringResource(
                                R.string.times
                            ).uppercase(),
                            color = MaterialTheme.colors.secondary,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.padding(15.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(264.dp)
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colors.primaryVariant),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 64.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp, Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = White),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0,
                                    onClick = {
                                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0) {
                                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value - 1)
                                            val exercise =
                                                execUiState.exercisesList[execUiState.exercisesListIndex.value]
                                            currentExcType =
                                                if (exercise.duration != 0 && exercise.repetitions != 0) {
                                                    4
                                                } else if (exercise.duration == 0) {
                                                    5
                                                } else {
                                                    6
                                                }
                                        }
                                    }) {
                                    Icon(
                                        Icons.Filled.FastRewind,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }

                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    onClick = {
                                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value < execUiState.exercisesList.size - 1) {
                                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value + 1)
                                            val exercise =
                                                execUiState.exercisesList[execUiState.exercisesListIndex.value]
                                            currentExcType =
                                                if (exercise.duration != 0 && exercise.repetitions != 0) {
                                                    4
                                                } else if (exercise.duration == 0) {
                                                    5
                                                } else {
                                                    6
                                                }
                                        } else {
                                            onNavigateToFinishScreen(routineId, seconds)
                                        }
                                    }) {
                                    Icon(
                                        Icons.Filled.FastForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }
                            }

                            Button(modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    cancelDialogState.show()
                                }) {
                                Text(
                                    text = stringResource(R.string.cancel).uppercase(),
                                    color = MaterialTheme.colors.secondary,
                                    style = MaterialTheme.typography.button,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }

                }

            }

        } else if (currentExcType == 3 && execUiState.exercisesList.isNotEmpty() && execUiState.cyclesList.isNotEmpty()) {

            val animDurationSec =
                execUiState.exercisesList[execUiState.exercisesListIndex.value].duration
            var animationPlayed by remember { mutableStateOf(false) }
            var lastPercentage by remember { mutableStateOf(1f) }
            var lastTime by remember { mutableStateOf(0) }
            var animationDuration by remember { mutableStateOf(animDurationSec?.times(1000) ?: 0) }

            LaunchedEffect(key1 = true) {
                animationPlayed = true
            }


            val currentPercentage = animateFloatAsState(
                targetValue = if (animationPlayed) 0f else lastPercentage, animationSpec = tween(
                    durationMillis = if (animationPlayed) animationDuration else 0,
                    delayMillis = 0,
                    easing = LinearEasing
                )
            )

            val currentTime = (if (animationPlayed) animDurationSec else lastTime)?.let {
                animateIntAsState(
                    targetValue = it, animationSpec = tween(
                        durationMillis = if (animationPlayed) animationDuration else 0,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )
            }

            Column(
                Modifier
                    .padding(horizontal = 10.dp, vertical = 25.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopBar(Modifier.padding(top = 5.dp, bottom = 5.dp))

                routUiState.routine?.name?.let {
                    Text(
                        text = it.uppercase(),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 40.sp,
                        style = MaterialTheme.typography.h1,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rutina),
                        contentDescription = null,
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(90.dp)
                            .clip(RoundedCornerShape(10))
                    )
                }

                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        execUiState.cyclesList[execUiState.exercisesListIndex.value].name?.let {
                            Text(
                                text = it.uppercase(),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(modifier = Modifier.padding(horizontal = 25.dp))

                        execUiState.exercisesList[execUiState.exercisesListIndex.value].exercise?.name?.let {
                            Text(
                                text = it.uppercase(),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                    }

                    Divider(
                        color = MaterialTheme.colors.secondary,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val seconds = (animDurationSec?.minus(currentTime?.value!!))?.rem(
                            60
                        )?.let { TimeUnit.SECONDS.toSeconds(it.toLong()) }
                        if (currentTime != null && animDurationSec != null && seconds != null) {
                            Text(
                                text = java.lang.StringBuilder().append(
                                    TimeUnit.SECONDS.toMinutes((animDurationSec - currentTime.value).toLong())
                                        .toString()
                                ).append(":").append(if (seconds < 10) "0" else "")
                                    .append(seconds.toString()).toString(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.secondary,
                                fontSize = 40.sp,
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(264.dp)
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colors.primaryVariant),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, CenterVertically),
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 64.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp, Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = White),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    if (currentTime != null && animationPlayed) {
                                        lastTime = currentTime.value

                                        lastPercentage = currentPercentage.value
                                        animationPlayed = false
                                    } else {
                                        if (animDurationSec != null) {
                                            animationDuration = (animDurationSec - lastTime) * 1000
                                        }
                                        animationPlayed = true
                                    }
                                }) {
                                if (animationPlayed) {
                                    Icon(
                                        Icons.Filled.Pause,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = White),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0,
                                    onClick = {
                                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0) {
                                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value - 1)
                                            val exercise =
                                                execUiState.exercisesList[execUiState.exercisesListIndex.value]
                                            currentExcType =
                                                if (exercise.duration != 0 && exercise.repetitions != 0) {
                                                    4
                                                } else if (exercise.duration == 0) {
                                                    5
                                                } else {
                                                    6
                                                }
                                        }
                                    }) {
                                    Icon(
                                        Icons.Filled.FastRewind,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }

                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    onClick = {
                                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value < execUiState.exercisesList.size - 1) {
                                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value + 1)
                                            val exercise =
                                                execUiState.exercisesList[execUiState.exercisesListIndex.value]
                                            currentExcType =
                                                if (exercise.duration != 0 && exercise.repetitions != 0) {
                                                    4
                                                } else if (exercise.duration == 0) {
                                                    5
                                                } else {
                                                    6
                                                }
                                        } else {
                                            onNavigateToFinishScreen(routineId, seconds)
                                        }
                                    }) {
                                    Icon(
                                        Icons.Filled.FastForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary,
                                    )
                                }
                            }

                            Button(modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    cancelDialogState.show()
                                }) {
                                Text(
                                    text = stringResource(R.string.cancel).uppercase(),
                                    color = MaterialTheme.colors.secondary,
                                    style = MaterialTheme.typography.button,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }

                }

            }

        } else if (currentExcType == 4) currentExcType = 1
        else if (currentExcType == 5) currentExcType = 2
        else if (currentExcType == 6) currentExcType = 3
    }
}

@Composable
private fun ExecuteScreenError(
    viewModel: ExecuteRoutineViewModel,
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit
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
            onClick = { onNavigateToRoutineExecutionRequested(routineId) },
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.tryAgain), color = Color.Black
            )
        }
    }
}


@Composable
fun ExecutionFinishedScreen(
    routineId: Int,
    onNavigateToRoutine: (id: Int) -> Unit,
    seconds: Int
) {
    val context = LocalContext.current

    if (seconds < 0) {
        Toast.makeText(
            context, stringResource(id = R.string.fetchRoutinesFailed), Toast.LENGTH_SHORT
        ).show()
        onNavigateToRoutine(routineId)
    }
    val millisToL = (seconds * 1000).toLong()
    // https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
    val hms = String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millisToL),
        TimeUnit.MILLISECONDS.toMinutes(millisToL) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                millisToL
            )
        ),
        TimeUnit.MILLISECONDS.toSeconds(millisToL) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                millisToL
            )
        )
    )
    println(hms)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(Modifier.padding(top = 20.dp, bottom = 16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(id = R.string.executionFinished).uppercase(),
                color = MaterialTheme.colors.secondary,
                maxLines = 1,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier
                    .height(120.dp)
                    .width(264.dp)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.primaryVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            ) {
                Text(
                    text = hms,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = stringResource(id = R.string.timeTaken).uppercase(),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 64.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(25.dp),
                onClick = { onNavigateToRoutine(routineId) }) {
                Text(
                    text = stringResource(id = R.string.finish).uppercase(),
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            }
        }
    }
}


// TopBar for some execution screens
@Composable
fun TopBar(modifier: Modifier) {

    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .padding(3.dp),
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Logo",
                tint = MaterialTheme.colors.secondary
            )
            Text(
                text = "ENTRENAGRATIS.ES".uppercase(),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        LinearProgressIndicator(
            progress = 0.45f,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primaryVariant
        )
    }
}
