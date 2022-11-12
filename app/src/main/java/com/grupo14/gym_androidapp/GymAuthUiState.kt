package com.grupo14.gym_androidapp

import com.grupo14.gym_androidapp.api.models.UserApiModel

data class GymAuthUiState(
    /*val authToken: String? = null,
    val username: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val gender: String? = null,
    val birthdate: java.time.LocalDate? = null,*/

    val user: UserApiModel? = null,
    val fetchUserError: String? = null,
    val isFetchingUser: Boolean = false,
)

//val GymAuthUiState.isSignedIn: Boolean get() = authToken.isNullOrBlank()