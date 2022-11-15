package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class HomeUiState(
    val favorites: List<RoutineApiModel> = listOf(),
    val isFetchingFavorites: Boolean = false,
    val fetchFavoritesErrorStringId: Int? = null,
    val hasMoreFavoritesToFetch: Boolean = true
)

class HomeViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    private var nextFetchFavoritesPage: Int = 0
    private var currentFetchFavoritesJob: Job? = null

    fun fetchMoreFavorites() {
        if (currentFetchFavoritesJob != null && currentFetchFavoritesJob!!.isActive)
            return

        if (!uiState.hasMoreFavoritesToFetch)
            return

        currentFetchFavoritesJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isFetchingFavorites = true)

                val moreFavorites = gymRepository.fetchCurrentUserFavorites(nextFetchFavoritesPage)

                val newFavoritesList = mutableListOf<RoutineApiModel>()
                newFavoritesList.addAll(uiState.favorites)
                newFavoritesList.addAll(moreFavorites.content!!)

                nextFetchFavoritesPage++

                uiState = uiState.copy(
                    favorites = newFavoritesList,
                    isFetchingFavorites = false,
                    fetchFavoritesErrorStringId = null,
                    hasMoreFavoritesToFetch = !moreFavorites.isLastPage!!
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isFetchingFavorites = false,
                    fetchFavoritesErrorStringId = R.string.fetchFavoritesFailed,
                    hasMoreFavoritesToFetch = false
                )
            }
        }
    }
}