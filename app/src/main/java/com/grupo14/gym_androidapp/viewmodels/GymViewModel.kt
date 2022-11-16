package com.grupo14.gym_androidapp.viewmodels

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.grupo14.gym_androidapp.navigation.Escriin
import com.grupo14.gym_androidapp.navigation.getCurrentScreenOf

data class GymUiState(
    val currentScreen: Escriin? = null
)

class GymViewModel(
    navController: NavController
) : ViewModel(), NavController.OnDestinationChangedListener {
    var uiState by mutableStateOf(GymUiState())
        private set

    init {
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        uiState = uiState.copy(
            currentScreen = getCurrentScreenOf(controller)
        )
    }
}