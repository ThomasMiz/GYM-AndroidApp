package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.CycleApiModel
import com.grupo14.gym_androidapp.api.models.CycleExerciseApiModel
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.getErrorStringIdForHttpCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ExecutionState(
    val cyclesList: MutableList<CycleApiModel> = mutableListOf(),
    val exercisesList: MutableList<CycleExerciseApiModel> = mutableListOf(),
    val _exercisesListIndex: MutableStateFlow<Int> = MutableStateFlow(0),
    val exercisesListIndex: StateFlow<Int> = _exercisesListIndex.asStateFlow()
)

data class CycleState(
    val cycle: CycleApiModel,
    val exercises: List<CycleExerciseApiModel> = listOf()
)

data class RoutineState(
    val currentRoutineId: Int? = null,
    val routine: RoutineApiModel? = null,
    val isFetchingRoutine: Boolean = false,
    val fetchRoutineErrorStringId: Int? = null,
    val cycleStates: List<CycleState> = listOf(),
)

class ExecuteRoutineViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    var buildAuxCollections: Boolean = false

    var uiState by mutableStateOf(RoutineState())
        private set

    var executionUiState by mutableStateOf(ExecutionState())
        private set

    private var currentFetchRoutineJob: Job? = null

    fun setExercisesListIndex(index: Int) {
        executionUiState._exercisesListIndex.value = index
    }

    fun fetchWholeRoutine(
        routineId: Int,
        onFinish: () -> Unit,
        onFailure: (errorMessageId: Int) -> Unit
    ) {
        if (currentFetchRoutineJob != null) {
            if (currentFetchRoutineJob!!.isActive)
                return

            currentFetchRoutineJob?.cancel()
        }

        uiState = uiState.copy(currentRoutineId = routineId)
        currentFetchRoutineJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isFetchingRoutine = true)

                val routineResult = gymRepository.fetchRoutine(routineId)
                if (isActive)
                    uiState = uiState.copy(routine = routineResult)

                val cyclesResult =
                    gymRepository.fetchRoutineCycles(routineId, 0, 999, "order", "asc")
                val cycleList = mutableListOf<CycleState>()

                cyclesResult.content?.forEach { cycle ->
                    val exercises = mutableListOf<CycleExerciseApiModel>()
                    val exercisesResult =
                        gymRepository.fetchCycleExercises(cycle.id!!, 0, 999, "order", "asc")
                    exercisesResult.content?.forEach { exercise ->
                        exercises.add(exercise)
                    }

                    cycleList.add(CycleState(cycle, exercises))
                }

                if (isActive) {
                    uiState = uiState.copy(
                        routine = routineResult,
                        isFetchingRoutine = false,
                        cycleStates = cycleList,
                        fetchRoutineErrorStringId = null
                    )
                    onFinish()
                }
            } catch (e: ApiException) {
                if (isActive) {
                    uiState = uiState.copy(isFetchingRoutine = false)
                    onFailure(getErrorStringIdForHttpCode(e.response?.code()))
                }
            } catch (e: Exception) {
                if (isActive) {
                    uiState = uiState.copy(isFetchingRoutine = false)
                    onFailure(R.string.unknownError)
                }
            }
        }
    }
}
