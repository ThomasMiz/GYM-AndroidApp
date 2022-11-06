package com.grupo14.gym_androidapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home"
) {
    // Acá típicamente haríamos algo de este estilo:
    /*NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = algunaFunBuilder
    )*/
    // Pero para no tener algunaFunBuilder como una función separada, Kotlin nos deja hacer:
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToOtherScreen = { id -> navController.navigate("other/$id") }
            )
        }

        composable(
            "other/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { navBarStackEntry ->
            OtherScreen(navBarStackEntry.arguments?.getInt("id") ?: 0)
        }/*{
                OtherScreen(it.arguments?.getInt("id"))
            }*/
    }
}