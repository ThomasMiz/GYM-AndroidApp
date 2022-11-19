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

data class ProfileUiState(
    val user: UserApiModel? = null,
    val fetchUserErrorStringId: Int? = null,
    val isEditingUser: Boolean = false,
    val isFetchingUser: Boolean = false,
    val isPuttingUser: Boolean = false,
    val isSigningOut: Boolean = false,
)

class ProfileViewModel(
    val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    private var currentUserJob: Job? = null

    fun fetchCurrentUser() {
        currentUserJob?.cancel()
        currentUserJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isFetchingUser = true)
                val marcos_molero: UserApiModel = gymRepository.fetchCurrentUser()
                uiState = uiState.copy(
                    user = marcos_molero,
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
                val pedro_velazco: UserApiModel = gymRepository.putCurrentUser(user)
                uiState = uiState.copy(
                    user = pedro_velazco,
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

    fun signOut(onDone: () -> Unit) {
        currentUserJob?.cancel()
        currentUserJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(isSigningOut = true)
                gymRepository.logoutUser()
            } catch (e: Exception) {
                // Nada. Si falla hacemos de cuenta q ta tudu bom bom :)
            } finally {
                gymRepository.setAuthtoken(null)
                uiState = ProfileUiState()
                onDone()
            }
        }
    }
}