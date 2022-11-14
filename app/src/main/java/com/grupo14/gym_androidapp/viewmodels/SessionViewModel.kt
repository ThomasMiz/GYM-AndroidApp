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
    val user: UserApiModel? = null,
    val fetchUserErrorStringId: Int? = null,
    val fetchUserErrorString: String? = null,
    val isFetchingUser: Boolean = false,
    val isLoggedIn: Boolean = false,
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
        var userResult : UserApiModel? = null
        currentUserJob = viewModelScope.launch {
            runCatching {
                // https://stackoverflow.com/questions/64891635/calling-multiple-suspend-functions-in-activity-fragment
                token = gymRepository.loginUser(username, password)
                userResult = gymRepository.fetchCurrentUser()
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = true,
                    user = userResult,
                    fetchUserErrorStringId = null,
                    fetchUserErrorString = null,
                )
                println("Got user's token: ${token}")
                println("Got user's data: ${userResult}")

            }.onFailure {e->
                sessionUiState = sessionUiState.copy(
                    user = null,
                    fetchUserErrorStringId = null,
                    fetchUserErrorString = e.message,
                    isFetchingUser = false,
                    isLoggedIn = false,
                )
            }
        }
    }

}