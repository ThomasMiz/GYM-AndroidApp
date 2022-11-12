package com.grupo14.gym_androidapp.api

import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.delay
import java.util.Date

class GymApi {
    suspend fun fetchCurrentUser(): UserApiModel {
        delay(3000)
        return UserApiModel(
            id = 69,
            username = "pedromcpedro",
            firstName = "Pedro",
            lastName = "McPedro",
            gender = "male",
            birthdate = Date(284007600000),
            email = "pedro@mcpedro.com",
            phone = "12344321",
            avatarUrl = "https://flic.kr/p/3ntH2u",
            date = Date(284007600000),
            lastActivity = Date(284007600000),
            verified = true
        )
    }
}