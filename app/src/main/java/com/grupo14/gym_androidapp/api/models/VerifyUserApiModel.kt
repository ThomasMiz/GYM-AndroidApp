package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class VerifyUserApiModel(
    @JsonProperty("email") val email: String?,
    @JsonProperty("code") val code: String
)