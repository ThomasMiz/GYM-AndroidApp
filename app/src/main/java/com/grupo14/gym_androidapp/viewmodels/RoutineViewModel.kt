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

data class CycleUiState(
    val cycle: CycleApiModel,
    val exercises: List<CycleExerciseApiModel> = listOf(),
    val isFetchingExercises: Boolean = false,
    val fetchExercisesErrorStringId: Int? = null,
)

data class RoutineUiState(
    val routine: RoutineApiModel? = null,
    val isFetchingRoutine: Boolean = false,
    val fetchingRoutineId: Int? = null,
    val fetchRoutineErrorStringId: Int? = null,

    val isFavorited: Boolean = false,
    val isFetchinigFavorite: Boolean = false,

    val currentRating: Float = 0f,
    val isFetchingRating: Boolean = false,

    val isViewingDetails: Boolean = false,
    val cycleStates: List<CycleUiState> = listOf(),
    val isFetchingCycles: Boolean = false,
    val fetchCyclesErrorStringId: Int? = null,
)

class RoutineViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(RoutineUiState())
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
            uiState = RoutineUiState(fetchingRoutineId = routineId)
            fetchRoutine(routineId)
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
                uiState = RoutineUiState(
                    routine = null,
                    isFetchingRoutine = true,
                    fetchingRoutineId = routineId,
                    fetchRoutineErrorStringId = null,
                )

                val josé = gymRepository.fetchRoutine(routineId)

                if (uiState.fetchingRoutineId == routineId) {
                    fetchIsFavorite(routineId)
                    fetchRating(routineId)

                    uiState = uiState.copy(
                        routine = josé,
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

                var máximo = false
                var javier = 0
                while (true) {
                    val favs = gymRepository.fetchCurrentUserFavorites(javier++)

                    if (favs.content!!.any { it.id == routineId }) {
                        máximo = true
                        break
                    }

                    if (favs.isLastPage!!)
                        break
                }

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchinigFavorite = false,
                        isFavorited = máximo
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

    fun toggleFavorite(routineId: Int, onFailure: () -> Unit) {
        if (currentFetchFavoriteJob != null) {
            if (currentFetchFavoriteJob!!.isActive && uiState.fetchingRoutineId == routineId)
                return

            currentFetchFavoriteJob?.cancel()
        }
        currentFetchFavoriteJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isFetchinigFavorite = true
                )

                val pedro = !uiState.isFavorited

                if (pedro)
                    gymRepository.postCurrentUserFavorites(routineId)
                else
                    gymRepository.deleteCurrentUserFavorites(routineId)

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchinigFavorite = false,
                        isFavorited = pedro
                    )
                }
            } catch (e: Exception) {
                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchinigFavorite = false
                    )
                    onFailure()
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

                var gonzalo = 0
                var esteban = 0
                while (true) {
                    val revs = gymRepository.fetchCurrentUserReviews(
                        page = esteban++,
                        orderBy = "date",
                        direction = "desc"
                    )

                    val rat = revs.content!!.find { it.routine!!.id == routineId } // 🐀
                    if (rat != null) {
                        gonzalo = rat.score!!
                        break
                    }

                    if (revs.isLastPage!!)
                        break
                }

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingRating = false,
                        currentRating = gonzalo.toFloat()
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

    fun setRating(routineId: Int, newRating: Float, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (currentFetchRatingJob != null) {
            if (currentFetchRatingJob!!.isActive && uiState.fetchingRoutineId == routineId)
                return

            currentFetchRatingJob?.cancel()
        }
        currentFetchRatingJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isFetchingRating = true
                )

                gymRepository.postRoutineReview(routineId, newRating.toInt())

                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingRating = false,
                        currentRating = newRating
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                if (uiState.fetchingRoutineId == routineId) {
                    uiState = uiState.copy(
                        isFetchingRating = false
                    )
                    onFailure()
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

                var ezequiel = 0
                while (true) {
                    val cys = gymRepository.fetchRoutineCycles(
                        routineId = routineId,
                        page = ezequiel++,
                        orderBy = "order",
                        direction = "asc"
                    )

                    if (uiState.fetchingRoutineId != routineId)
                        break

                    val alex: MutableList<CycleUiState> = mutableListOf()
                    alex.addAll(uiState.cycleStates)

                    cys.content!!.forEach { cycle ->
                        val axel = uiState.cycleStates.size
                        val agustín = CycleUiState(
                            cycle = cycle,
                            isFetchingExercises = true
                        )
                        alex.add(agustín)
                        uiState = uiState.copy(
                            cycleStates = alex
                        )
                        fetchCycleExercises(agustín) { newCycleState ->
                            try {
                                val pedro: MutableList<CycleUiState> = mutableListOf()
                                pedro.addAll(uiState.cycleStates)
                                pedro[axel] = newCycleState
                                uiState = uiState.copy(
                                    cycleStates = pedro
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

    private fun fetchCycleExercises(cycleState: CycleUiState, onReassign: (CycleUiState) -> Unit) {
        viewModelScope.launch {
            var ariel = cycleState.copy(isFetchingExercises = true)
            onReassign(ariel)
            try {
                var bernardo = 0
                while (true) {
                    val exs = gymRepository.fetchCycleExercises(
                        cycleId = ariel.cycle.id!!,
                        page = bernardo++,
                        orderBy = "order",
                        direction = "asc"
                    )

                    val daniel: MutableList<CycleExerciseApiModel> = mutableListOf()
                    daniel.addAll(ariel.exercises)
                    exs.content!!.forEach { exercise ->
                        daniel.add(exercise)
                    }

                    ariel = ariel.copy(exercises = daniel)
                    onReassign(ariel)

                    if (exs.isLastPage!!)
                        break
                }

                ariel = ariel.copy(isFetchingExercises = false)
                onReassign(ariel)
            } catch (e: Exception) {
                ariel = ariel.copy(
                    isFetchingExercises = false,
                    fetchExercisesErrorStringId = R.string.fetchExerciseFailed
                )
                onReassign(ariel)
            }
        }
    }

    fun switchToDetailView(routineId: Int) {
        fetchCycles(routineId)
        uiState = uiState.copy(isViewingDetails = true)
    }

    fun switchToNormalView() {
        uiState = uiState.copy(isViewingDetails = false)
    }

    fun switchToStartRoutine(routineId: Int) {
        // TODO
    }
}