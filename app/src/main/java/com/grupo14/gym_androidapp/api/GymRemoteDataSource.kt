package com.grupo14.gym_androidapp.api

import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GymRemoteDataSource(
    private val gymApi: GymApi = GymApi(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend fun fetchCurrentUser(): UserApiModel = withContext(ioDispatcher) {
        gymApi.fetchCurrentUser()
    }
}