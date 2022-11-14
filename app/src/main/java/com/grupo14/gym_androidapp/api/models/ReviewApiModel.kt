package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReviewApiModel (
    @JsonProperty("id") val id: Int? = null,
    @JsonProperty("date") val date: Date? = null,
    @JsonProperty("score") val score: Int? = null,
    @JsonProperty("review") val review: String? = null,
    @JsonProperty("routine") val routine: RoutineApiModel? = null
)