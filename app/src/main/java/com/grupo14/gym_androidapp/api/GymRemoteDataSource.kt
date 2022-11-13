package com.grupo14.gym_androidapp.api

import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.api.GymApi
import com.grupo14.gym_androidapp.api.api.GymApiManager
import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GymRemoteDataSource(
    private val gymApiManager: GymApiManager = GymApiManager(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val gymApi: GymApi = gymApiManager.gymApi;

    fun setAuthToken(authToken: String) {
        gymApiManager.setAuthToken(authToken);
    }

    suspend fun fetchCurrentUser(): UserApiModel = withContext(ioDispatcher) {
        val response = gymApi.fetchCurrentUser().execute()
        // println("Response! ${response.isSuccessful} ${response.code()} ${response.message()}");
        response.body() ?: throw ApiException(response)
    }

    suspend fun putCurrentUser(user: UserApiModel): UserApiModel = withContext(ioDispatcher) {
        val response = gymApi.putCurrentUser(user).execute()
        // println("Response! ${response.isSuccessful} ${response.code()} ${response.message()}");
        response.body() ?: throw ApiException(response)
    }
}