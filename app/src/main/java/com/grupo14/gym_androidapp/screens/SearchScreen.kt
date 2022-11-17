package com.grupo14.gym_androidapp.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.grupo14.gym_androidapp.MyDropDownMenu
import com.grupo14.gym_androidapp.api.models.Difficulty
import com.grupo14.gym_androidapp.viewmodels.SearchViewModel

@Composable
fun SearchScreen(
    onNavigate: (route: String) -> Unit,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current

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
                        text = viewModel.filterSearch,
                        hint = "Buscar rutina...", // TODO: Traducir
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) { search -> viewModel.filterSearch = search }

                    SearchBar(
                        text = viewModel.filterUsername,
                        hint = "Buscar creador...", // TODO: Traducir
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) { search -> viewModel.filterUsername = search }
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
                    selectedText = orderByStrings.elementAtOrNull(orderByValues.indexOfFirst { it == viewModel.filterOrderBy })
                        ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, CircleShape)
                ) { selectedString ->
                    val i = orderByStrings.indexOf(selectedString)
                    viewModel.filterOrderBy = if (i >= 0) orderByValues[i] else ""
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
                                selectedText = viewModel.filterCategory?.name ?: "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, CircleShape),
                                enabled = !categoryStrings.isEmpty()
                            ) { string ->
                                viewModel.filterCategory =
                                    viewModel.uiState.categories.find { it.name == string }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {

                        val difficulties = Difficulty.values()
                        val difficultyStrings =
                            difficulties.map { stringResource(it.stringResourceId) }

                        MyDropDownMenu(
                            label = "Dificultad",
                            elements = difficultyStrings,
                            selectedText = difficultyStrings.elementAtOrNull(difficulties.indexOfFirst { it == viewModel.filterDifficulty })
                                ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, CircleShape)
                        ) { dif ->
                            val i = difficultyStrings.indexOf(dif)
                            if (i >= 0)
                                viewModel.filterDifficulty = difficulties[i]
                        }
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
                    value = viewModel.filterRating.toFloat(),
                    config = RatingBarConfig().style(RatingBarStyle.HighLighted),
                    onValueChange = { viewModel.filterRating = it.toInt() },
                    onRatingChanged = { viewModel.filterRating = it.toInt() }
                )

                Button(
                    onClick = { viewModel.fuckingGo(onNavigate) },
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
    text: String,
    modifier: Modifier = Modifier,
    hint: String = "",
    onValueChanged: (String) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    var isHintDisplayed = hint != "" && text.isNullOrBlank()
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = { onValueChanged(it) },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged { isFocused = it.isFocused },
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





