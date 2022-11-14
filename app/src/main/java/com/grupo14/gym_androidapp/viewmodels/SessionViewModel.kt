package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SessionUiState(
    val errorString: String? = null,
    val isLoggedIn: Boolean = false,
    val isLogginIn: Boolean = false,
    val isRegistered: Boolean = false,
    val verificationSent: Boolean = false
)

class SessionViewModel(
    val gymRepository: GymRepository = GymRepository()
) : ViewModel() {

    var sessionUiState by mutableStateOf(SessionUiState())
        private set

    private var currentUserJob: Job? = null

    fun loginUser(username : String, password : String){
        currentUserJob?.cancel()
        var token: TokenApiModel? = null
        sessionUiState = sessionUiState.copy(isLogginIn = true, errorString = null)
        currentUserJob = viewModelScope.launch {
            runCatching {
                token = gymRepository.loginUser(username, password)
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = true,
                    isLogginIn = false,
                    errorString = null,
                )
                println("Got user's token: ${token}")

            }.onFailure {e->
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    errorString = e.message,
                )
            }
        }
    }

}