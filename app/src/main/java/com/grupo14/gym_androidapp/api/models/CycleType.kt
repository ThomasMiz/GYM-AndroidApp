package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.grupo14.gym_androidapp.R

enum class CycleType(val apiEnumString: String, val stringResourceId: Int) {
    @JsonProperty("warmup")
    WARMUP("warmup", R.string.warmup),

    @JsonProperty("exercise")
    EXERCISE("exercise", R.string.exercise),

    @JsonProperty("cooldown")
    COOLDOWN("cooldown", R.string.cooldown);
}