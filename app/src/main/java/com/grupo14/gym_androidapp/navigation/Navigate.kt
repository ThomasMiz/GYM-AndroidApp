package com.grupo14.gym_androidapp.navigation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo14.gym_androidapp.AppConfig
import com.grupo14.gym_androidapp.R
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.screens.*
import com.grupo14.gym_androidapp.viewmodels.GymViewModel

private val ActiveScreens = listOf(
    Escriin.LoginEscriin,
    Escriin.RegisterEscriin,
    Escriin.VerifyEscriin,
    Escriin.HomeEscriin,
    Escriin.ProfileEscriin,
    Escriin.SearchEscriin,
    Escriin.RoutineEscriin
)

fun handleOnNavigate(navController: NavController, route: String) {
    val pedro = ActiveScreens.find { it.route == route }
    if (pedro?.onNavigatedNewStart == null) {
        navController.navigate(route)
    } else {
        navController.popBackStack(
            navController.graph.startDestinationId,
            pedro.onNavigatedPopBackInclusive
        )
        navController.graph.setStartDestination(pedro.onNavigatedNewStart)
        navController.navigate(route)
    }
}

fun getCurrentScreenOf(navController: NavController): Escriin? {
    val currentScreen = navController.currentBackStackEntry?.let { navBackStackEntry ->
        navBackStackEntry.destination.route?.let { currentRoute ->
            ActiveScreens.find { escriin ->
                escriin.route == currentRoute
            }
        }
    }
    println("Current screen is $currentScreen")
    return currentScreen
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Activities(
    navController: NavHostController = rememberNavController(),
    gymRepository: GymRepository = GymRepository()
) {
    val viewModel by remember { mutableStateOf(GymViewModel(navController)) }

    BackHandler(
        enabled = navController.previousBackStackEntry != null
    ) {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            val currentScreen = viewModel.uiState.currentScreen
            if (currentScreen != null && currentScreen.showTopAppBar) {
                MyTopAppBar(currentScreen.titleResId, currentScreen.showBackButton) {
                    navController.popBackStack()
                }
            }
        },
        bottomBar = {
            val currentScreen = viewModel.uiState.currentScreen
            if (currentScreen != null && currentScreen.showBottomAppBar) {
                MyBottomAppBar(
                    currentRoute = currentScreen.route,
                    onNavigate = { route -> handleOnNavigate(navController, route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (gymRepository.getAuthtoken() == null) Escriin.LoginEscriin.route else Escriin.HomeEscriin.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            ActiveScreens.forEach { screen ->
                composable(
                    route = screen.route,
                    arguments = screen.routeArgs ?: emptyList(),
                    deepLinks = listOf(
                        //navDeepLink { uriPattern = AppConfig.BASE_URL + screen.route }
                    NavDeepLink(uri = AppConfig.BASE_URL + screen.route)
                    )
                ) { navBackStackEntry ->
                    screen.content(
                        gymRepository = gymRepository,
                        onNavigate = { route -> handleOnNavigate(navController, route) },
                        navBackStackEntry = navBackStackEntry
                    )
                }
            }
        }
    }
}

@Composable
fun MyTopAppBar(titleResId: Int, showBackButton: Boolean, onBackClicked: () -> Unit) {
    TopAppBar(
        title = { if (titleResId >= 0) Text(stringResource(titleResId)) },
        navigationIcon = if (showBackButton) ({
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Filled.ArrowBack, "backIcon")
            }
        }) else null,
        backgroundColor = colorResource(R.color.dark),
        contentColor = colorResource(R.color.white)
    )
}

@Composable
fun MyBottomAppBar(currentRoute: String, onNavigate: (route: String) -> Unit) {
    BottomNavigation(
        backgroundColor = colorResource(R.color.dark),
        contentColor = colorResource(R.color.white)
    ) {
        val searchTitle = stringResource(R.string.search)
        val searchRoute = Escriin.SearchEscriin.route
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = searchTitle) },
            label = { Text(text = searchTitle) },
            alwaysShowLabel = true,
            selected = currentRoute == searchRoute,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = { onNavigate(searchRoute) }
        )

        val homeTitle = stringResource(R.string.home)
        val homeRoute = Escriin.HomeEscriin.route
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = homeTitle) },
            label = { Text(text = homeTitle) },
            alwaysShowLabel = true,
            selected = currentRoute == homeRoute,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = { onNavigate(homeRoute) }
        )

        val profileTitle = stringResource(R.string.profile)
        val profileRoute = Escriin.ProfileEscriin.route
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = profileTitle) },
            label = { Text(text = profileTitle) },
            alwaysShowLabel = true,
            selected = currentRoute == profileRoute,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = { onNavigate(profileRoute) }
        )
    }
}
