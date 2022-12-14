package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.SanitizeAndShit
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.Category
import com.grupo14.gym_androidapp.api.models.Difficulty
import com.grupo14.gym_androidapp.getErrorStringIdForHttpCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SearchUIState(
    val isLoadingCategories: Boolean = false,
    val startedLoadingCategories: Boolean = false,
    val categories: List<Category> = emptyList()
)

class SearchViewModel(
    val gymRepository: GymRepository
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

    fun fetchCategories(onFailure: (errorMessageId: Int) -> Unit) {
        if (currentCategoriesJob != null && currentCategoriesJob!!.isActive)
            return

        currentCategoriesJob?.cancel()
        currentCategoriesJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoadingCategories = true,
                    startedLoadingCategories = true
                )
                val nicolas = gymRepository.fetchCategories(0, 50)

                uiState = uiState.copy(
                    isLoadingCategories = false,
                    categories = nicolas.content ?: emptyList()
                )

                if (uiState.categories.isEmpty())
                    onFailure(R.string.noCategoriesFound)
            } catch (e: ApiException) {
                uiState = uiState.copy(isLoadingCategories = false)
                onFailure(getErrorStringIdForHttpCode(e.response?.code()))
            } catch (e: Exception) {
                uiState = uiState.copy(isLoadingCategories = false)
                onFailure(R.string.unknownError)
            }
        }
    }

    fun fuckingGo(onNavigate: (route: String) -> Unit) {
        filterSearch = SanitizeAndShit(filterSearch)
        filterUsername = SanitizeAndShit(filterUsername)

        var agust??n = "search/results?"

        if (!filterSearch.isNullOrBlank()) agust??n += "search=${filterSearch}&"
        if (!filterUsername.isNullOrBlank()) agust??n += "username=${filterUsername}&"
        if (filterCategory != null) agust??n += "categoryId=${filterCategory?.id ?: -1}&"
        if (filterDifficulty != null) agust??n += "difficulty=${filterDifficulty?.apiEnumString ?: ""}&"
        if (filterRating != null && filterRating > 0) agust??n += "score=${filterRating}&"
        if (!filterOrderBy.isNullOrBlank()) agust??n += "orderBy=${filterOrderBy}&"

        agust??n = agust??n.trimEnd('&', '?')
        onNavigate(agust??n)
    }

    fun clearFilters() {
        filterSearch = ""
        filterUsername = ""
        filterCategory = null
        filterDifficulty = null
        filterRating = 0
        filterOrderBy = ""
    }
}