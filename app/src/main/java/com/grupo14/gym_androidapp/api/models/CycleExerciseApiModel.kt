package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CycleExerciseApiModel (
    @JsonProperty("order") val order: Int?,
    @JsonProperty("duration") val duration: Int?,
    @JsonProperty("repetitions") val repetitions: Int?,
    @JsonProperty("exercise") val exercise: ExerciseApiModel?,
)