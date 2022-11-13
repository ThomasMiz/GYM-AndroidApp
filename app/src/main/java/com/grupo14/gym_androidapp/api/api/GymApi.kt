package com.grupo14.gym_androidapp.api.api

import com.grupo14.gym_androidapp.api.models.*
import retrofit2.Call
import retrofit2.http.*

interface GymApi {
    @POST("users")
    fun registerNewUser(@Body user: UserApiModel): Call<UserApiModel>

    @GET("users/{userId}")
    fun fetchUser(@Path("userId") userId: Int): Call<UserApiModel>

    @POST("users/resend_verification")
    fun resendUserVerification(@Body user: UserApiModel): Call<ErrorApiModel>

    @POST("users/verify_email")
    fun verifyUserEmail(@Body user: UserApiModel): Call<ErrorApiModel>

    @POST("users/login")
    fun loginUser(@Body user: UserApiModel): Call<TokenApiModel>

    @POST("users/logout")
    fun logoutUser(): Call<ErrorApiModel>

    @GET("users/current")
    fun fetchCurrentUser(): Call<UserApiModel>

    @PUT("users/current")
    fun putCurrentUser(@Body user: UserApiModel): Call<UserApiModel>

    @DELETE("users/current")
    fun deleteCurrentUser(): Call<ErrorApiModel>

    @GET("users/current/routines")
    fun getCurrentUserRoutines(
        @Query("difficulty") difficulty: Difficulty?,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String,
        @Query("direction") direction: String
    ) : Call<ApiModelListPageOf<SmallRoutineApiModel>>

    @GET("users/{userId}/routines")
    fun getUserRoutines(
        @Path("userId") userId: Int,
        @Query("difficulty") difficulty: Difficulty?,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String,
        @Query("direction") direction: String
    ) : Call<ApiModelListPageOf<SmallRoutineApiModel>>

    @GET("users/current/routines")
    fun getCurrentUserReviews(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String,
        @Query("direction") direction: String
    ) : Call<ApiModelListPageOf<SmallRoutineApiModel>>
}