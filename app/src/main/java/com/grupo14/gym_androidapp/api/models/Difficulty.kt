package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.grupo14.gym_androidapp.R

enum class Difficulty(val apiEnumString: String, val stringResourceId: Int) {
    @JsonProperty("rookie")
    ROOKIE("rookie", R.string.rookie),

    @JsonProperty("beginner")
    BEGINNER("beginner", R.string.beginner),

    @JsonProperty("intermediate")
    INTERMEDIATE("intermediate", R.string.intermediate),

    @JsonProperty("advanced")
    ADVANCED("advanced", R.string.advanced),

    @JsonProperty("expert")
    EXPERT("expert", R.string.expert);
}