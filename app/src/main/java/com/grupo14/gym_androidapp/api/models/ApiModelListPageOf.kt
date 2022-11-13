package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiModelListPageOf<T> (
    @JsonProperty("totalCount") val totalCount: Int?,
    @JsonProperty("orderBy") val orderBy: String?,
    @JsonProperty("direction") val direction: String?,
    @JsonProperty("size") val size: Int?,
    @JsonProperty("page") val page: Int?,
    @JsonProperty("isLastPage") val isLastPage: Boolean?,

    @JsonProperty("content") val content: List<T>?,
)