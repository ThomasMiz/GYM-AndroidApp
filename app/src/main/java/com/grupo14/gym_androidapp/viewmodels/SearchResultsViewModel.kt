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
        val newsearch = if (search.isNullOrBlank()) null else search.trim()
        val newusername = if (username.isNullOrBlank()) null else username.trim()
        val newcategoryId = if (categoryId != null && categoryId >= 0) categoryId else null
        val newdifficulty = Difficulty.values().find { it.apiEnumString == difficulty }
        val newscore = if (score != null && score >= 0) score else null
        val neworderBy = if (orderBy.isNullOrBlank()) null else orderBy.trim()
        val newdirection = if (direction.isNullOrBlank()) null else direction.trim()

        if (this.search == newsearch
            && this.username == newusername
            && this.categoryId == newcategoryId
            && this.difficulty == newdifficulty
            && this.score == newscore
        ) {
            return
        }

        this.search = newsearch
        this.username = newusername
        this.categoryId = newcategoryId
        this.difficulty = newdifficulty
        this.score = newscore
        this.orderBy = neworderBy
        this.direction = newdirection

        uiState = SearchResultsUIState(initialized = true, isFetchingRoutines = true)
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
                    page = nextFetchRoutinesPage,// size = 1,
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