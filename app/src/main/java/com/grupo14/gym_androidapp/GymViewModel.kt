package com.grupo14.gym_androidapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GymViewModel(
    val gymRepository: GymRepository = GymRepository()
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
                println("Got user: ${userResult}")
                loginUiState = loginUiState.copy(
                    user = userResult,
                    fetchUserErrorStringId = null,
                    isFetchingUser = false,
                )
            } catch (e: Exception) {
                loginUiState = loginUiState.copy(
                    user = null,
                    fetchUserErrorStringId = R.string.fetchUserFailed,
                    isFetchingUser = false,
                )
            }
        }
    }
}