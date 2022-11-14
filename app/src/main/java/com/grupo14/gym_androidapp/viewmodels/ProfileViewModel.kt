package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class GymProfileUiState(
    val user: UserApiModel? = null,
    val fetchUserErrorStringId: Int? = null,
    val isEditingUser: Boolean = false,
    val isFetchingUser: Boolean = false,
    val isPuttingUser: Boolean = false,
)

class ProfileViewModel(
    val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(GymProfileUiState())
        private set

    private var currentUserJob: Job? = null

    fun fetchCurrentUser() {
        currentUserJob?.cancel()
        currentUserJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isFetchingUser = true)
                val userResult: UserApiModel = gymRepository.fetchCurrentUser()
                uiState = uiState.copy(
                    user = userResult,
                    fetchUserErrorStringId = null,
                    isFetchingUser = false,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    user = null,
                    fetchUserErrorStringId = R.string.fetchUserFailed,
                    isFetchingUser = false,
                )
            }
        }
    }

    fun putCurrentUser(user: UserApiModel) {
        currentUserJob?.cancel()
        currentUserJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isPuttingUser = true)
                val userResult: UserApiModel = gymRepository.putCurrentUser(user)
                uiState = uiState.copy(
                    user = userResult,
                    fetchUserErrorStringId = null,
                    isPuttingUser = false,
                    isEditingUser = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    fetchUserErrorStringId = R.string.putUserFailed,
                    isPuttingUser = false,
                )
            }
        }
    }

    fun startEditUser() {
        uiState = uiState.copy(
            isEditingUser = true
        )
    }

    fun cancelEditUser() {
        uiState = uiState.copy(
            isEditingUser = false
        )
    }
}