package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun ExecuteRoutineScreen(
    viewModel: ExecuteRoutineViewModel,
    routineId: Int,
    onNavigateToRoutineExecutionRequested: (id: Int) -> Unit
) {

    if (viewModel.uiState.fetchingRoutineId != routineId) {
        viewModel.start(routineId)
    }

    val isRunning = true

    TopBar(Modifier.padding(top = 20.dp, bottom = 16.dp))
    Column(
        Modifier
            .padding(horizontal = 10.dp, vertical = 30.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        Image(
            painter = painterResource(id = R.drawable.rutina),
            contentDescription = "routine",
            alignment = Alignment.TopCenter,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10))
        )

        Text(
            text = "Ciclo 1" + ": " + "Salto",
            color = MaterialTheme.colors.secondary,
            fontSize = 24.sp,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Divider(
            color = MaterialTheme.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 10.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = " 30 repeticiones",
                color = MaterialTheme.colors.secondary,
                fontSize = 16.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

            Text(
                text = "40 segundos",
                color = MaterialTheme.colors.secondary,
                fontSize = 16.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }

        if (!isRunning) {
            Text(
                text = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBCCCCCCCCCCCCCCCCCC",
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(horizontal = 50.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            )

            Button(
                onClick = { /*To Do */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {

                Text(
                    "Comenzar",
                    Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 25.sp
                )
            }
        } else {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {

                // Timer de MIERDA

                Button(
                    onClick = { println("PEDRO ${viewModel.uiState}") },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {

                    Text(
                        "Comenzar",
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 14.sp
                    )
                }

            }

            Row() {

                Button(
                    onClick = { /*To Do */ },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) {

                    Text(
                        "Pausar",
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = { /*To Do */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {

                    Text(
                        "Finalizar",
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 14.sp
                    )
                }
            }
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
fun ExecutionFinished(routineId: Int, onNavigateToRoutineExecutionRequested: (id: Int) -> Unit) {
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
