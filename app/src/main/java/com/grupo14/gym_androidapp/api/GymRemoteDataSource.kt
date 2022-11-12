package com.grupo14.gym_androidapp.api

import com.grupo14.gym_androidapp.api.api.GymApi
import com.grupo14.gym_androidapp.api.api.RetrofitClient
import com.grupo14.gym_androidapp.api.models.UserApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GymRemoteDataSource(
    private val gymApi: GymApi = RetrofitClient.getClient().create(GymApi::class.java),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend fun fetchCurrentUser(): UserApiModel = withContext(ioDispatcher) {
        val response = gymApi.fetchCurrentUser().execute()
        // println("Response! ${response.isSuccessful} ${response.code()} ${response.message()}");
        response.body() ?: throw java.lang.Exception(response.message())
    }
}