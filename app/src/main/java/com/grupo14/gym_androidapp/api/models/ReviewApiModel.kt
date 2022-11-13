package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReviewApiModel (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("date") val date: Date?,
    @JsonProperty("score") val score: Int?,
    @JsonProperty("review") val review: String?,
    @JsonProperty("routine") val routine: RoutineApiModel?
)