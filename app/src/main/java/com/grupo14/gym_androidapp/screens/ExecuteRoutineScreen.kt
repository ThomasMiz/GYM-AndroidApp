package com.grupo14.gym_androidapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.viewmodels.ExecuteRoutineViewModel


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
            shape = RoundedCornerShape(16.dp),
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
            shape = RoundedCornerShape(16.dp),
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
            shape = RoundedCornerShape(16.dp),
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

@Composable
fun ExecutionRoutineScreen1(
    viewModel: ExecuteRoutineViewModel,
    routineId: Int,
    onNavigateToRoutine: (id: Int) -> Unit,
    onNavigateToFinishScreen: (id: Int) -> Unit
){
    Column() {

        Text(
            text = "Pedro 1",
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.button,
        )

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = RoundedCornerShape(16.dp),
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
            shape = RoundedCornerShape(16.dp),
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
fun ExecutionFinishedScreen(routineId: Int, onNavigateToRoutineExecutionRequested: (id: Int) -> Unit) {
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
                    text = "hs/ms/segs",
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
                shape = RoundedCornerShape(29.dp),
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
