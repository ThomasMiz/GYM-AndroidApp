package com.grupo14.gym_androidapp.api

import android.content.Context
import com.grupo14.gym_androidapp.api.models.*

class GymRepository(
    private val applicationContext: Context,
    private val gymRemoteDataSource: GymRemoteDataSource = GymRemoteDataSource()
) {
    private val DEFAULT_PAGE_SIZE = 10
    private val DEFAULT_ORDERBY: String? = null
    private val DEFAULT_DIRECTION: String? = null

    private val preferences = applicationContext.getSharedPreferences("my_app", Context.MODE_PRIVATE)

    init {
        val token = preferences.getString("auth_token", null)
        if (!token.isNullOrBlank()) {
            gymRemoteDataSource.setAuthToken(token)
        }
    }

    fun setAuthtoken(authToken: String?) {
        gymRemoteDataSource.setAuthToken(authToken);
        try {
            with (preferences.edit()) {
                putString("auth_token", authToken)
                apply()
            }
        } catch (e: Exception) {
            // oops 💀
        }
    }

    fun getAuthtoken(): String? {
        return gymRemoteDataSource.getAuthToken();
    }

    // ↓ USERS ↓

    suspend fun fetchUsers(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchUsers(page, size, search, orderBy, direction)

    suspend fun registerNewUser(user: LoginUserApiModel) = gymRemoteDataSource.registerNewUser(user)
    suspend fun fetchUser(userId: Int) = gymRemoteDataSource.fetchUser(userId)
    suspend fun resendUserVerification(email: String) = gymRemoteDataSource.resendUserVerification(email)
    suspend fun verifyUserEmail(email: String, code: String) = gymRemoteDataSource.verifyUserEmail(email, code)

    suspend fun loginUser(username: String, password: String): TokenApiModel {
        val result = gymRemoteDataSource.loginUser(username, password)
        setAuthtoken(result.token)
        return result;
    }
    suspend fun logoutUser() {
        gymRemoteDataSource.logoutUser()
        setAuthtoken(null)
    }

    suspend fun fetchCurrentUser() = gymRemoteDataSource.fetchCurrentUser()
    suspend fun putCurrentUser(user: UserApiModel) = gymRemoteDataSource.putCurrentUser(user)
    suspend fun deleteCurrentUser() = gymRemoteDataSource.deleteCurrentUser()

    suspend fun fetchCurrentUserRoutines(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, difficulty: Difficulty? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchCurrentUserRoutines(page, size, search, difficulty, orderBy, direction)

    suspend fun fetchUserRoutines(
        userId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, difficulty: Difficulty? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchUserRoutines(userId, page, size, search, difficulty, orderBy, direction)

    suspend fun fetchCurrentUserReviews(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchCurrentUserReviews(page, size, orderBy, direction)

    // ↑ USERS ↑
    // ↓ FAVORITES ↓

    suspend fun fetchCurrentUserFavorites(
        page: Int, size: Int = DEFAULT_PAGE_SIZE
    ) = gymRemoteDataSource.fetchCurrentUserFavorites(page, size)

    suspend fun postCurrentUserFavorites(routineId: Int) = gymRemoteDataSource.postCurrentUserFavorites(routineId)
    suspend fun deleteCurrentUserFavorites(routineId: Int) = gymRemoteDataSource.deleteCurrentUserFavorites(routineId)

    // ↑ FAVORITES ↑
    // ↓ CATEGORIES ↓

    suspend fun fetchCategories(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchCategories(page, size, search, orderBy, direction)

    suspend fun postCategories(category: Category) = gymRemoteDataSource.postCategories(category)
    suspend fun fetchCategory(categoryId: Int) = gymRemoteDataSource.fetchCategory(categoryId)
    suspend fun putCategory(category: Category) = gymRemoteDataSource.putCategory(category)
    suspend fun deleteCategory(categoryId: Int) = gymRemoteDataSource.deleteCategory(categoryId)

    // ↑ CATEGORIES ↑
    // ↓ ROUTINES ↓

    suspend fun fetchRoutines(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, userId: Int? = null, categoryId: Int? = null, difficulty: Difficulty? = null, score: Int? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchRoutines(page, size, search, userId, categoryId, difficulty, score, orderBy, direction)

    suspend fun fetchRoutine(routineId: Int) = gymRemoteDataSource.fetchRoutine(routineId)

    // ↑ ROUTINES ↑
    // ↓ ROUTINES CYCLES ↓

    suspend fun fetchRoutineCycles(
        routineId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchRoutineCycles(routineId, page, size, orderBy, direction)

    suspend fun fetchRoutineCycle(routineId: Int, cycleId: Int) = gymRemoteDataSource.fetchRoutineCycle(routineId, cycleId)

    // ↑ ROUTILES CYCLES ↑
    // ↓ CYCLES EXERCISES ↓

    suspend fun fetchCycleExercises(
        cycleId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchCycleExercises(cycleId, page, size, orderBy, direction)

    suspend fun fetchCycleExercise(cycleId: Int, exerciseId: Int) = gymRemoteDataSource.fetchCycleExercise(cycleId, exerciseId)

    // ↑ CYCLES EXERCISES ↑
    // ↓ EXERCISES ↓

    suspend fun fetchExercises(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchExercises(page, size, search, orderBy, direction)

    suspend fun fetchExercise(exerciseId: Int) = gymRemoteDataSource.fetchExercise(exerciseId)

    // ↑ EXERCISES ↑
    // ↓ EXERCISES IMAGES ↓


    // ↑ EXERCISES IMAGES ↑
    // ↓ EXERCISES VIDEOS ↓


    // ↑ EXERCISES VIDEOS ↑
    // ↓ REVIEWS ↓

    suspend fun fetchRoutineReviews(
        routineId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = gymRemoteDataSource.fetchRoutineReviews(routineId, page, size, orderBy, direction)

    suspend fun postRoutineReview(routineId: Int, score: Int, review: String = "") = gymRemoteDataSource.postRoutineReview(routineId, SubmitReviewApiModel(score, review))

    // ↑ REVIEWS ↑
}