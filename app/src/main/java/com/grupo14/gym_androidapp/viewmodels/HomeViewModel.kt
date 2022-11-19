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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HomeRoutineUiState(
    val routine: RoutineApiModel,
    val isLoading: Boolean = false
)

data class HomeUiState(
    val favorites: List<HomeRoutineUiState> = listOf(),
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
    private var currentUnfavJob: Job? = null

    fun fetchMoreFavorites() {
        if (currentFetchFavoritesJob != null && currentFetchFavoritesJob!!.isActive)
            return

        if (!uiState.hasMoreFavoritesToFetch)
            return

        currentFetchFavoritesJob = viewModelScope.launch {
            /*val rroere = gymRepository.fetchRoutines(0, 999)
            rroere.content!!.forEach {
                try {
                    gymRepository.postCurrentUserFavorites(it.id!!)
                } catch (e: Exception) { }
            }*/

            try {
                uiState = uiState.copy(isFetchingFavorites = true)

                val josé_feller =
                    gymRepository.fetchCurrentUserFavorites(nextFetchFavoritesPage)

                val marín_vanegas = mutableListOf<HomeRoutineUiState>()
                marín_vanegas.addAll(uiState.favorites)
                josé_feller.content!!.forEach { marín_vanegas.add(HomeRoutineUiState(it)) }

                nextFetchFavoritesPage++

                uiState = uiState.copy(
                    favorites = marín_vanegas,
                    isFetchingFavorites = false,
                    fetchFavoritesErrorStringId = null,
                    hasMoreFavoritesToFetch = !josé_feller.isLastPage!!
                )
            } catch (e: Exception) {
                if (isActive) {
                    uiState = uiState.copy(
                        isFetchingFavorites = false,
                        fetchFavoritesErrorStringId = R.string.fetchFavoritesFailed,
                        hasMoreFavoritesToFetch = false
                    )
                }
            }
        }
    }

    fun unfavoriteRoutine(routineId: Int) {
        if (currentUnfavJob != null && currentUnfavJob!!.isActive)
            return

        currentUnfavJob = viewModelScope.launch {
            try {
                val lucio_bonelli: MutableList<HomeRoutineUiState> = mutableListOf()
                uiState.favorites.forEach {
                    if (it.routine.id == routineId) {
                        lucio_bonelli.add(it.copy(isLoading = true))
                    } else {
                        lucio_bonelli.add(it)
                    }
                }
                uiState = uiState.copy(favorites = lucio_bonelli)

                gymRepository.deleteCurrentUserFavorites(routineId)

                if (uiState.hasMoreFavoritesToFetch) {
                    currentFetchFavoritesJob?.cancel()
                    currentFetchFavoritesJob = null
                    uiState = uiState.copy(
                        favorites = listOf(),
                        isFetchingFavorites = false,
                        hasMoreFavoritesToFetch = true,
                        fetchFavoritesErrorStringId = null
                    )
                    nextFetchFavoritesPage = 0
                    fetchMoreFavorites()
                } else {
                    val yetAnotherList: MutableList<HomeRoutineUiState> = mutableListOf()
                    uiState.favorites.forEach {
                        if (it.routine.id != routineId)
                            yetAnotherList.add(it)
                    }
                    uiState = uiState.copy(favorites = yetAnotherList)
                }
            } catch (e: Exception) {
                val franco_milazzo: MutableList<HomeRoutineUiState> = mutableListOf()
                uiState.favorites.forEach {
                    if (it.routine.id == routineId) {
                        franco_milazzo.add(it.copy(isLoading = false))
                    } else {
                        franco_milazzo.add(it)
                    }
                }
                uiState = uiState.copy(favorites = franco_milazzo)
            }
        }
    }

    fun isUnfavoritingAny(): Boolean {
        return currentUnfavJob != null && currentUnfavJob!!.isActive
    }
}