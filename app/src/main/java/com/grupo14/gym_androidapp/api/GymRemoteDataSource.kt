package com.grupo14.gym_androidapp.api

import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.api.GymApi
import com.grupo14.gym_androidapp.api.api.GymApiManager
import com.grupo14.gym_androidapp.api.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.Query

class GymRemoteDataSource(
    private val gymApiManager: GymApiManager = GymApiManager(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val gymApi: GymApi = gymApiManager.gymApi;

    private val DEFAULT_PAGE_SIZE = 10
    private val DEFAULT_ORDERBY: String? = null
    private val DEFAULT_DIRECTION: String? = null

    fun setAuthToken(authToken: String) {
        gymApiManager.setAuthToken(authToken);
    }

    private suspend fun <T> handleApiRequest(requestGetter: (GymApi) -> Call<T>) =
        withContext(ioDispatcher) {
            val response = requestGetter(gymApi).execute()
            response.body() ?: throw ApiException(response)
        }

    // ↓ USERS ↓

    suspend fun registerNewUser(user: LoginUserApiModel) = handleApiRequest { it.registerNewUser(user) }
    suspend fun fetchUser(userId: Int) = handleApiRequest { it.getUser(userId) }
    suspend fun resendUserVerification(email: String) = handleApiRequest { it.resendUserVerification(UserApiModel(email = email)) }
    suspend fun verifyUserEmail(email: String, code: String) = handleApiRequest { it.verifyUserEmail(VerifyUserApiModel(email = email, code = code)) }
    suspend fun loginUser(username: String, password: String) = handleApiRequest { it.loginUser(LoginUserApiModel(username = username, password = password)) }
    suspend fun logoutUser() = handleApiRequest { it.logoutUser() }
    suspend fun fetchCurrentUser() = handleApiRequest { it.getCurrentUser() }
    suspend fun putCurrentUser(user: UserApiModel) = handleApiRequest { it.putCurrentUser(user) }
    suspend fun deleteCurrentUser() = handleApiRequest { it.deleteCurrentUser() }

    suspend fun getCurrentUserRoutines(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, difficulty: Difficulty? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getCurrentUserRoutines(page, size, search, difficulty, orderBy, direction) }

    suspend fun getUserRoutines(
        userId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, difficulty: Difficulty? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getUserRoutines(userId, page, size, search, difficulty, orderBy, direction) }

    suspend fun getCurrentUserReviews(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getCurrentUserReviews(page, size, orderBy, direction) }

    // ↑ USERS ↑
    // ↓ FAVORITES ↓

    suspend fun getCurrentUserFavorites(
        page: Int, size: Int = DEFAULT_PAGE_SIZE
    ) = handleApiRequest { it.getCurrentUserFavorites(page, size) }

    suspend fun postCurrentUserFavorites(routineId: Int) = handleApiRequest { it.postCurrentUserFavorites(routineId) }
    suspend fun deleteCurrentUserFavorites(routineId: Int) = handleApiRequest { it.deleteCurrentUserFavorites(routineId) }

    // ↑ FAVORITES ↑
    // ↓ CATEGORIES ↓

    suspend fun getCategories(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getCategories(page, size, search, orderBy, direction) }

    suspend fun postCategories(category: Category) = handleApiRequest { it.postCategories(category) }
    suspend fun getCategory(categoryId: Int) = handleApiRequest { it.getCategory(categoryId) }
    suspend fun putCategory(category: Category) = handleApiRequest { it.putCategory(category.id!!, category) }
    suspend fun deleteCategory(categoryId: Int) = handleApiRequest { it.deleteCategory(categoryId) }

    // ↑ CATEGORIES ↑
    // ↓ ROUTINES ↓

    suspend fun getRoutines(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, userId: Int? = null, categoryId: Int? = null, difficulty: String? = null, score: Int? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getRoutines(page, size, search, userId, categoryId, difficulty, score, orderBy, direction) }

    suspend fun getRoutine(routineId: Int) = handleApiRequest { it.getRoutine(routineId) }

    // ↑ ROUTINES ↑
    // ↓ ROUTINES CYCLES ↓

    suspend fun getRoutineCycles(
        routineId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getRoutineCycles(routineId, page, size, orderBy, direction) }

    suspend fun getRoutineCycle(routineId: Int, cycleId: Int) = handleApiRequest { it.getRoutineCycle(routineId, cycleId) }

    // ↑ ROUTILES CYCLES ↑
    // ↓ CYCLES EXERCISES ↓

    suspend fun getCycleExercises(
        cycleId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest { it.getCycleExercises(cycleId, page, size, orderBy, direction) }

    suspend fun getCycleExercise(cycleId: Int, exerciseId: Int) = handleApiRequest { it.getCycleExercise(cycleId, exerciseId) }

    // ↑ CYCLES EXERCISES ↑
    // ↓ EXERCISES ↓

    suspend fun getExercises(
        page: Int, size: Int = DEFAULT_PAGE_SIZE, search: String? = null, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest{ it.getExercises(page, size, search, orderBy, direction) }

    suspend fun getExercise(exerciseId: Int) = handleApiRequest { it.getExercise(exerciseId) }

    // ↑ EXERCISES ↑
    // ↓ EXERCISES IMAGES ↓


    // ↑ EXERCISES IMAGES ↑
    // ↓ EXERCISES VIDEOS ↓


    // ↑ EXERCISES VIDEOS ↑
    // ↓ REVIEWS ↓

    suspend fun getRoutineReviews(
        routineId: Int, page: Int, size: Int = DEFAULT_PAGE_SIZE, orderBy: String? = DEFAULT_ORDERBY, direction: String? = DEFAULT_DIRECTION
    ) = handleApiRequest{ it.getRoutineReviews(routineId, page, size, orderBy, direction) }

    suspend fun postRoutineReview(routineId: Int, review: ReviewApiModel) = handleApiRequest { it.postRoutineReview(routineId, review) }

    // ↑ REVIEWS ↑
}