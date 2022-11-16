package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginUserApiModel(
    @JsonProperty("username") val username: String? = null,
    @JsonProperty("password") val password: String? = null,
    @JsonProperty("firstName") val firstName: String? = null,
    @JsonProperty("lastName") val lastName: String? = null,
    @JsonProperty("gender") val gender: Gender? = null,
    @JsonProperty("birthdate") val birthdate: Date? = null,
    @JsonProperty("email") val email: String? = null,
    @JsonProperty("phone") val phone: String? = null,
    @JsonProperty("avatarUrl") val avatarUrl: String? = null
)