package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExerciseApiModel (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("detail") val detail: String?,
    @JsonProperty("type") val type: String?,
    @JsonProperty("date") val date: Date?,
)