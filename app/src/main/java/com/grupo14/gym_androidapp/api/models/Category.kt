package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Category (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("detail") val detail: String?
)