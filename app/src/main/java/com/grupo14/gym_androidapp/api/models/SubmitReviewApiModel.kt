package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmitReviewApiModel (
    @JsonProperty("score") val score: Int? = null,
    @JsonProperty("review") val review: String? = null,
)