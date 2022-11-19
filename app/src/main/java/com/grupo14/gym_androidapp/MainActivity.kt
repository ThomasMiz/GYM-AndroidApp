package com.grupo14.gym_androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.grupo14.gym_androidapp.api.GymRepository
import com.grupo14.gym_androidapp.navigation.Activities
import com.grupo14.gym_androidapp.ui.theme.GYMAndroidAppTheme

class MainActivity : ComponentActivity() {
    companion object {
        var instance: MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this;
        super.onCreate(savedInstanceState)

        val dario = GymRepository(applicationContext)

        if (AppConfig.INITIAL_AUTH_TOKEN != null)
            dario.setAuthtoken(AppConfig.INITIAL_AUTH_TOKEN);

        setContent {
            GYMAndroidAppTheme {
                Activities(gymRepository = dario)
            }
        }
    }
}
