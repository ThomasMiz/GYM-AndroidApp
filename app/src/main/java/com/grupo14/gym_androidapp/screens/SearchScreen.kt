package com.grupo14.gym_androidapp.screens

import android.util.Log
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.grupo14.gym_androidapp.R

@Composable
fun SearchScreen() {
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            // Prevent BottomNavigationView overlap content
            .padding(bottom = 60.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Icon(
            painter = painterResource(id = R.drawable.icon),
            null,
            Modifier
                .size(80.dp)
                .fillMaxWidth()
            ,
            tint = MaterialTheme.colors.secondary
        )
        Card(
            backgroundColor = Color.White,
            border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
            shape = RoundedCornerShape(8.dp),
            elevation = 12.dp,
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

                Row(){
                    SearchBar(
                        hint = "Buscar rutina...",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) { }

                    SearchBar(
                        hint = "Buscar creador...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) { }
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

                Row(){
                    DropDownMenu(
                        listOf("Aerobico", "Musculacion", "Etc"),
                        Modifier
                            .fillMaxWidth(0.5f)
                            .background(Color.White, CircleShape),
                        label = "Categorias"
                    )
                    DropDownMenu(
                        listOf("Facil", "Intermedio", "Dificil"),
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White, CircleShape),
                        label = "Dificultad"
                    )
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
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp)
                        .padding(top=20.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text(
                        "Buscar",
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
    onSearch: (String) -> Unit = {}
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
                    isHintDisplayed = !it.isFocused
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
fun DropDownMenu( elements : List<String>, modifier: Modifier = Modifier, label : String) {

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
            onValueChange = { selectedText = it },
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






