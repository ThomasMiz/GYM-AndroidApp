package com.grupo14.gym_androidapp.api.models

import java.util.Date

data class UserApiModel(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthdate: Date,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val date: Date,
    val lastActivity: Date,
    val verified: Boolean
)