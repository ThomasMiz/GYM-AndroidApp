package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.Difficulty
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.getErrorStringIdForHttpCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SearchResultsUIState(
    val routines: List<RoutineApiModel> = listOf(),
    val isFetchingRoutines: Boolean = false,
    val fetchRoutinesErrorStringId: Int? = null,
    val hasMoreRoutinesToFetch: Boolean = true,
    val initialized: Boolean = false
)

class SearchResultsViewModel(
    val gymRepository: GymRepository = GymRepository()
) : ViewModel() {
    var uiState by mutableStateOf(SearchResultsUIState())
        private set

    private val maxRoutinesToShow = 100
    private var nextFetchRoutinesPage: Int = 0
    private var currentFetchRoutinesJob: Job? = null

    var search: String? = null
    var username: String? = null
    var categoryId: Int? = null
    var difficulty: Difficulty? = null
    var score: Int? = null
    var orderBy: String? = null
    var direction: String? = null

    fun initialize(
        search: String?,
        username: String?,
        categoryId: Int?,
        difficulty: String?,
        score: Int?,
        orderBy: String?,
        direction: String?
    ) {
        this.search = if (search.isNullOrBlank()) null else search.trim()
        this.username = if (username.isNullOrBlank()) null else username.trim()
        this.categoryId = if (categoryId != null && categoryId >= 0) categoryId else null
        this.difficulty = Difficulty.values().find { it.apiEnumString == difficulty }
        this.score = if (score != null && score >= 0) score else null
        this.orderBy = if (orderBy.isNullOrBlank()) null else orderBy.trim()
        this.direction = if (direction.isNullOrBlank()) null else direction.trim()

        uiState = SearchResultsUIState(initialized = true)
    }

    fun fetchMoreRoutines() {
        if (currentFetchRoutinesJob != null && currentFetchRoutinesJob!!.isActive)
            return

        if (!uiState.hasMoreRoutinesToFetch)
            return

        currentFetchRoutinesJob = viewModelScope.launch {
            uiState = uiState.copy(isFetchingRoutines = true)
            var userId: Int? = null

            if (username != null) {
                try {
                    val users = gymRepository.fetchUsers(0, 1, username)
                    userId = users.content?.elementAtOrNull(0)?.id
                } catch (e: ApiException) {
                    if (isActive) {
                        uiState = uiState.copy(
                            isFetchingRoutines = false,
                            fetchRoutinesErrorStringId = getErrorStringIdForHttpCode(e.response?.code()),
                            hasMoreRoutinesToFetch = false
                        )
                    }
                } catch (e: Exception) {
                    if (isActive) {
                        uiState = uiState.copy(
                            isFetchingRoutines = false,
                            fetchRoutinesErrorStringId = R.string.noUsersFoundBySearch,
                            hasMoreRoutinesToFetch = false
                        )
                    }
                }
            }

            try {
                val moreRoutines = gymRepository.fetchRoutines(
                    page = nextFetchRoutinesPage,
                    search = search,
                    userId = userId,
                    categoryId = categoryId,
                    difficulty = difficulty,
                    score = score,
                    orderBy = orderBy,
                    direction = direction
                )

                val newRoutinesList = mutableListOf<RoutineApiModel>()
                newRoutinesList.addAll(uiState.routines)
                newRoutinesList.addAll(moreRoutines.content ?: emptyList())

                nextFetchRoutinesPage++

                if (isActive) {
                    uiState = uiState.copy(
                        routines = newRoutinesList,
                        isFetchingRoutines = false,
                        fetchRoutinesErrorStringId = null,
                        hasMoreRoutinesToFetch = !moreRoutines.isLastPage!! && newRoutinesList.size < maxRoutinesToShow
                    )
                }
            } catch (e: ApiException) {
                if (isActive) {
                    uiState = uiState.copy(
                        isFetchingRoutines = false,
                        fetchRoutinesErrorStringId = getErrorStringIdForHttpCode(e.response?.code()),
                        hasMoreRoutinesToFetch = false
                    )
                }
            } catch (e: Exception) {
                if (isActive) {
                    uiState = uiState.copy(
                        isFetchingRoutines = false,
                        fetchRoutinesErrorStringId = R.string.fetchRoutinesFailed,
                        hasMoreRoutinesToFetch = false
                    )
                }
            }
        }
    }
}