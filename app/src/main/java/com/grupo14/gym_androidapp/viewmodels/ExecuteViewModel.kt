package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.CycleApiModel
import com.grupo14.gym_androidapp.api.models.CycleExerciseApiModel
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class CycleState(
    val cycle: CycleApiModel,
    val exercises: List<CycleExerciseApiModel> = listOf(),
    val isFetchingExercises: Boolean = false,
    val fetchExercisesErrorStringId: Int? = null,
)

data class RoutineState(
    val routine: RoutineApiModel? = null,
    val isFetchingRoutine: Boolean = false,
    val fetchingRoutineId: Int? = null,
    val fetchRoutineErrorStringId: Int? = null,

    val isFavorited: Boolean = false,
    val isFetchinigFavorite: Boolean = false,

    val currentRating: Float = 0f,
    val isFetchingRating: Boolean = false,

    val isExecuting: Boolean = false,
    val cycleStates: List<CycleState> = listOf(),
    val isFetchingCycles: Boolean = false,
    val fetchCyclesErrorStringId: Int? = null,
)

class ExecuteRoutineViewModel (
    private val gymRepository: GymRepository
) : ViewModel() {

    var uiState by mutableStateOf(RoutineState())
        private set

    private var currentFetchRoutineJob: Job? = null
    private var currentFetchFavoriteJob: Job? = null
    private var currentFetchRatingJob: Job? = null
    private var currentFetchCycleJob: Job? = null

    fun start(routineId: Int) {
        if (uiState.fetchingRoutineId != routineId) {
            currentFetchRoutineJob?.cancel()
            currentFetchFavoriteJob?.cancel()
            currentFetchRatingJob?.cancel()
            currentFetchCycleJob?.cancel()
            uiState = RoutineState(fetchingRoutineId = routineId)
            fetchRoutine(routineId)
            fetchCycles(routineId)
            switchToExecuteView()
        }
    }

    fun fetchRoutine(routineId: Int) {
        if (currentFetchRoutineJob != null) {
            if (currentFetchRoutineJob!!.isActive && uiState.fetchingRoutineId == routineId)
                return

            currentFetchRoutineJob?.cancel()
            currentFetchFavoriteJob?.cancel()
        }
        currentFetchRoutineJob = viewModelScope.launch {
            try {

                uiState = RoutineState(
                    routine = null,
                    isFetchingRoutine = true,
                    fetchingRoutineId = routineId,
                    fetchRoutineErrorStringId = null,
                )

                val routineResult = gymRepository.fetchRoutine(routineId)

                if (uiState.fetchingRoutineId == routineId) {
                    fetchIsFavorite(routineId)
                    fetchRating(routineId)

                    uiState = uiState.copy(
                        routine = routineResult,
                        isFetchingRoutine = false,
                    )
                }
            } catch (e: Exception) {
                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        routine = null,
                        isFetchingRoutine = false,
                        fetchRoutineErrorStringId = R.string.fetchRoutineFailed,
                    )
                }
            }
        }
    }

    private fun fetchIsFavorite(routineId: Int) {
        if (currentFetchFavoriteJob != null) {
            if (currentFetchFavoriteJob!!.isActive && uiState.fetchingRoutineId == routineId)
                return

            currentFetchFavoriteJob?.cancel()
        }
        currentFetchFavoriteJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isFetchinigFavorite = true,
                    isFavorited = false
                )

                var isFavorite = false
                var i = 0
                while (true) {
                    val favs = gymRepository.fetchCurrentUserFavorites(i++)

                    if (favs.content!!.any { it.id == routineId }) {
                        isFavorite = true
                        break
                    }

                    if (favs.isLastPage!!)
                        break
                }

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchinigFavorite = false,
                        isFavorited = isFavorite
                    )
                }
            } catch (e: Exception) {
                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchinigFavorite = false,
                        isFavorited = false
                    )
                }
            }
        }
    }

    private fun fetchRating(routineId: Int) {
        if (currentFetchRatingJob != null) {
            if (currentFetchRatingJob!!.isActive && uiState.fetchingRoutineId == routineId)
                return

            currentFetchRatingJob?.cancel()
        }
        currentFetchRatingJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isFetchingRating = true,
                    currentRating = 0f
                )

                var rating = 0
                var i = 0
                while (true) {
                    val revs = gymRepository.fetchCurrentUserReviews(
                        page = i++,
                        orderBy = "date",
                        direction = "desc"
                    )

                    val rat = revs.content!!.find { it.routine!!.id == routineId } // 🐀
                    if (rat != null) {
                        rating = rat.score!!
                        break
                    }

                    if (revs.isLastPage!!)
                        break
                }

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingRating = false,
                        currentRating = rating.toFloat()
                    )
                }
            } catch (e: Exception) {
                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingRating = false,
                        currentRating = 0f
                    )
                }
            }
        }
    }

    private fun fetchCycles(routineId: Int) {
        if (currentFetchCycleJob != null) {
            if (currentFetchCycleJob!!.isActive && uiState.fetchingRoutineId == routineId)
                return

            currentFetchCycleJob?.cancel()
        }
        currentFetchCycleJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isFetchingCycles = true,
                    cycleStates = mutableListOf(),
                    fetchCyclesErrorStringId = null
                )

                var i = 0
                while (true) {
                    val cys = gymRepository.fetchRoutineCycles(
                        routineId = routineId,
                        page = i++,
                        orderBy = "order",
                        direction = "asc"
                    )

                    if (uiState.fetchingRoutineId != routineId)
                        break

                    val newCycleStates: MutableList<CycleState> = mutableListOf()
                    newCycleStates.addAll(uiState.cycleStates)

                    cys.content!!.forEach { cycle ->
                        val cycleIndex = uiState.cycleStates.size
                        val cycleState = CycleState(
                            cycle = cycle,
                            isFetchingExercises = true
                        )
                        newCycleStates.add(cycleState)
                        uiState = uiState.copy(
                            cycleStates = newCycleStates
                        )
                        fetchCycleExercises(cycleState) { newCycleState ->
                            try {
                                val ncs: MutableList<CycleState> = mutableListOf()
                                ncs.addAll(uiState.cycleStates)
                                ncs[cycleIndex] = newCycleState
                                uiState = uiState.copy(
                                    cycleStates = ncs
                                )
                            } catch (e: Exception) {
                                // nada lol
                            }
                        }
                    }

                    if (cys.isLastPage!!)
                        break
                }

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingCycles = false,
                    )
                }
            } catch (e: Exception) {
                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingCycles = false,
                        fetchCyclesErrorStringId = R.string.fetchCyclesFailed
                    )
                }
            }
        }
    }

    private fun fetchCycleExercises(cycleState: CycleState, onReassign: (CycleState) -> Unit) {
        viewModelScope.launch {
            var cs = cycleState.copy(isFetchingExercises = true)
            onReassign(cs)
            try {
                var i = 0
                while (true) {
                    val exs = gymRepository.fetchCycleExercises(
                        cycleId = cs.cycle.id!!,
                        page = i++,
                        orderBy = "order",
                        direction = "asc"
                    )

                    val newList: MutableList<CycleExerciseApiModel> = mutableListOf()
                    newList.addAll(cs.exercises)
                    exs.content!!.forEach { exercise ->
                        newList.add(exercise)
                    }

                    cs = cs.copy(exercises = newList)
                    onReassign(cs)

                    if (exs.isLastPage!!)
                        break
                }

                cs = cs.copy(isFetchingExercises = false)
                onReassign(cs)
            } catch (e: Exception) {
                cs = cs.copy(
                    isFetchingExercises = false,
                    fetchExercisesErrorStringId = R.string.fetchExerciseFailed
                )
                onReassign(cs)
            }
        }
    }

    fun switchToExecuteView() {
        uiState = uiState.copy(isExecuting = true)
    }

}
