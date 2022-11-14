package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.FullLoadingScreen
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.viewmodels.RoutineViewModel

@Composable
fun RoutineScreen(
    viewModel: RoutineViewModel,
    routineId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
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
}

@Composable
fun RoutineScreenError(
    viewModel: RoutineViewModel,
    routineId: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
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
fun RoutineScreenLoaded(
    viewModel: RoutineViewModel,
    routineId: Int
) {
    Text(text = viewModel.uiState.routine!!.name!!)
}