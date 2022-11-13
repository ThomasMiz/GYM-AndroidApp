package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.grupo14.gym_androidapp.R

enum class Difficulty(val stringResourceId: Int) {
    @JsonProperty("rookie")
    ROOKIE(R.string.rookie),

    @JsonProperty("beginner")
    BEGINNER(R.string.beginner),

    @JsonProperty("intermediate")
    INTERMEDIATE(R.string.intermediate),

    @JsonProperty("advanced")
    ADVANCED(R.string.advanced),

    @JsonProperty("expert")
    EXPERT(R.string.expert);
}