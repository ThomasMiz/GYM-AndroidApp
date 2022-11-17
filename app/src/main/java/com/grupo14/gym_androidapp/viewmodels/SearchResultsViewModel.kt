package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.ui.theme.DifficultyRed
import com.grupo14.gym_androidapp.ui.theme.StarYellow

data class SearchResultsUIState(
    val isSearching: Boolean = false,
    val searchFinished: Boolean = false,
)

class SearchResultsViewModel(
    val gymRepository: GymRepository = GymRepository()
) : ViewModel() {
    var uiState by mutableStateOf(SearchResultsUIState())
        private set
}

@Composable
private fun RoutineCardEntry(
    routine: RoutineApiModel,
) {
    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
        //.clickable(/*To do */)
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
