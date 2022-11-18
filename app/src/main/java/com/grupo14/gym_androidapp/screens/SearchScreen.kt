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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.grupo14.gym_androidapp.MyDropDownMenu
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.models.Difficulty
import com.grupo14.gym_androidapp.viewmodels.SearchViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun SearchScreen(
    onNavigate: (route: String) -> Unit,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current

    if (!viewModel.uiState.startedLoadingCategories) {
        val loadCategoriesFailed = stringResource(id = R.string.loadCategoriesFailed)
        viewModel.fetchCategories() {
            Toast.makeText(context, loadCategoriesFailed, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = CenterHorizontally
    ) {
        Card(
            backgroundColor = Color.White,
            border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.searchTitle),
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

                SearchBar(
                    text = viewModel.filterSearch,
                    hint = stringResource(id = R.string.searchRoutinePlaceholder),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) { search -> viewModel.filterSearch = search }

                SearchBar(
                    text = viewModel.filterUsername,
                    hint = stringResource(id = R.string.searchCreatorPlaceholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) { search -> viewModel.filterUsername = search }

                val orderByStrings = listOf(
                    stringResource(id = R.string.orderByDateString),
                    stringResource(id = R.string.orderByRatingString),
                    stringResource(id = R.string.orderByDifficultyString),
                    stringResource(id = R.string.orderByCategoryString)
                )
                val orderByValues = listOf("date", "score", "difficulty", "category")

                MyDropDownMenu(
                    label = stringResource(id = R.string.orderByPlaceholder),
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
                    text = stringResource(id = R.string.filters),
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
                                if (category.name != null)
                                    categoryStrings.add(category.name)
                            }

                            MyDropDownMenu(
                                label = stringResource(id = R.string.categories),
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
                            label = stringResource(id = R.string.difficulty),
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
                    text = stringResource(id = R.string.rating),
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
                        text = stringResource(id = R.string.search),
                        Modifier
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 25.sp
                    )
                }

                val clearFiltersDialogState = rememberMaterialDialogState()
                Button(
                    onClick = { clearFiltersDialogState.show() },
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                ) {
                    Text(
                        text = stringResource(id = R.string.clearFiltersButton),
                        Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 18.sp
                    )
                }

                MaterialDialog(dialogState = clearFiltersDialogState, buttons = {
                    positiveButton(res = R.string.yes) {
                        viewModel.clearFilters()
                    }
                    negativeButton(res = R.string.no)
                }) {
                    title(res = R.string.clearFiltersDialogTitle)
                    message(res = R.string.clearFiltersDialogMessage)
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
    fontSize: TextUnit = TextUnit.Unspecified,
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
            textStyle = TextStyle(color = Color.Black, fontSize = fontSize),
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
                fontSize = fontSize,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}





