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
    val onNavigatePopBack: String? = null,
    val content: @Composable (gymRepository: GymRepository, onNavigate: ((route: String) -> Unit), navBackStackEntry: NavBackStackEntry) -> Unit
) {
    override fun toString(): String {
        return "Escriin: $route"
    }

    companion object {
        private var sessionViewModel: SessionViewModel? = null
        private var searchViewModel: SearchViewModel? = null

        val LoginEscriin = Escriin(
            titleResId = R.string.login,
            showBackButton = false,
            route = "login",
            showTopAppBar = false,
            showBottomAppBar = false,
            onNavigatedNewStart = "login"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            if (sessionViewModel == null)
                sessionViewModel = SessionViewModel(gymRepository)
            val marcos by remember { mutableStateOf(sessionViewModel!!) }
            LoginScreen(onNavigate = onNavigate, marcos)
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
            if (sessionViewModel == null)
                sessionViewModel = SessionViewModel(gymRepository)
            val pedro by remember { mutableStateOf(sessionViewModel!!) }
            RegisterScreen(
                onNavigate = onNavigate,
                pedro
            )
        }

        val VerifyEscriin = Escriin(
            titleResId = R.string.verifyUser,
            route = "verify",
            showTopAppBar = false,
            showBottomAppBar = false,
            onNavigatedPopBackInclusive = false
        ) { gymRepository, onNavigate, navBackStackEntry ->
            if (sessionViewModel == null)
                sessionViewModel = SessionViewModel(gymRepository)
            val mariano by remember { mutableStateOf(sessionViewModel!!) }
            VerifyUserScreen(
                onNavigate = onNavigate,
                mariano
            )
        }

        val HomeEscriin = Escriin(
            titleResId = R.string.home,
            showBackButton = false,
            route = "home",
            onNavigatedNewStart = "home"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val joaquín by remember { mutableStateOf(HomeViewModel(gymRepository)) }

            HomeScreen(
                viewModel = joaquín,
                onNavigateToRoutineRequested = { id -> onNavigate("routine/$id") }
            )
        }

        val SearchEscriin = Escriin(
            titleResId = R.string.search,
            showBackButton = false,
            route = "search"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            if (searchViewModel == null)
                searchViewModel = SearchViewModel(gymRepository)

            val valentín by remember { mutableStateOf(searchViewModel!!) }
            SearchScreen(
                onNavigate = onNavigate,
                viewModel = valentín
            )
        }

        val SearchResultsEscriin = Escriin(
            titleResId = R.string.searchResults,
            showBottomAppBar = false,
            route = "search/results?search={search}&username={username}&categoryId={categoryId}&difficulty={difficulty}&score={score}&orderBy={orderBy}&direction={direction}",
            routeArgs = listOf(
                navArgument("search") { defaultValue = null; nullable = true; type = NavType.StringType },
                navArgument("userId") { defaultValue = -1; type = NavType.IntType },
                navArgument("categoryId") { defaultValue = -1; type = NavType.IntType },
                navArgument("difficulty") { defaultValue = null; nullable = true; type = NavType.StringType },
                navArgument("score") { defaultValue = -1; type = NavType.IntType },
                navArgument("orderBy") { defaultValue = null; nullable = true; type = NavType.StringType },
                navArgument("direction") { defaultValue = null; nullable = true; type = NavType.StringType }
            )
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val search = navBackStackEntry.arguments?.getString("search")
            val username = navBackStackEntry.arguments?.getString("username")
            val categoryId = navBackStackEntry.arguments?.getInt("categoryId")
            val difficulty = navBackStackEntry.arguments?.getString("difficulty")
            val score = navBackStackEntry.arguments?.getInt("score")
            val orderBy = navBackStackEntry.arguments?.getString("orderBy")
            val direction = navBackStackEntry.arguments?.getString("direction")

            val jorge by remember { mutableStateOf(SearchResultsViewModel(gymRepository)) }
            jorge.initialize(search, username, categoryId, difficulty, score, orderBy, direction)

            SearchResultsScreen(
                onNavigateToRoutineRequested = { id -> onNavigate("routine/$id") },
                viewModel = jorge
            )
        }

        val ProfileEscriin = Escriin(
            titleResId = R.string.profile,
            showBackButton = false,
            route = "profile"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val iván by remember { mutableStateOf(ProfileViewModel(gymRepository)) }

            ProfileScreen(
                viewModel = iván,
                onSignedOut = { onNavigate("login") }
            )
        }

        val RoutineEscriin = Escriin(
            titleResId = R.string.routine,
            route = "routine/{routineId}",
            routeArgs = listOf(navArgument("routineId") { type = NavType.IntType }),
            onNavigatePopBack = "routine/{routineId}"
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val carlos by remember { mutableStateOf(RoutineViewModel(gymRepository)) }
            RoutineScreen(
                viewModel = carlos,
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                onNavigateToRoutineExecutionRequested = { id -> onNavigate("routine/$id/preview") }
            )
        }

        val PreviewExecutionScreen = Escriin(
            titleResId = R.string.preview,
            showBottomAppBar = false,
            showTopAppBar = false,
            route = "routine/{routineId}/preview",
            routeArgs = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { gymRepository, onNavigate, navBackStackEntry ->
            PreviewExecutionScreen(
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                onNavigateToExecutionMode = { id, mode -> onNavigate("routine/$id/execute/$mode") },
                onNavigateToRoutineRequested = { id -> onNavigate("routine/$id") }
            )
        }

        val ExecutionRoutineScreen1 = Escriin(
            titleResId = R.string.execute,
            showBottomAppBar = false,
            showTopAppBar = false,
            route = "routine/{routineId}/execute/1",
            routeArgs = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val kevin by remember { mutableStateOf(ExecuteRoutineViewModel(gymRepository)) }
            ExecutionRoutineScreen1(
                viewModel = kevin,
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                onNavigateToRoutine = { id -> onNavigate("routine/$id") },
                onNavigateToFinishScreen = {id, seconds -> onNavigate("routine/$id/finish?seconds=$seconds",)}
            )
        }

        val ExecutionRoutineScreen2 = Escriin(
            titleResId = R.string.execute,
            showBottomAppBar = false,
            showTopAppBar = false,
            route = "routine/{routineId}/execute/2",
            routeArgs = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { gymRepository, onNavigate, navBackStackEntry ->
            val lorenzo by remember { mutableStateOf(ExecuteRoutineViewModel(gymRepository)) }
            ExecutionRoutineScreen2(
                viewModel = lorenzo,
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                onNavigateToRoutine = { id -> onNavigate("routine/$id") },
                onNavigateToFinishScreen = {id, seconds -> onNavigate("routine/$id/finish?seconds=$seconds",)}
            )
        }

        val ExecutionFinishedScreen = Escriin(
            titleResId = R.string.finish,
            showBottomAppBar = false,
            showTopAppBar = false,
            route = "routine/{routineId}/finish?seconds={seconds}",
            routeArgs = listOf(
                navArgument("routineId") { type = NavType.IntType },
                navArgument("seconds") {type = NavType.IntType }
            )
        ) { gymRepository, onNavigate, navBackStackEntry ->
            ExecutionFinishedScreen(
                routineId = navBackStackEntry.arguments?.getInt("routineId") ?: -1,
                seconds = navBackStackEntry.arguments?.getInt("seconds")?: -1,
                onNavigateToRoutine = { id -> onNavigate("routine/$id") }
            )
        }

    }
}