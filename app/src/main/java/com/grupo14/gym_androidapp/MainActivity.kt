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
        const val BASE_URL = "http://localhost:8080/api/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this;
        super.onCreate(savedInstanceState)

        val gymRepository = GymRepository()
        gymRepository.setAuthtoken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjksImlhdCI6MTY2ODI2MTk5NzQwNiwiZXhwIjoxNjY4MjY0NTg5NDA2fQ.-OjnvkmF3qy69SSy-GN1V1RuRNUl8ziU9hKKawu3vAE");

        setContent {
            GYMAndroidAppTheme {
                Activities(gymRepository = gymRepository)
            }
        }
    }
}