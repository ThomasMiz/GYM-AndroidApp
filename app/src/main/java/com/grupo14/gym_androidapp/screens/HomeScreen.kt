package com.grupo14.gym_androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.ui.theme.ErrorRed
import com.grupo14.gym_androidapp.ui.theme.FavoritePink
import com.grupo14.gym_androidapp.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToOtherScreen: (id: Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
        //.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.yourFavoriteRoutines),
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                //contentPadding = PaddingValues(5.dp),
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.uiState.favorites) { routine ->
                    FavoriteRoutineCard(routine) {
                        // TODO: On unfavorite button tapped
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
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
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

        var id by remember { mutableStateOf(1) }

        Surface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Button(onClick = { onNavigateToOtherScreen(id) }) {
                    Text(
                        text = stringResource(R.string.pedro),
                        fontSize = 30.sp
                    )
                }

                TextField(
                    value = id.toString(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { newValue -> id = newValue.toIntOrNull() ?: 0 },
                )
            }
        }
    }
}

@Composable
private fun FavoriteRoutineCard(routine: RoutineApiModel, onUnfavorited: () -> Unit) {
    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
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
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(onClick = { onUnfavorited() }) {
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