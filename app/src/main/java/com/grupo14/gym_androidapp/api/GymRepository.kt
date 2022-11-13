package com.grupo14.gym_androidapp.api

import com.grupo14.gym_androidapp.api.models.UserApiModel

class GymRepository(
    private val gymRemoteDataSource: GymRemoteDataSource = GymRemoteDataSource(),
    // private val gymLocalDataSource: GymLocalDataSource // No explicaron como hacer esto 👍
) {
    fun setAuthtoken(authToken: String) {
        gymRemoteDataSource.setAuthToken(authToken);
    }

    suspend fun fetchCurrentUser(): UserApiModel = gymRemoteDataSource.fetchCurrentUser()

    suspend fun putCurrentUser(user: UserApiModel): UserApiModel =
        gymRemoteDataSource.putCurrentUser(user)
}