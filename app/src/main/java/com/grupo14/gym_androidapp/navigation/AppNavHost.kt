package com.grupo14.gym_androidapp.navigation

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grupo14.gym_androidapp.R

@Composable
fun MyTopAppBar(onBackClicked: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.pedro)) },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Filled.ArrowBack, "backIcon")
            }
        },
        backgroundColor = colorResource(R.color.dark),
        contentColor = colorResource(R.color.white)
    )
}

@Composable
fun MyBottomAppBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = colorResource(R.color.dark),
        contentColor = colorResource(R.color.white)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val searchTitle = stringResource(R.string.search)
        val searchRoute = "search"
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = searchTitle) },
            label = { Text(text = searchTitle) },
            alwaysShowLabel = true,
            selected = currentRoute == searchRoute,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = {
                navController.navigate(searchRoute) {
                    navController.graph.startDestinationRoute?.let { screenRoute ->
                        popUpTo(screenRoute) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        val homeTitle = stringResource(R.string.home)
        val homeRoute = "home"
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = homeTitle) },
            label = { Text(text = homeTitle) },
            alwaysShowLabel = true,
            selected = currentRoute == homeRoute,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = {
                navController.navigate(homeRoute) {
                    navController.graph.startDestinationRoute?.let { screenRoute ->
                        popUpTo(screenRoute) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        val profileTitle = stringResource(R.string.profile)
        val profileRoute = "profile"
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = profileTitle) },
            label = { Text(text = profileTitle) },
            alwaysShowLabel = true,
            selected = currentRoute == profileRoute,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = {
                navController.navigate(profileRoute) {
                    navController.graph.startDestinationRoute?.let { screenRoute ->
                        popUpTo(screenRoute) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}