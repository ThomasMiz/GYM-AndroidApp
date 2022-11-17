package com.grupo14.gym_androidapp.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.grupo14.gym_androidapp.viewmodels.SearchResultsViewModel

@Composable
fun SearchResultsScreen(
    onNavigate: (route: String) -> Unit,
    viewModel: SearchResultsViewModel
) {
    Text("hola")
}