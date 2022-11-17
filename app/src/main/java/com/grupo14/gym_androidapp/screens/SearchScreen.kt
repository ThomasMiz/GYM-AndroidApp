package com.grupo14.gym_androidapp.screens

import android.util.Log
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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.grupo14.gym_androidapp.AdaptibleList
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.ui.theme.DifficultyRed
import com.grupo14.gym_androidapp.ui.theme.StarYellow
import com.grupo14.gym_androidapp.viewmodels.*

@Composable
fun SearchScreen(onNavigate: (route: String) -> Unit, viewModel: SearchViewModel){
    val context = LocalContext.current

    if(viewModel.searchUIState.isSearching){
        println("Ahora estoy cargando")
        SearchScreenLoaded(viewModel = viewModel, loading = true)
    } else if(viewModel.searchUIState.searchFinished){
        println("Juju tengo resultados")
        viewModel.readyToDisplayResults()
        SearchScreenLoaded(viewModel = viewModel, loading = false)
        AdaptibleList(items = viewModel.routineUIState.routineList) { routine ->
            RoutineCardEntry(routine = routine)
        }
    } else {
        println("Nada pibe")
        viewModel.fetchCategories() {errorMessage ->  Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()}
        SearchScreenLoaded(viewModel = viewModel, loading = false)
    }
}


@Composable
fun SearchScreenLoaded(viewModel : SearchViewModel, loading : Boolean) {
    val context = LocalContext.current

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
            ){

                Text(
                    "Búsqueda",
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
                var searchRoutine by remember { mutableStateOf("")}
                var searchCreator by remember { mutableStateOf("")}

                Row(){

                    SearchBar(
                        hint = "Buscar rutina...",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) { search -> searchRoutine = search }

                    SearchBar(
                        hint = "Buscar creador...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) { search -> searchCreator = search }
                }
                DropDownMenu(
                    listOf("Fecha de creación", "Puntuación", "Dificultad", "Categoría"),
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, CircleShape),
                    label = "Ordenar por"
                )

                Text(
                    "Filtros",
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

                var filterCategory by remember { mutableStateOf("")}

                val categoryList = mutableListOf<String>()
                viewModel.routineUIState.categoryList.forEach{category -> if(category.name != null) categoryList.add(category.name) }

                var filterDifficulty by remember { mutableStateOf("")}

                Row(){

                    DropDownMenu(
                        categoryList,
                        Modifier
                            .fillMaxWidth(0.5f)
                            .background(Color.White, CircleShape),
                        label = "Categorias"
                    ) { string -> filterCategory = string }

                    DropDownMenu(
                        listOf("rookie", "beginner", "intermediate", "advanced", "expert"),
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White, CircleShape),
                        label = "Dificultad"
                    ) { string -> filterDifficulty = string}
                }

                Text(
                    "Clasificación",
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

                var rating = remember { mutableStateOf(0f) }

                RatingBar(
                    value = rating.value,
                    config = RatingBarConfig()
                        .style(RatingBarStyle.HighLighted),
                    onValueChange = {
                        rating.value = it
                    },
                    onRatingChanged = {
                        Log.d("TAG", "onRatingChanged: $it")
                    }
                )

                Button(
                    onClick = { viewModel.searchRoutines(
                        searchByName = searchRoutine,
                        searchByCreator = searchCreator,
                        orderBySelected = "",
                        filterByCategory = filterCategory,
                        filterByClassification = filterDifficulty,
                        filterByDifficulty = rating.toString(),
                        ) { errorMessage ->  Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show() }
                      },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    if(!loading) {
                        Text(
                            "Buscar",
                            Modifier
                                .padding(vertical = 8.dp),
                            color = MaterialTheme.colors.secondary,
                            fontSize = 25.sp
                        )
                    } else {
                        CircularProgressIndicator(color = MaterialTheme.colors.secondaryVariant)
                    }
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
    var text by remember { mutableStateOf("")}
    var isHintDisplayed by remember { mutableStateOf(hint != "")}
    val focusManager = LocalFocusManager.current

    Box(modifier=modifier){
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
        )
        if(isHintDisplayed){
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
fun DropDownMenu(
    elements : List<String>,
    modifier: Modifier = Modifier,
    label : String,
    onSelect: (String) -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }
    val suggestions = elements
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)  Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    val focusManager = LocalFocusManager.current

    Column(
        Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {
                selectedText = it
                onSelect(selectedText)
            },
            modifier = modifier
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = {Text(text= label, color = Color.Black)},
            placeholder = { Text(text = label, color = Color.Black) },
            singleLine = true,
            trailingIcon = {
                Icon(icon,null, Modifier.clickable { expanded = !expanded })
            },
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(color=Color.Black, fontSize = 15.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
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






