package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.Gender
import com.grupo14.gym_androidapp.api.models.LoginUserApiModel
import com.grupo14.gym_androidapp.api.models.TokenApiModel
import com.grupo14.gym_androidapp.api.models.UserApiModel
import com.grupo14.gym_androidapp.getErrorStringIdForHttpCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

class SessionViewModel(
    val gymRepository: GymRepository
) : ViewModel() {

    var sessionUiState by mutableStateOf(SessionUiState())
        private set

    var usernameVal by mutableStateOf("")
    var passwordVal by mutableStateOf("")
    var emailVal by mutableStateOf("")

    private var currentUserJob: Job? = null

    fun loginUser(username: String, password: String, onFailure: (errorMessageId: Int) -> Unit) {
        currentUserJob?.cancel()
        var pablo: TokenApiModel?
        sessionUiState = sessionUiState.copy(isLoggingIn = true)
        currentUserJob = viewModelScope.launch {
            try {
                pablo = gymRepository.loginUser(username.trim(), password.trim())
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = true,
                    isLoggingIn = false
                )

            } catch (e: ApiException) {
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    isLoggingIn = false
                )
                onFailure(getErrorStringIdForHttpCode(e.response?.code()))
            } catch (e: Exception) {
                sessionUiState = sessionUiState.copy(
                    isLoggedIn = false,
                    isLoggingIn = false
                )
                onFailure(R.string.serverUnknownError)
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
        onFailure: (errorMessageId: Int) -> Unit
    ) {
        currentUserJob?.cancel()
        var patricio: UserApiModel? = null
        sessionUiState = sessionUiState.copy(isRegistering = true)

        currentUserJob = viewModelScope.launch {
            try {
                patricio = gymRepository.registerNewUser(
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
            } catch (e: ApiException) {
                sessionUiState = sessionUiState.copy(
                    isRegistered = false,
                    isRegistering = false,
                )
                onFailure(getErrorStringIdForHttpCode(e.response?.code()))
            } catch (e: Exception) {
                sessionUiState = sessionUiState.copy(
                    isRegistered = false,
                    isRegistering = false,
                )
                onFailure(R.string.serverUnknownError)
            }
        }
    }

    fun readyToVerify() {
        sessionUiState = sessionUiState.copy(isRegistered = false, codeSent = false)
    }

    fun verifyUser(email: String, code: String, onFailure: (errorMessageId: Int) -> Unit) {
        currentUserJob?.cancel()
        sessionUiState = sessionUiState.copy(isVerifying = true)

        currentUserJob = viewModelScope.launch {
            try {
                gymRepository.verifyUserEmail(email.trim(), code.trim())
                sessionUiState = sessionUiState.copy(
                    userVerified = true,
                    isVerifying = false
                )

            } catch (e: ApiException) {
                sessionUiState = sessionUiState.copy(
                    userVerified = false,
                    isVerifying = false
                )
                onFailure(getErrorStringIdForHttpCode(e.response?.code()))
            } catch (e: Exception) {
                sessionUiState = sessionUiState.copy(
                    userVerified = false,
                    isVerifying = false
                )
                onFailure(R.string.serverUnknownError)
            }
        }
    }

    fun resendVerification(email: String, onFailure: (errorMessageId: Int) -> Unit) {
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

            }.onFailure { e ->
                sessionUiState = sessionUiState.copy(
                    sendingCode = false,
                    codeSent = false
                )
                onFailure(R.string.resendVerificationFailed)
            }
        }
    }

    fun readyToLogin() {
        sessionUiState = sessionUiState.copy(userVerified = false)
    }
}