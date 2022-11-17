package com.grupo14.gym_androidapp.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.grupo14.gym_androidapp.MyDropDownMenu
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.SanitizeAndShit
import com.grupo14.gym_androidapp.api.models.Category
import com.grupo14.gym_androidapp.api.models.Difficulty
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.ui.theme.DifficultyRed
import com.grupo14.gym_androidapp.ui.theme.StarYellow
import com.grupo14.gym_androidapp.viewmodels.SearchViewModel

@Composable
fun SearchScreen(
    onNavigate: (route: String) -> Unit,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current

    var filterSearch by remember { mutableStateOf("") }
    var filterUsername by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf<Category?>(null) }
    var filterDifficulty by remember { mutableStateOf<String?>(null) }
    var filterRating by remember { mutableStateOf(0f) }
    var filterOrderBy by remember { mutableStateOf("") }

    if (!viewModel.uiState.startedLoadingCategories) {
        viewModel.fetchCategories() { // TODO: Traducir
            Toast.makeText(context, "No se pudieron cargar las categorias", Toast.LENGTH_SHORT)
                .show()
        }
    }

    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            backgroundColor = Color.White,
            border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = "Búsqueda", // TODO: Traducir
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp),
                )

                Divider(
                    color = MaterialTheme.colors.primary,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(0.8f)
                )


                Row() {
                    SearchBar(
                        hint = "Buscar rutina...", // TODO: Traducir
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) { search -> filterSearch = search }

                    SearchBar(
                        hint = "Buscar creador...", // TODO: Traducir
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) { search -> filterUsername = search }
                }

                val orderByStrings = listOf( // TODO: Traducir
                    "Fecha de creación",
                    "Puntuación",
                    "Dificultad",
                    "Categoría"
                )
                val orderByValues = listOf("date", "score", "difficulty", "category")

                MyDropDownMenu(
                    label = "Ordenar por", // TODO: Traducir
                    elements = orderByStrings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, CircleShape)
                ) { selectedString ->
                    val i = orderByStrings.indexOf(selectedString)
                    filterOrderBy = if (i >= 0) orderByValues[i] else ""
                }

                Text(
                    text = "Filtros", // TODO: Traducir
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp),
                )

                Divider(
                    color = MaterialTheme.colors.primary,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(0.8f)
                )

                Row {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.uiState.isLoadingCategories) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colors.secondaryVariant,
                                modifier = Modifier.padding(top = 25.dp)
                            )
                        } else {
                            val categoryStrings = mutableListOf<String>()
                            viewModel.uiState.categories.forEach { category ->
                                if (category.name != null) categoryStrings.add(category.name)
                            }

                            MyDropDownMenu(
                                label = "Categorias", // TODO: Traducir
                                elements = categoryStrings,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, CircleShape),
                                enabled = !categoryStrings.isEmpty()
                            ) { string ->
                                filterCategory =
                                    viewModel.uiState.categories.find { it.name == string }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        val difficultyStrings = listOf(
                            stringResource(Difficulty.ROOKIE.stringResourceId),
                            stringResource(Difficulty.BEGINNER.stringResourceId),
                            stringResource(Difficulty.INTERMEDIATE.stringResourceId),
                            stringResource(Difficulty.ADVANCED.stringResourceId),
                            stringResource(Difficulty.EXPERT.stringResourceId)
                        )

                        MyDropDownMenu(
                            label = "Dificultad",
                            elements = difficultyStrings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, CircleShape)
                        ) { string -> filterDifficulty = string }
                    }
                }

                Text(
                    text = "Clasificación",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 20.dp)
                        .padding(bottom = 10.dp),
                )

                Divider(
                    color = MaterialTheme.colors.primary,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(0.8f)
                )

                RatingBar(
                    value = filterRating,
                    config = RatingBarConfig().style(RatingBarStyle.HighLighted),
                    onValueChange = { filterRating = it },
                    onRatingChanged = { filterRating = it }
                )

                Button(
                    onClick = {
                        filterSearch = SanitizeAndShit(filterSearch)
                        filterUsername = SanitizeAndShit(filterUsername)

                        var route = "search/results?"

                        if (filterSearch.isNotBlank()) route += "search=${filterSearch}&"
                        if (filterUsername.isNotBlank()) route += "userId=${filterUsername.length}&"
                        if (filterCategory != null) route += "categoryId=${filterCategory?.id ?: -1}&"
                        if (filterDifficulty != null) route += "difficulty=${filterDifficulty}&"
                        if (filterRating > 0) route += "score=${filterRating}&"
                        if (filterOrderBy.isNotBlank()) route += "orderBy=${filterOrderBy}&"

                        route = route.trimEnd('&', '?')
                        onNavigate(route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = "Buscar", // TODO strings de mierda
                        Modifier
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 25.sp
                    )
                }

            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {},
) {
    var text by remember { mutableStateOf("") }
    var isHintDisplayed by remember { mutableStateOf(hint != "") }
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text == ""
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
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






