package com.grupo14.gym_androidapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.screens.*
import com.grupo14.gym_androidapp.viewmodels.*

data class Escriin(
    val titleResId: Int,
    val showBackButton: Boolean = true,
    val route: String,
    val routeArgs: List<NamedNavArgument>? = null,
    val showTopAppBar: Boolean = true,
    val showBottomAppBar: Boolean = true,
    val onNavigatedNewStart: String? = null,
    val onNavigatedPopBackInclusive: Boolean = true,
    val content: @Composable (gymRepository: GymRepository, onNavigate: ((route: String) -> Unit), navBackStackEntry: NavBackStackEntry) -> Unit
) {
    override fun toString(): String {
        return "Escriin: $route"
    }

    companion object {
        val LoginEscriin = Escriin(
            titleResId = R.string.login,
            showBackButton = false,
            route = "login",
            showTopAppBar = false,
            showBottomAppBar = false,
            onNavigatedNewStart = "login"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(SessionViewModel(gymRepository)) }
            LoginScreen(onNavigate = onNavigate, viewModel)
        }

        val RegisterEscriin = Escriin(
            titleResId = R.string.register,
            showBackButton = false,
            route = "register",
            showTopAppBar = false,
            showBottomAppBar = false,
            onNavigatedNewStart = "login",
            onNavigatedPopBackInclusive = false
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(SessionViewModel(gymRepository)) }
            RegisterScreen(
                onNavigate = onNavigate,
                viewModel
            )
        }

        val VerifyEscriin = Escriin(
            titleResId = R.string.verifyUser,
            route = "verify",
            showTopAppBar = false,
            showBottomAppBar = false,
            onNavigatedPopBackInclusive = false
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(SessionViewModel(gymRepository)) }
            VerifyUserScreen(
                onNavigate = onNavigate,
                viewModel
            )
        }

        val HomeEscriin = Escriin(
            titleResId = R.string.home,
            showBackButton = false,
            route = "home",
            onNavigatedNewStart = "home"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(HomeViewModel(gymRepository)) }

            HomeScreen(
                viewModel = viewModel,
                onNavigateToRoutineRequested = { id -> onNavigate("routine/$id") }
            )
        }

        val SearchEscriin = Escriin(
            titleResId = R.string.search,
            showBackButton = false,
            route = "search"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            SearchScreen()
        }

        val ProfileEscriin = Escriin(
            titleResId = R.string.profile,
            showBackButton = false,
            route = "profile"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(ProfileViewModel(gymRepository)) }

            ProfileScreen(
                viewModel = viewModel,
                onSignedOut = { onNavigate("login") }
            )
        }

        val RoutineEscriin = Escriin(
            titleResId = R.string.routine,
            route = "routine/{routineId}",
            routeArgs = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(RoutineViewModel(gymRepository)) }
            RoutineScreen(
                viewModel = viewModel,
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                onNavigateToRoutineExecutionRequested = { id -> onNavigate("routine/$id/execute") }
            )
        }

        val ExecuteRoutine = Escriin(
            titleResId = R.string.execute,
            route = "routine/{routineId}/execute",
            routeArgs = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val viewModel by remember { mutableStateOf(ExecuteRoutineViewModel(gymRepository)) }
            ExecuteRoutineScreen(
                viewModel = viewModel,
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                onNavigateToRoutineExecutionRequested = { id -> onNavigate("routine/$id") }
            )
        }
    }
}