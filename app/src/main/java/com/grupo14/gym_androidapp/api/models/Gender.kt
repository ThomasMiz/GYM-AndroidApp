package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.grupo14.gym_androidapp.R

enum class Gender(val stringResourceId: Int) {
    @JsonProperty("male")
    MALE(R.string.male),

    @JsonProperty("female")
    FEMALE(R.string.female),

    @JsonProperty("other")
    OTHER(R.string.other);
}