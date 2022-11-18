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
    val context = LocalContext.current
    var mode = 1
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
            modifier = Modifier.padding(10.dp).align(CenterHorizontally)

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
                mode = if (mode == 1) 2 else 1
                Toast.makeText(
                    context, R.string.executionMode, Toast.LENGTH_SHORT
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
                when (mode) {
                    1 -> onNavigateToExecutionMode(routineId, mode)
                    2 -> onNavigateToExecutionMode(routineId, mode)
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
    onNavigateToFinishScreen: (id: Int) -> Unit,
) {
    val context = LocalContext.current

    val routUiState = viewModel.uiState
    val execUiState = viewModel.executionUiState

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var renderAgain by remember { mutableStateOf(false) }
    var millis by remember { mutableStateOf(0L) }

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

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            millis++
        }
    }

    // Load whole routine
    if (routUiState.currentRoutineId != routineId) {
        FullLoadingScreen()
        viewModel.fetchWholeRoutine(routineId = routineId, onFailure = {
            Toast.makeText(context, R.string.fetchRoutinesFailed, Toast.LENGTH_SHORT).show()
        }, onFinish = { println("${viewModel.uiState}") })
    }

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
    }

    // Render
    if (!renderAgain && execUiState.exercisesList.isNotEmpty() && execUiState.cyclesList.isNotEmpty()) {

        var animationPlayed by remember { mutableStateOf(false) }
        var lastTime by remember { mutableStateOf(0) }
        val excAnimationSec =
            execUiState.exercisesList[execUiState.exercisesListIndex.value].duration
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


        TopBar(Modifier.padding(top = 20.dp, bottom = 16.dp))

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

            routUiState.routine?.name?.let {
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
                state = listState, modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            ) {
                items(execUiState.exercisesList.size) { i ->
                    ExerciseDisplayList(
                        exerciseName = execUiState.exercisesList[i].exercise?.name,
                        cycleName = execUiState.cyclesList[i].name,
                        exerciseDescription = execUiState.exercisesList[i].exercise?.detail,
                        exerciseType = execUiState.exercisesList[i].exercise?.type,
                        selected = i == execUiState.exercisesListIndex.value,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (currTime != null && execUiState.exercisesList[execUiState.exercisesListIndex.value].duration!! > 0) {
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
                            style = MaterialTheme.typography.h1
                        )
                    }
                }

                if (execUiState.exercisesList[execUiState.exercisesListIndex.value].repetitions!! > 0) {
                    Text(
                        text = execUiState.exercisesList[execUiState.exercisesListIndex.value].repetitions.toString() + " " + stringResource(
                            id = R.string.times
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.h3,
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
                    enabled = execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0,
                    onClick = {
                        viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value - 1)
                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                index = execUiState.exercisesListIndex.value, -7
                            )
                        }
                        renderAgain = true
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
                    enabled = execUiState.exercisesList[execUiState.exercisesListIndex.value].duration!! > 0,
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
                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value < execUiState.exercisesList.size - 1) {
                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value + 1)
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    index = execUiState.exercisesListIndex.value, scrollOffset = -7
                                )
                            }
                            renderAgain = true
                        } else {
                            onNavigateToFinishScreen(routineId)
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
                        cancelDialogState.show()
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
    } else if (renderAgain) {
        renderAgain = false
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
    onNavigateToFinishScreen: (id: Int) -> Unit
) {
    val context = LocalContext.current

    var millis by remember { mutableStateOf(-1) }

    val execUiState = viewModel.executionUiState
    val routUiState = viewModel.uiState

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            millis++
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
        FullLoadingScreen()
        viewModel.fetchWholeRoutine(routineId = routineId, onFailure = {
            Toast.makeText(context, R.string.fetchRoutinesFailed, Toast.LENGTH_SHORT).show()
        }, onFinish = { println("${viewModel.uiState}") })
    }

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
                                        onNavigateToFinishScreen(routineId)
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
                                        onNavigateToFinishScreen(routineId)
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

        val animDurationSec = execUiState.exercisesList[execUiState.exercisesListIndex.value].duration
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
                                        onNavigateToFinishScreen(routineId)
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
    routineId: Int, onNavigateToRoutine: (id: Int) -> Unit, millis: Long = 3600
) {
    val context = LocalContext.current

    if (millis < 0) {
        Toast.makeText(
            context, stringResource(id = R.string.fetchRoutinesFailed), Toast.LENGTH_SHORT
        ).show()
        onNavigateToRoutine(routineId)
    }

    // https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
    val hms = String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                millis
            )
        ),
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                millis
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
