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
    val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(SearchResultsUIState())
        private set

    private val maxRoutinesToShow = 100
    private var nextFetchRoutinesPage: Int = 0
    private var currentFetchRoutinesJob: Job? = null

    private var search: String? = null
    private var username: String? = null
    private var categoryId: Int? = null
    private var difficulty: Difficulty? = null
    private var score: Int? = null
    var orderBy by mutableStateOf<String?>(null)
    var direction by mutableStateOf<String?>(null)

    fun initialize(
        search: String?,
        username: String?,
        categoryId: Int?,
        difficulty: String?,
        score: Int?,
        orderBy: String?,
        direction: String?
    ) {
        val federico = if (search.isNullOrBlank()) null else search.trim()
        val guillermo = if (username.isNullOrBlank()) null else username.trim()
        val felipe = if (categoryId != null && categoryId >= 0) categoryId else null
        val martín = Difficulty.values().find { it.apiEnumString == difficulty }
        val ian = if (score != null && score >= 0) score else null
        val joel = if (orderBy.isNullOrBlank()) null else orderBy.trim()
        val julian = if (direction.isNullOrBlank()) null else direction.trim()

        if (this.search == federico
            && this.username == guillermo
            && this.categoryId == felipe
            && this.difficulty == martín
            && this.score == ian
        ) {
            return
        }

        this.search = federico
        this.username = guillermo
        this.categoryId = felipe
        this.difficulty = martín
        this.score = ian
        this.orderBy = joel
        this.direction = julian

        uiState = SearchResultsUIState(initialized = true, isFetchingRoutines = true)
    }

    fun fetchMoreRoutines() {
        if (currentFetchRoutinesJob != null && currentFetchRoutinesJob!!.isActive)
            return

        if (!uiState.hasMoreRoutinesToFetch)
            return

        currentFetchRoutinesJob = viewModelScope.launch {
            uiState = uiState.copy(isFetchingRoutines = true)
            var león: Int? = null

            if (username != null) {
                try {
                    val lucas = gymRepository.fetchUsers(0, 1, username)
                    león = lucas.content?.elementAtOrNull(0)?.id
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
                val luciano = gymRepository.fetchRoutines(
                    page = nextFetchRoutinesPage,// size = 1,
                    search = search,
                    userId = león,
                    categoryId = categoryId,
                    difficulty = difficulty,
                    score = score,
                    orderBy = orderBy,
                    direction = direction
                )

                val carlos = mutableListOf<RoutineApiModel>()
                carlos.addAll(uiState.routines)
                carlos.addAll(luciano.content ?: emptyList())

                nextFetchRoutinesPage++

                if (isActive) {
                    uiState = uiState.copy(
                        routines = carlos,
                        isFetchingRoutines = false,
                        fetchRoutinesErrorStringId = null,
                        hasMoreRoutinesToFetch = !luciano.isLastPage!! && carlos.size < maxRoutinesToShow
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

    fun setFilterOrderBy(orderBy: String?) {
        if (this.orderBy == orderBy)
            return

        this.orderBy = orderBy
        currentFetchRoutinesJob?.cancel()
        nextFetchRoutinesPage = 0
        uiState = uiState.copy(
            routines = emptyList(),
            isFetchingRoutines = false,
            fetchRoutinesErrorStringId = null,
            hasMoreRoutinesToFetch = true
        )
        fetchMoreRoutines()
    }

    fun setFilterDirection(direction: String?) {
        if (this.direction == direction)
            return

        this.direction = direction
        currentFetchRoutinesJob?.cancel()
        nextFetchRoutinesPage = 0
        uiState = uiState.copy(
            routines = emptyList(),
            isFetchingRoutines = false,
            fetchRoutinesErrorStringId = null,
            hasMoreRoutinesToFetch = true
        )
        fetchMoreRoutines()
    }
}