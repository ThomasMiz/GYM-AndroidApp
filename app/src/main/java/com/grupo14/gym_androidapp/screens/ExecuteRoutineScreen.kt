package com.grupo14.gym_androidapp.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@Composable
fun PreviewExecutionScreen(
    routineId: Int,
    onNavigateToExecutionMode: (routineId: Int, mode : Int) -> Unit,
    onNavigateToRoutineRequested: (routineId: Int) -> Unit
) {
    val context = LocalContext.current
    var mode = 1
    Column(
        Modifier
            .padding(70.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Ready?",
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.h2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxHeight(0.6f)

        )

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                mode = if(mode == 1) 2 else 1
                Toast.makeText(context, "Modo cambiado a: ExecutionRoutineScreen$mode", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text(
                text = stringResource(id = R.string.changeMode),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.button,
                textAlign = TextAlign.Center,
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                when (mode) {
                    1 -> onNavigateToExecutionMode(routineId, mode)
                    2 -> onNavigateToExecutionMode(routineId, mode)
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.startRoutine),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.button,
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                onNavigateToRoutineRequested(routineId)
            }
        ) {
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
    onNavigateToFinishScreen: (id: Int, ticks : Int) -> Unit // To do: pass ticks
){
    val context = LocalContext.current

    val routUiState = viewModel.uiState
    val execUiState = viewModel.executionUiState

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var renderAgain by remember { mutableStateOf(false) }
    var ticks by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while(true) {
            delay(1000)
            ticks++
        }
    }

    // Load whole routine
    if (routUiState.currentRoutineId != routineId) {
        FullLoadingScreen()
        viewModel.fetchWholeRoutine(
            routineId = routineId,
            onFailure = { Toast.makeText(context, R.string.fetchRoutinesFailed, Toast.LENGTH_SHORT).show() },
            onFinish = { println("${viewModel.uiState}") }
        )
    }

    // Error
    if(routUiState.fetchRoutineErrorStringId != null){
        ExecuteScreenError(
            viewModel = viewModel,
            routineId = routineId,
            onNavigateToRoutineExecutionRequested = {id -> onNavigateToRoutine(id)}
        )
    }

    // Load the whole data in two parallel collections
    if(!viewModel.buildAuxCollections && routUiState.cycleStates.isNotEmpty() ){
        routUiState.cycleStates.forEach{ cycle ->
            var i=0
            if(cycle.exercises.isNotEmpty()){
                while(i < cycle.cycle.repetitions!!){
                    cycle.exercises.forEach{ exercise ->
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
    if( !renderAgain && execUiState.exercisesList.isNotEmpty() && execUiState.cyclesList.isNotEmpty()) {

        var animationPlayed by remember { mutableStateOf(false) }
        var lastTime by remember { mutableStateOf(0) }
        val excAnimationSec = execUiState.exercisesList[execUiState.exercisesListIndex.value].duration
        var animationDuration by remember { mutableStateOf(excAnimationSec?.times(1000) ?: 0) }

        val currTime = (if (animationPlayed) excAnimationSec else lastTime)?.let {
            animateIntAsState(
                targetValue = it,
                animationSpec = tween (
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
        Modifier.padding(horizontal = 10.dp, vertical = 25.dp).fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
            state = listState,
            modifier = Modifier.weight(1f).padding(2.dp)
        ){
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
                        text = java.lang.StringBuilder()
                            .append(TimeUnit.SECONDS.toMinutes((excAnimationSec - currTime.value).toLong()).toString())
                            .append(":")
                            .append(if (seconds < 10) "0" else "")
                            .append(seconds.toString())
                            .toString(),
                        color = MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.h1
                    )
                }
            }

            if (execUiState.exercisesList[execUiState.exercisesListIndex.value].repetitions!! > 0) {
                Text(
                    text = execUiState.exercisesList[execUiState.exercisesListIndex.value].repetitions.toString() + " " + stringResource(id = R.string.times),
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

            Button(
                modifier = Modifier.fillMaxWidth(0.8f).weight(1f).padding(10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                enabled = execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value > 0,
                onClick = {
                    viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value - 1)
                    coroutineScope.launch {
                        listState.animateScrollToItem(index = execUiState.exercisesListIndex.value, -7)
                    }
                    renderAgain = true
                }
            ) {
                Icon(Icons.Filled.FastRewind, tint = MaterialTheme.colors.secondary, contentDescription = null)
            }

            Button(
                modifier = Modifier.fillMaxWidth(0.8f).weight(1f).padding(10.dp),
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
                }
            ) {
                if (animationPlayed)
                    Icon(Icons.Filled.Pause, contentDescription = null, tint = MaterialTheme.colors.secondary)
                else
                    Icon(Icons.Filled.PlayArrow,  contentDescription = null, tint = MaterialTheme.colors.secondary)
            }

                Button(
                    modifier = Modifier.fillMaxWidth(0.8f).weight(1f).padding(10.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    onClick = {
                        if (execUiState.exercisesList.isNotEmpty() && execUiState.exercisesListIndex.value < execUiState.exercisesList.size - 1) {
                            viewModel.setExercisesListIndex(execUiState.exercisesListIndex.value + 1)
                            coroutineScope.launch { listState.animateScrollToItem(index = execUiState.exercisesListIndex.value, scrollOffset = -7)}
                            renderAgain = true
                        } else {
                            onNavigateToFinishScreen(routineId, ticks) // To Do: pass ticks to the stack
                        }
                    }
                ) {
                    Icon(Icons.Filled.FastForward, tint = MaterialTheme.colors.secondary, contentDescription = null)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    shape = RoundedCornerShape(18.dp),
                    onClick = {
                        onNavigateToRoutine(routineId)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.button,
                    )
                }
            }
    }
    } else if(renderAgain) {
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
    ){
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
                modifier =Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = if(exerciseDescription != null) "$exerciseDescription" else stringResource(id = R.string.NoDesc),
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

@Composable
fun ExecutionRoutineScreen2(
    viewModel: ExecuteRoutineViewModel,
    routineId: Int,
    onNavigateToRoutine: (id: Int) -> Unit,
    onNavigateToFinishScreen: (id: Int) -> Unit
){

    Column() {

        Text(
            text = "Pedro 2",
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.button,
        )

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = RoundedCornerShape(18.dp),
            onClick = {
                onNavigateToFinishScreen(routineId)
            }
        ) {
            Text(
                text =  "Irme a finish",
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.button,
            )
        }
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
                text = stringResource(R.string.tryAgain),
                color = Color.Black
            )
        }
    }
}


@Composable
fun ExecutionFinishedScreen(
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit,
    millis: Long = 3600 // TO DO: Stack parameter
) {

    // https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
    val hms = String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
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
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(25.dp),
                onClick = { onNavigateToRoutineExecutionRequested(routineId) }) {
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
