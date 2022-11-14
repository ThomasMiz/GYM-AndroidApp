package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

@JsonIgnoreProperties(ignoreUnknown = true)
data class RoutineApiModel (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("detail") val detail: String?,
    @JsonProperty("date") val date: Date?,
    @JsonProperty("score") val score: Float?,
    @JsonProperty("isPublic") val isPublic: Boolean?,
    @JsonProperty("difficulty") val difficulty: Difficulty?,
    @JsonProperty("user") val user: SmallUserApiModel?,
    @JsonProperty("category") val category: Category?
)