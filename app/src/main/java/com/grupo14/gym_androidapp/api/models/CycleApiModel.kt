package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CycleApiModel (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("detail") val detail: String?,
    @JsonProperty("type") val type: String?,
    @JsonProperty("order") val order: Int?,
    @JsonProperty("repetitions") val repetitions: Int?,
)