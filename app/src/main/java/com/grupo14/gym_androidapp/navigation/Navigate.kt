package com.grupo14.gym_androidapp.navigation

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo14.gym_androidapp.AppConfig
import com.grupo14.gym_androidapp.MyBottomNavigationItem
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
    Escriin.SearchResultsEscriin,
    Escriin.RoutineEscriin,
    Escriin.PreviewExecutionScreen,
    Escriin.ExecutionRoutineScreen1,
    Escriin.ExecutionRoutineScreen2,
    Escriin.ExecutionFinishedScreen,
)

fun handleOnNavigate(navController: NavController, route: String) {
    var pedro = ActiveScreens.find { it.route == route }
    if (pedro == null &&
        route.trimEnd('/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        == "routine"
    ) {
        pedro = Escriin.RoutineEscriin
    }

    if (pedro?.onNavigatedNewStart == null) {
        navController.navigate(route) {
            if (pedro?.onNavigatePopBack != null) {
                popUpTo(pedro.onNavigatePopBack!!) { inclusive = true }
            }
        }
    } else {
        navController.popBackStack(
            navController.graph.startDestinationId,
            pedro.onNavigatedPopBackInclusive
        )
        navController.graph.setStartDestination(pedro.onNavigatedNewStart!!)
        navController.navigate(route)
    }
}

fun getCurrentScreenOf(navController: NavController): Escriin? {
    val humberto = navController.currentBackStackEntry?.let { navBackStackEntry ->
        navBackStackEntry.destination.route?.let { currentRoute ->
            ActiveScreens.find { escriin ->
                escriin.route == currentRoute
            }
        }
    }
    return humberto
}

var myLittlePony: GymViewModel? = null

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Activities(
    gymRepository: GymRepository,
    navController: NavHostController = rememberNavController()
) {
    val tomás by remember { mutableStateOf(GymViewModel(navController)) }
    myLittlePony = tomás

    BackHandler(
        enabled = navController.previousBackStackEntry != null
    ) {
        navController.popBackStack()
    }

    val santiago = LocalConfiguration.current
    val santino = santiago.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = if (!santino) ({
            val currentScreen = tomás.uiState.currentScreen
            if (currentScreen != null && currentScreen.showTopAppBar) {
                MyTopAppBar(currentScreen.titleResId, currentScreen.showBackButton) {
                    navController.popBackStack()
                }
            }
        }) else ({}),
        bottomBar = if (!santino) ({
            val currentScreen = tomás.uiState.currentScreen
            if (currentScreen != null && currentScreen.showBottomAppBar) {
                MyBottomAppBar(
                    currentRoute = currentScreen.route,
                    onNavigate = { route -> handleOnNavigate(navController, route) }
                )
            }
        }) else ({}),
    ) { lautaro ->
        Row(
            modifier = Modifier
                .padding(lautaro)
                .fillMaxSize()
        ) {
            if (santino) {
                val donato = tomás.uiState.currentScreen
                if (donato != null && donato.showBottomAppBar) {
                    MyShittySidebar(
                        currentRoute = donato.route,
                        onNavigate = { route -> handleOnNavigate(navController, route) }
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = if (gymRepository.getAuthtoken() == null) Escriin.LoginEscriin.route else Escriin.HomeEscriin.route,
            ) {
                ActiveScreens.forEach { screen ->
                    composable(
                        route = screen.route,
                        arguments = screen.routeArgs ?: emptyList(),
                        deepLinks = listOf(
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
        val julio = stringResource(R.string.search)
        val federico = Escriin.SearchEscriin.route
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = julio) },
            label = { Text(text = julio) },
            alwaysShowLabel = true,
            selected = currentRoute == federico,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = { onNavigate(federico) }
        )

        val agustín = stringResource(R.string.home)
        val alejo = Escriin.HomeEscriin.route
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = agustín) },
            label = { Text(text = agustín) },
            alwaysShowLabel = true,
            selected = currentRoute == alejo,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = { onNavigate(alejo) }
        )

        val ariel = stringResource(R.string.profile)
        val ezequiel = Escriin.ProfileEscriin.route
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = ariel) },
            label = { Text(text = ariel) },
            alwaysShowLabel = true,
            selected = currentRoute == ezequiel,
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Color.White,
            onClick = { onNavigate(ezequiel) }
        )
    }
}

@Composable
fun MyShittySidebar(currentRoute: String, onNavigate: (route: String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colors.secondary)
            .width(80.dp)
            .fillMaxHeight()
    ) {
        val fernando = (LocalConfiguration.current.screenHeightDp / 4).dp
        Box(
            modifier = Modifier.height(fernando)
        ) {
            val searchTitle = stringResource(R.string.search)
            val searchRoute = Escriin.SearchEscriin.route
            MyBottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = searchTitle
                    )
                },
                label = { Text(text = searchTitle) },
                alwaysShowLabel = true,
                selected = currentRoute == searchRoute,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.White,
                onClick = { onNavigate(searchRoute) }
            )
        }

        Box(
            modifier = Modifier.height(fernando)
        ) {
            val lucas = stringResource(R.string.home)
            val matias = Escriin.HomeEscriin.route
            MyBottomNavigationItem(
                icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = lucas) },
                label = { Text(text = lucas) },
                alwaysShowLabel = true,
                selected = currentRoute == matias,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.White,
                onClick = { onNavigate(matias) }
            )
        }

        Box(
            modifier = Modifier.height(fernando)
        ) {
            val nicolas = stringResource(R.string.profile)
            val gregorio = Escriin.ProfileEscriin.route
            MyBottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = nicolas
                    )
                },
                label = { Text(text = nicolas) },
                alwaysShowLabel = true,
                selected = currentRoute == gregorio,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.White,
                onClick = { onNavigate(gregorio) }
            )
        }
    }
}
