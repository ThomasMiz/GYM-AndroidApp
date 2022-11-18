package com.grupo14.gym_androidapp.viewmodels

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class CycleState(
    val cycle: CycleApiModel,
    val exercises: List<CycleExerciseApiModel> = listOf()
)

data class RoutineState(
    val routine: RoutineApiModel? = null,
    val isFetchingRoutine: Boolean = false,
    val fetchRoutineErrorStringId: Int? = null,
    val cycleStates: List<CycleState> = listOf(),

    val isExecuting: Boolean = false,
)

class ExecuteRoutineViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    var uiState by mutableStateOf(RoutineState())
        private set

    private var currentFetchRoutineJob: Job? = null

    fun start(routineId: Int) {
        uiState = RoutineState()
        fetchWholeRoutine(routineId)
    }

    fun fetchWholeRoutine(routineId: Int, onFailure: (errorMessageId: Int) -> Unit) {
        if (currentFetchRoutineJob != null) {
            if (currentFetchRoutineJob!!.isActive)
                return

            currentFetchRoutineJob?.cancel()
        }

        currentFetchRoutineJob = viewModelScope.launch {
            try {
                uiState = RoutineState(isFetchingRoutine = true)

                val routineResult = gymRepository.fetchRoutine(routineId)

            } catch (e: ApiException) {
                onFailure(getErrorStringIdForHttpCode(e.response?.code()))
            } catch (e: Exception) {
                onFailure(R.string.unknownError)
            }
        }
    }

    fun switchToExecuteView() {
        uiState = uiState.copy(isExecuting = true)
    }

}
