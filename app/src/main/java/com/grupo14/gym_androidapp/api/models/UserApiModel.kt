package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserApiModel(
    @JsonProperty("id") val id: Int? = null,
    @JsonProperty("username") val username: String? = null,
    @JsonProperty("firstName") val firstName: String? = null,
    @JsonProperty("lastName") val lastName: String? = null,
    @JsonProperty("gender") val gender: Gender? = null,
    @JsonProperty("birthdate") val birthdate: Date? = null,
    @JsonProperty("email") val email: String? = null,
    @JsonProperty("phone") val phone: String? = null,
    @JsonProperty("avatarUrl") val avatarUrl: String? = null,
    @JsonProperty("date") val date: Date? = null,
    @JsonProperty("lastActivity") val lastActivity: Date? = null,
    @JsonProperty("verified") val verified: Boolean? = null
)