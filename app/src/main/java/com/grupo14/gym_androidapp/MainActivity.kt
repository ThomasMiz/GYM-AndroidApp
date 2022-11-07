package com.grupo14.gym_androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.grupo14.gym_androidapp.navigation.Activities
import com.grupo14.gym_androidapp.ui.theme.GYMAndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GYMAndroidAppTheme {
                Activities()
            }
        }
    }
}