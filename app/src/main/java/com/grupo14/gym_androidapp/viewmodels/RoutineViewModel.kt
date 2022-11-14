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
import kotlinx.coroutines.launch

data class RoutineUiState(
    val routine: RoutineApiModel? = null,
    val isFetchingRoutine: Boolean = false,
    val fetchingRoutineId: Int? = null,
    val fetchRoutineErrorStringId: Int? = null,
)

class RoutineViewModel(
    val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(RoutineUiState())
        private set

    private var currentFetchRoutineJob: Job? = null

    fun fetchRoutine(routineId: Int) {
        if (currentFetchRoutineJob != null) {
            if (uiState.fetchingRoutineId == routineId)
                return;

            currentFetchRoutineJob!!.cancel()
        }
        currentFetchRoutineJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    routine = null,
                    isFetchingRoutine = true,
                    fetchingRoutineId = routineId,
                    fetchRoutineErrorStringId = null,
                )
                val routineResult: RoutineApiModel = gymRepository.fetchRoutine(routineId)
                println("Got routine: ${routineResult}") // TODO: Remove
                uiState = uiState.copy(
                    routine = routineResult,
                    isFetchingRoutine = false,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    routine = null,
                    isFetchingRoutine = false,
                    fetchRoutineErrorStringId = R.string.fetchRoutineFailed,
                )
            }
        }
    }

}