package com.grupo14.gym_androidapp.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiModelListPageOfUser<T> (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("username") val username: String?,
    @JsonProperty("gender") val gender: String?,
    @JsonProperty("avatarUrl") val avatarUrl: String?,
    @JsonProperty("date") val date: Date?,
    @JsonProperty("lastActivity") val lastActivity: Date?,
)