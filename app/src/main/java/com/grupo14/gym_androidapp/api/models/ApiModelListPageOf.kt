package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiModelListPageOf<T> (
    @JsonProperty("totalCount") val totalCount: Int?,
    @JsonProperty("orderBy") val orderBy: String?,
    @JsonProperty("direction") val direction: String?,
    @JsonProperty("size") val size: Int?,
    @JsonProperty("page") val page: Int?,
    @JsonProperty("isLastPage") val isLastPage: Boolean?,
    @JsonProperty("search") val search: String?,
    @JsonProperty("content") val content: List<T>?,
)