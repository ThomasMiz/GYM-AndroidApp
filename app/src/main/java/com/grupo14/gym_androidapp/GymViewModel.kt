package com.grupo14.gym_androidapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GymViewModel(
    private val gymRepository: GymRepository = GymRepository()
) : ViewModel() {
    var loginUiState by mutableStateOf(GymAuthUiState())
        private set

    private var fetchCurrentUserJob: Job? = null

    fun fetchCurrentUser() {
        fetchCurrentUserJob?.cancel()
        fetchCurrentUserJob = viewModelScope.launch {
            try {
                loginUiState = loginUiState.copy(isFetchingUser = true)
                val userResult: UserApiModel = gymRepository.fetchCurrentUser()
                loginUiState = loginUiState.copy(
                    user = userResult,
                    isFetchingUser = false,
                )
            } catch (e: Exception) {
                loginUiState = loginUiState.copy(
                    user = null,
                    fetchUserError = "Failed to fetch user", // TODO: Use stringResource()
                    isFetchingUser = false,
                )
            }
        }
    }
}