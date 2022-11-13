package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserApiModel(
    @JsonProperty("id") val id: Long?,
    @JsonProperty("username") val username: String?,
    @JsonProperty("firstName") val firstName: String?,
    @JsonProperty("lastName") val lastName: String?,
    @JsonProperty("gender") val gender: Gender?,
    @JsonProperty("birthdate") val birthdate: Date?,
    @JsonProperty("email") val email: String?,
    @JsonProperty("phone") val phone: String?,
    @JsonProperty("avatarUrl") val avatarUrl: String?,
    @JsonProperty("date") val date: Date?,
    @JsonProperty("lastActivity") val lastActivity: Date?,
    @JsonProperty("verified") val verified: Boolean?
)