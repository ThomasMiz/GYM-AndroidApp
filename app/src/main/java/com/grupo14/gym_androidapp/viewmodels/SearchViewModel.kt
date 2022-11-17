package com.grupo14.gym_androidapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.api.api.ApiException
import com.grupo14.gym_androidapp.api.models.Category
import com.grupo14.gym_androidapp.api.models.RoutineApiModel
import com.grupo14.gym_androidapp.api.models.SmallUserApiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class UserUIState(
    val userList : MutableList<SmallUserApiModel> = mutableListOf(),
    val isFetchingUser: Boolean = false,
    val isFetchingUsers: Boolean = false,
    val fetchingUserId: Int? = null,
    val fetchUserErrorStringId: Int? = null,
)

data class RoutineUIState(
    val routineList : MutableList<RoutineApiModel> = mutableListOf(),
    val isFetchingRoutine: Boolean = false,
    val isFetchingRoutines: Boolean = false,
    val fetchingRoutineId: Int? = null,
    val fetchRoutineErrorStringId: Int? = null,

    val isFetchingCategories : Boolean = false,
    val categoryList: MutableList<Category> = mutableListOf(),
    val fetchCategoryErrorStringId: Int? = null,
)

data class SearchUIState(
    val isSearching : Boolean = false,
    val searchFinished : Boolean = false,
)

class SearchViewModel(
    val gymRepository: GymRepository = GymRepository()
) : ViewModel() {

    private var currentSearchJob: Job? = null
    private var currentFetchCategoriesJob : Job? = null
    private var currentFetchRoutinesByNameJob: Job? = null
    private var currentFetchRoutinesByUserIdJob: Job? = null
    private var currentFetchUsersJob: Job? = null


    var routineUIState by mutableStateOf(RoutineUIState())
        private set

    var userUIState by mutableStateOf(UserUIState())
        private set

    var searchUIState by mutableStateOf(SearchUIState())
        private set

    fun searchRoutines(
        searchByName : String,
        searchByCreator : String,
        orderBySelected : String, // TO DO
        filterByCategory : String,
        filterByClassification : String,
        filterByDifficulty : String,
        onFailure: (errorMessage: String) -> Unit
    ){
        currentSearchJob?.cancel()

        if (searchByName.isNotEmpty() && searchByName.length < 3){
            onFailure("Debe escribir por lo menos tres letras para obtener resultados")
            return
        }

        currentSearchJob = viewModelScope.launch {

            routineUIState = routineUIState.copy(
                routineList = mutableListOf(),
            )

            userUIState = userUIState.copy(
                userList = mutableListOf(),
            )

            searchUIState = searchUIState.copy(isSearching = true)

            if (searchByCreator.isNotEmpty()) {

                println("Ahora voy a buscar por creador")

                fetchUsersByUsername(searchByCreator, onFailure)

                if (userUIState.userList.isEmpty()) {
                    onFailure("User doesn't exist")
                    return@launch
                }

                routineUIState = routineUIState.copy(
                    isFetchingRoutines = true
                )

                userUIState.userList.forEach { user ->
                    if(user.id != null)
                        fetchRoutineByUserId(user.id, onFailure)
                }

                routineUIState = routineUIState.copy(
                    isFetchingRoutines = false
                )
            }

            if (searchByName.isNotEmpty()){

                println("Ahora voy a buscar por nombre de rutina")

                routineUIState = routineUIState.copy(
                    isFetchingRoutines = true
                )

                fetchRoutineByName(searchByName, onFailure)

                routineUIState = routineUIState.copy(
                    isFetchingRoutines = false
                )

            }
            println()
            if(routineUIState.categoryList.isEmpty()){

                routineUIState = routineUIState.copy(
                    isFetchingCategories = true
                )

                fetchCategories(onFailure)

                routineUIState = routineUIState.copy(
                    isFetchingCategories = false
                )
            }

                var i = 0
                routineUIState.routineList.forEach{ routine ->
                    i++

                    if(filterByCategory.isNotEmpty()){

                        var belongs = false
                        routineUIState.categoryList.forEach { category ->
                            if (routine.category?.name.equals(category.name)) {
                                belongs = true
                            }
                        }

                        if(!belongs)
                            routineUIState.routineList.removeAt(i)

                        return@forEach
                    }

                    // Aca puede haber un bug
                    if(routine.difficulty.toString() != filterByDifficulty){
                            routineUIState.routineList.removeAt(i)
                            return@forEach
                    }

                    // Aca puede haber un bug
                    if(routine.score.toString() != filterByClassification){
                        routineUIState.routineList.removeAt(i)
                        return@forEach
                    }
                }
        }

        searchUIState = searchUIState.copy(isSearching = false, searchFinished = true)
    }

    fun readyToDisplayResults(){
        searchUIState = searchUIState.copy(searchFinished = false)
    }

    private fun fetchRoutineByUserId(userId : Int, onFailure: (errorMessage: String) -> Unit) {
        currentFetchRoutinesByUserIdJob?.cancel()

        currentFetchRoutinesByUserIdJob = viewModelScope.launch {

            routineUIState = routineUIState.copy(
                isFetchingRoutine = true,
            )

            var i = 0
            val newList: MutableList<RoutineApiModel> = mutableListOf()

            try {

                while (true) {
                    val routines = gymRepository.fetchUserRoutines(
                        userId = userId,
                        page = i++,
                        orderBy = "user",
                        direction = "asc"
                    )

                    newList.addAll(routineUIState.routineList)
                    routines.content!!.forEach { routine ->
                        newList.add(routine)
                    }

                    routineUIState = routineUIState.copy(routineList = newList)

                    if (routines.isLastPage!!)
                        break
                }

            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // To Do: Cambiar
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                routineUIState = routineUIState.copy(
                    isFetchingRoutine = false,
                    isFetchingRoutines = false,
                    // fetchRoutineErrorStringId =
                )

                searchUIState = searchUIState.copy(isSearching = false)

                onFailure(errorMessage)
            }
        }
    }

    private fun fetchRoutineByName(routineName : String, onFailure: (errorMessage: String) -> Unit){
        currentFetchRoutinesByNameJob?.cancel()

        currentFetchRoutinesByNameJob = viewModelScope.launch {

            routineUIState = routineUIState.copy(
                isFetchingRoutine = true,
            )

            var i = 0
            val newList: MutableList<RoutineApiModel> = mutableListOf()

            try {
                while(true) {
                    val routines = gymRepository.fetchUserRoutines(
                        search = routineName,
                        page = i++,
                        orderBy = "name",
                        direction = "asc",
                    )

                    newList.addAll(routineUIState.routineList)
                    routines.content!!.forEach { routine ->
                        newList.add(routine)
                    }

                    routineUIState = routineUIState.copy(routineList = newList)

                    if (routines.isLastPage!!)
                        break
                }

            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // To Do: Cambiar
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                routineUIState = routineUIState.copy(
                    isFetchingRoutine = false,
                    isFetchingRoutines = false,
                    // fetchRoutineErrorStringId =
                )

                searchUIState = searchUIState.copy(isSearching = false)

                onFailure(errorMessage)
            }
        }
    }

    private fun fetchUsersByUsername(
        username: String,
        onFailure: (errorMessage: String) -> Unit
    ) {
        currentFetchUsersJob?.cancel()

        currentFetchUsersJob = viewModelScope.launch {

            userUIState = userUIState.copy(
                isFetchingUsers = true,
            )

            var i = 0
            val newList: MutableList<SmallUserApiModel> = mutableListOf()
            println(username)
            try {

                while (true) {
                    val users = gymRepository.fetchAllUsers(
                        search = username,
                        page = i++,
                        orderBy = "username",
                        direction = "asc"
                    )
                    println(users.content.toString())
                    newList.addAll(userUIState.userList)
                    users.content!!.forEach { user ->
                        newList.add(user)
                    }

                    userUIState = userUIState.copy(userList = newList)

                    if (users.isLastPage!!)
                        break
                }

                userUIState = userUIState.copy(
                    isFetchingUsers = false
                )

            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // To Do: Cambiar
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                userUIState = userUIState.copy(
                    isFetchingUsers = false,
                    // fetchUserErrorStringId
                )

                searchUIState = searchUIState.copy(isSearching = false)

                onFailure(errorMessage)
            }
        }
    }

     fun fetchCategories(onFailure: (errorMessage: String) -> Unit) {

        currentFetchCategoriesJob?.cancel()

         currentFetchCategoriesJob = viewModelScope.launch {

            var i = 0
            val newList: MutableList<Category> = mutableListOf()

            try {

                while (true) {
                    val categories = gymRepository.fetchCategories(
                        search = null,
                        page = i++,
                        orderBy = "id",
                        direction = "asc"
                    )

                    newList.addAll(routineUIState.categoryList)
                    categories.content!!.forEach { category ->
                        newList.add(category)
                    }

                    routineUIState = routineUIState.copy(categoryList = newList)

                    if (categories.isLastPage!!)
                        break
                }

                if(routineUIState.categoryList.isEmpty()){
                    onFailure("Category list empty")
                }

            } catch (e: ApiException) {
                val errorMessage = when (e.response?.code()) {
                    400 -> "Bad request: Request or data is invalid or has a constraint" // To Do: Cambiar
                    500 -> "Internal server error"
                    else -> "Unexpected error"
                }

                routineUIState = routineUIState.copy(
                    isFetchingCategories = false,
                    // fetchUserErrorStringId
                )

                searchUIState = searchUIState.copy(isSearching = false)

                onFailure(errorMessage)
            }
        }
    }
}