package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.Gender
import com.grupo14.gym_androidapp.api.models.LoginUserApiModel
import com.grupo14.gym_androidapp.api.models.TokenApiModel
import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

data class SessionUiState(
    val isLoggedIn: Boolean = false,
    val isLoggingIn: Boolean = false,
    val isRegistering: Boolean = false,
    val isRegistered: Boolean = false,
    val isVerifying: Boolean = false,
    val userVerified: Boolean = false,
    val codeSent: Boolean = false,
    val sendingCode: Boolean = false
)

// TODO: sacar prints en todo este archivo

class SessionViewModel(
    val gymRepository: GymRepository = GymRepository()
) : ViewModel() {

    var sessionUiState by mutableStateOf(SessionUiState())
        private set

    var usernameVal by mutableStateOf("")
    var passwordVal by mutableStateOf("")
    var emailVal by mutableStateOf("")

    private var currentUserJob: Job? = null

    fun loginUser(username: String, password: String, onFailure: (errorMessage: String) -> Unit) {
        currentUserJob?.cancel()
        var token: TokenApiModel?
        sessionUiState = sessionUiState.copy(isLoggingIn = true)
        currentUserJob = viewModelScope.launch {
            try {
                token = gymRepository.loginUser(username.trim(), password.trim())
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = true,
                    isLoggingIn = false
                )
                println("Got user's token: ${token}")

            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // TODO ??? No le mostras este string al usuario porque nos reprueban
                    401 -> "Usuario y/o contraseña incorrectos"
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    isLoggingIn = false
                )
                onFailure(errorMessage)
            } catch (e: Exception) {
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    isLoggingIn = false
                )
                onFailure("pero la PUCHA, qué _MIERDA_ le paso al server??") // TODO: cambiar
            }
        }
    }

    fun userReadyToLogin() {
        sessionUiState = sessionUiState.copy(isLoggedIn = false)
    }

    fun registerNewUser(
        email: String,
        username: String,
        password: String,
        onFailure: (errorMessage: String) -> Unit
    ) {
        currentUserJob?.cancel()
        var data: UserApiModel? = null
        sessionUiState = sessionUiState.copy(isRegistering = true)

        currentUserJob = viewModelScope.launch {
            try {
                data = gymRepository.registerNewUser(
                    LoginUserApiModel(
                        username = username.trim(),
                        password = password.trim(),
                        gender = Gender.OTHER,
                        email = email.trim()
                    )
                )
                sessionUiState = sessionUiState.copy(
                    isRegistered = true,
                    isRegistering = false
                )
                println("Got user's data: ${data}")
            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // TODO: cambiar
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                sessionUiState = sessionUiState.copy(
                    isRegistered = false,
                    isRegistering = false,
                )
                onFailure(errorMessage)
            } catch (e: Exception) {
                sessionUiState = sessionUiState.copy(
                    isRegistered = false,
                    isRegistering = false,
                )
                onFailure("pero la PUCHA, qué _MIERDA_ le paso al server??") // TODO: cambiar
            }
        }
    }

    fun readyToVerify() {
        sessionUiState = sessionUiState.copy(isRegistered = false, codeSent = false)
    }

    fun verifyUser(email: String, code: String, onFailure: (errorMessage: String) -> Unit) {
        currentUserJob?.cancel()
        sessionUiState = sessionUiState.copy(isVerifying = true)

        currentUserJob = viewModelScope.launch {
            try {
                gymRepository.verifyUserEmail(email.trim(), code.trim())
                sessionUiState = sessionUiState.copy(
                    userVerified = true,
                    isVerifying = false
                )
                println("User verified!")

            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // TODO: cambiar
                    401 -> "Authorization information is missing or invalid"
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                sessionUiState = sessionUiState.copy(
                    userVerified = false,
                    isVerifying = false
                )
                onFailure(errorMessage)
            } catch (e: Exception) {
                sessionUiState = sessionUiState.copy(
                    userVerified = false,
                    isVerifying = false
                )
                onFailure("pero la PUCHA, qué _MIERDA_ le paso al server??") // TODO: cambiar
            }
        }
    }

    fun resendVerification(email: String, onFailure: (errorMessage: String) -> Unit) {
        currentUserJob?.cancel()
        sessionUiState = sessionUiState.copy(sendingCode = true)
        currentUserJob = viewModelScope.launch {
            runCatching {
                gymRepository.resendUserVerification(email.trim())
            }.onSuccess {
                sessionUiState = sessionUiState.copy(
                    sendingCode = false,
                    codeSent = true
                )
                println("Code sended!")

            }.onFailure { e ->
                sessionUiState = sessionUiState.copy(
                    sendingCode = false,
                    codeSent = false
                )
                onFailure("NO TE MANDAMOS UNA MIERDA PORQUE SOS ALTO PELOTUDO ((y el srv se cayó))") // TODO: cambiar
            }
        }
    }

    fun readyToLogin() {
        sessionUiState = sessionUiState.copy(userVerified = false)
    }
}