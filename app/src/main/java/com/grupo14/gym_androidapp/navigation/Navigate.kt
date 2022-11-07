package com.grupo14.gym_androidapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grupo14.gym_androidapp.screens.*


@Composable
fun Activities(navController: NavHostController = rememberNavController()){

    Scaffold(
        topBar = { MyTopAppBar { navController.popBackStack() } },
        bottomBar = { MyBottomAppBar(navController) },
        content = { padding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                Modifier.padding(padding)

            ) {

                composable(route = "login") {
                    LoginScreen(navController)
                }

                composable(route = "register") {
                    RegisterScreen(navController)
                }

                composable(route = "home") {
                    HomeScreen(
                        onNavigateToOtherScreen = { id -> navController.navigate("other/$id") }
                    )
                }

                composable(
                    route = "other/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { navBarStackEntry ->
                    OtherScreen(navBarStackEntry.arguments?.getInt("id") ?: 0)
                }

                composable(route = "search") {
                    SearchScreen()
                }

                composable(route = "profile") {
                    ProfileScreen()
                }

            }
        }
    )
}

