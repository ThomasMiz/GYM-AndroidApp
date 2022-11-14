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
import java.util.*

data class SessionUiState(
    val errorString: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoggingIn: Boolean = false,
    val isRegistering: Boolean = false,
    val isRegistered: Boolean = false,
    val isVerifying: Boolean = false,
    val userVerified : Boolean = false,
    val codeSent : Boolean = false,
    val sendingCode : Boolean = false
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
        sessionUiState = sessionUiState.copy(isLoggingIn = true, errorString = null)
        currentUserJob = viewModelScope.launch {
            runCatching {
                token = gymRepository.loginUser(username, password)
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = true,
                    isLoggingIn = false,
                    errorString = null,
                )
                println("Got user's token: ${token}")

            }.onFailure {e->
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    isLoggingIn = false,
                    errorString = e.message,
                )
            }
        }
    }

    fun userReadyToLogin(){
        sessionUiState = sessionUiState.copy(isLoggedIn = false)
    }

    fun registerNewUser(email : String, username : String, password : String){
        currentUserJob?.cancel()
        var data: UserApiModel? = null
        sessionUiState = sessionUiState.copy(isRegistering = true, errorString = null)
        currentUserJob = viewModelScope.launch {
            runCatching {
                data = gymRepository.registerNewUser(
                    LoginUserApiModel(
                        username,  // Username
                        password,  // Password
                        "-",      // Default First Name
                        "-",      // Default Last name
                        Gender.OTHER,      // Default Gender
                        Date(0),      // Default Birthdate
                        email,     // Email
                        "-",      // Default Phone
                        "-"       // Default AvatarUrl

                    )
                )
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    isRegistered = true,
                    isRegistering = false,
                    errorString = null,
                )
                println("Got user's data: ${data}")

            }.onFailure {e->
                sessionUiState = sessionUiState.copy(
                    isRegistered = false,
                    isRegistering = false,
                    errorString = e.message,
                )
            }
        }
    }

    fun readyToVerify(){
        sessionUiState = sessionUiState.copy(isRegistered = false, codeSent = false)
    }

    fun verifyUser(email : String, code : String){
        currentUserJob?.cancel()
        sessionUiState = sessionUiState.copy(isVerifying = true, errorString = null)
        currentUserJob = viewModelScope.launch {
            runCatching {
                gymRepository.verifyUserEmail(email, code)
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    userVerified = true,
                    isVerifying = false,
                    errorString = null,
                )
                println("User verified!")

            }.onFailure {e->
                sessionUiState = sessionUiState.copy(
                    userVerified = false,
                    isVerifying = false,
                    errorString = e.message
                )
            }
        }
    }

    fun resendVerification(email : String){
        currentUserJob?.cancel()
        sessionUiState = sessionUiState.copy(sendingCode = true, errorString = null)
        currentUserJob = viewModelScope.launch {
            runCatching {
                gymRepository.resendUserVerification(email)
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    sendingCode = false,
                    codeSent = true,
                    errorString = null,
                )
                println("Code sended!")

            }.onFailure {e->
                sessionUiState = sessionUiState.copy(
                    sendingCode = false,
                    codeSent = false,
                    errorString = e.message
                )
            }
        }
    }

    fun readyToLogin(){
        sessionUiState = sessionUiState.copy(userVerified = false)
    }



}