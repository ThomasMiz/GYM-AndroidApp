package com.grupo14.gym_androidapp.api.api

import com.grupo14.gym_androidapp.api.models.*
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface GymApi {
    // ↓ USERS ↓

    @GET("users")
    fun fetchUsers(
        @Query("search") search : String?,
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction : String?
    ) : Call<ApiModelListPageOf<SmallUserApiModel>>

    @POST("users")
    fun registerNewUser(@Body user: LoginUserApiModel): Call<UserApiModel>

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: Int): Call<UserApiModel>

    @POST("users/resend_verification")
    fun resendUserVerification(@Body user: UserApiModel): Call<Void>

    @POST("users/verify_email")
    fun verifyUserEmail(@Body user: VerifyUserApiModel): Call<Void>

    @POST("users/login")
    fun loginUser(@Body user: LoginUserApiModel): Call<TokenApiModel>

    @POST("users/logout")
    fun logoutUser(): Call<Void>

    @GET("users/current")
    fun getCurrentUser(): Call<UserApiModel>

    @PUT("users/current")
    fun putCurrentUser(@Body user: UserApiModel): Call<UserApiModel>

    @DELETE("users/current")
    fun deleteCurrentUser(): Call<Void>

    @GET("users/current/routines")
    fun getCurrentUserRoutines(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("difficulty") difficulty: Difficulty?,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<RoutineApiModel>>

    @GET("users/{userId}/routines")
    fun getUserRoutines(
        @Query("userId") userId: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("difficulty") difficulty: Difficulty?,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<RoutineApiModel>>

    @GET("users/current/reviews")
    fun getCurrentUserReviews(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<ReviewApiModel>>

    // ↑ USERS ↑
    // ↓ FAVORITES ↓

    @GET("favourites")
    fun getCurrentUserFavorites(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiModelListPageOf<RoutineApiModel>>

    @POST("favourites/{routineId}")
    fun postCurrentUserFavorites(@Path("routineId") routineId: Int): Call<Void>

    @DELETE("favourites/{routineId}")
    fun deleteCurrentUserFavorites(@Path("routineId") routineId: Int): Call<Void>

    // ↑ FAVORITES ↑
    // ↓ CATEGORIES ↓

    @GET("categories")
    fun getCategories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<Category>>

    @POST("categories")
    fun postCategories(@Body category: Category): Call<Category>

    @GET("categories/{categoryId}")
    fun getCategory(@Path("categoryId") categoryId: Int): Call<Category>

    @PUT("categories/{categoryId}")
    fun putCategory(@Path("categoryId") categoryId: Int, @Body category: Category): Call<Category>

    @DELETE("categories/{categoryId}")
    fun deleteCategory(@Path("categoryId") categoryId: Int): Call<Void>

    // ↑ CATEGORIES ↑
    // ↓ ROUTINES ↓

    @GET("routines")
    fun getRoutines(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("userId") userId: Int?,
        @Query("categoryId") categoryId: Int?,
        @Query("difficulty") difficulty: String?,
        @Query("score") score: Int?,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<RoutineApiModel>>

    @GET("routines/{routineId}")
    fun getRoutine(@Path("routineId") routineId: Int): Call<RoutineApiModel>

    // ↑ ROUTINES ↑
    // ↓ ROUTINES CYCLES ↓

    @GET("routines/{routineId}/cycles")
    fun getRoutineCycles(
        @Path("routineId") routineId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<CycleApiModel>>

    @GET("routines/{routineId}/cycles/{cycleId}")
    fun getRoutineCycle(
        @Path("routineId") routineId: Int,
        @Path("cycleId") cycleId: Int
    ): Call<CycleApiModel>

    // ↑ ROUTILES CYCLES ↑
    // ↓ CYCLES EXERCISES ↓

    @GET("cycles/{cycleId}/exercises")
    fun getCycleExercises(
        @Path("cycleId") cycleId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<CycleExerciseApiModel>>

    @GET("cycles/{cycleId}/exercises/{exerciseId}")
    fun getCycleExercise(
        @Path("cycleId") cycleId: Int,
        @Path("exerciseId") exerciseId: Int
    ): Call<CycleExerciseApiModel>

    // ↑ CYCLES EXERCISES ↑
    // ↓ EXERCISES ↓

    @GET("exercises")
    fun getExercises(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<ExerciseApiModel>>

    @GET("exercises/{exerciseId}")
    fun getExercise(@Path("exerciseId") exerciseId: Int): Call<ExerciseApiModel>

    // ↑ EXERCISES ↑
    // ↓ EXERCISES IMAGES ↓


    // ↑ EXERCISES IMAGES ↑
    // ↓ EXERCISES VIDEOS ↓


    // ↑ EXERCISES VIDEOS ↑
    // ↓ REVIEWS ↓

    @GET("reviews/{routineId}")
    fun getRoutineReviews(
        @Path("routineId") routineId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("orderBy") orderBy: String?,
        @Query("direction") direction: String?
    ): Call<ApiModelListPageOf<ReviewApiModel>>

    @POST("reviews/{routineId}")
    fun postRoutineReview(
        @Path("routineId") routineId: Int,
        @Body review: SubmitReviewApiModel
    ): Call<SubmitReviewApiModel>

    // ↑ REVIEWS ↑
}