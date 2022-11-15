package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
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
        var token: TokenApiModel?
        var errorMessage: String? = "Unexpected error"
        sessionUiState = sessionUiState.copy(isLoggingIn = true, errorString = null)
        
        currentUserJob = viewModelScope.launch {
            try {
                token = gymRepository.loginUser(username, password)
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = true,
                    isLoggingIn = false,
                    errorString = null,
                )
                println("Got user's token: ${token}")

            } catch (e : ApiException) {

                e.response?.code()?.let {
                    errorMessage = when(e.response.code()) {
                        400 -> "Bad request: Request or data is invalid or has a constraint"
                        401 -> "Usuario y/o contraseña incorrectos"
                        500 -> "Internal server error"
                        else -> "Unexpected error"
                    }
                }

                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    isLoggingIn = false,
                    errorString = errorMessage
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
        var errorMessage: String? = "Unexpected error"
        sessionUiState = sessionUiState.copy(isRegistering = true, errorString = null)

        currentUserJob = viewModelScope.launch {
            try {
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
                sessionUiState = sessionUiState.copy(
                    isRegistered = true,
                    isRegistering = false,
                    errorString = null,
                )
                println("Got user's data: ${data}")

            } catch(e : ApiException) {

                e.response?.code()?.let {
                    errorMessage = when(e.response.code()) {
                        400 -> "Bad request: Request or data is invalid or has a constraint"
                        500 -> "Internal server error"
                        else -> "Unexpected error"
                    }
                }

                sessionUiState = sessionUiState.copy(
                    isRegistered = false,
                    isRegistering = false,
                    errorString = errorMessage,
                )
            }
        }
    }

    fun readyToVerify(){
        sessionUiState = sessionUiState.copy(isRegistered = false, codeSent = false)
    }

    fun verifyUser(email : String, code : String){
        currentUserJob?.cancel()
        var errorMessage: String? = "Unexpected error"
        sessionUiState = sessionUiState.copy(isVerifying = true, errorString = null)

        currentUserJob = viewModelScope.launch {
            try {
                gymRepository.verifyUserEmail(email, code)
                sessionUiState = sessionUiState.copy(
                    userVerified = true,
                    isVerifying = false,
                    errorString = null,
                )
                println("User verified!")

            } catch(e : ApiException) {

                e.response?.code()?.let {
                    errorMessage = when(e.response.code()) {
                        400 -> "Bad request: Request or data is invalid or has a constraint"
                        401 -> "Authorization information is missing or invalid"
                        500 -> "Internal server error"
                        else -> "Unexpected error"
                    }
                }

                sessionUiState = sessionUiState.copy(
                    userVerified = false,
                    isVerifying = false,
                    errorString = errorMessage
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