package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorApiModel(
    @JsonProperty("code") val code: Int?,
    @JsonProperty("description") val description: String,
    @JsonProperty("details") val details: List<String>
)