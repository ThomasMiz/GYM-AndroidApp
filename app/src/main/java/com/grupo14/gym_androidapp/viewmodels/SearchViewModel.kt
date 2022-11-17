package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.SanitizeAndShit
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.Category
import com.grupo14.gym_androidapp.api.models.Difficulty
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SearchUIState(
    val isLoadingCategories: Boolean = false,
    val startedLoadingCategories: Boolean = false,
    val categories: List<Category> = emptyList()
)

class SearchViewModel(
    val gymRepository: GymRepository = GymRepository()
) : ViewModel() {
    var uiState by mutableStateOf(SearchUIState())
        private set

    var filterSearch by mutableStateOf("")
    var filterUsername by mutableStateOf("")
    var filterCategory by mutableStateOf<Category?>(null)
    var filterDifficulty by mutableStateOf<Difficulty?>(null)
    var filterRating by mutableStateOf(0)
    var filterOrderBy by mutableStateOf("")

    private var currentCategoriesJob: Job? = null

    fun fetchCategories(onFailure: (errorMessage: String) -> Unit) {
        if (currentCategoriesJob != null && currentCategoriesJob!!.isActive)
            return

        currentCategoriesJob?.cancel()
        currentCategoriesJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoadingCategories = true,
                    startedLoadingCategories = true
                )
                val cats = gymRepository.fetchCategories(0, 50)

                uiState = uiState.copy(
                    isLoadingCategories = false,
                    categories = cats.content ?: emptyList()
                )

                if (uiState.categories.isEmpty())
                    onFailure("No categories found.") // TODO cambiar
            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // To Do: Cambiar
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                onFailure(errorMessage)
            } catch (e: Exception) {
                onFailure("pero la PUCHA, qué _MIERDA_ le paso al server??") // TODO: cambiar
            }
        }
    }

    fun fuckingGo(onNavigate: (route: String) -> Unit) {
        filterSearch = SanitizeAndShit(filterSearch)
        filterUsername = SanitizeAndShit(filterUsername)

        var route = "search/results?"

        if (!filterSearch.isNullOrBlank()) route += "search=${filterSearch}&"
        if (!filterUsername.isNullOrBlank()) route += "username=${filterUsername}&"
        if (filterCategory != null) route += "categoryId=${filterCategory?.id ?: -1}&"
        if (filterDifficulty != null) route += "difficulty=${filterDifficulty?.apiEnumString ?: ""}&"
        if (filterRating != null && filterRating > 0) route += "score=${filterRating}&"
        if (!filterOrderBy.isNullOrBlank()) route += "orderBy=${filterOrderBy}&"

        route = route.trimEnd('&', '?')
        onNavigate(route)
    }
}