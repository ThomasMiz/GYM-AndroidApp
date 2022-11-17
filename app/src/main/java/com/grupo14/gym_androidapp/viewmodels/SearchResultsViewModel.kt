package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.grupo14.gym_androidapp.api.GymRepository

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