package com.grupo14.gym_androidapp.api.api

import com.grupo14.gym_androidapp.api.models.UserApiModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface GymApi {
    @GET("users/current")
    fun fetchCurrentUser(): Call<UserApiModel>

    @PUT("users/current")
    fun putCurrentUser(@Body user: UserApiModel): Call<UserApiModel>
}