package com.example.evntly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.room.Room
import com.example.evntly.data.local.EventDatabase
import com.example.evntly.ui.navigation.AppNavHost
import com.example.evntly.data.repository.EventRepository
import com.example.evntly.ui.theme.EvntlyTheme
import com.example.evntly.ui.viewmodel.EventViewModel
import com.example.evntly.ui.viewmodel.EventViewModelFactory
import kotlin.getValue
import com.example.evntly.ui.viewmodel.AuthViewModel
import com.example.evntly.ui.viewmodel.AuthViewModelFactory



class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            EventDatabase::class.java,
            "events.db"
        ).build()

        val repository = EventRepository(db.getEventDao())

        val viewModel: EventViewModel by viewModels {
            EventViewModelFactory(repository)
        }

        installSplashScreen()
        setContent {
            // Create state to track dark mode
            val isDarkTheme = remember { mutableStateOf(false) }

            EvntlyTheme(
                darkTheme = isDarkTheme.value,
                dynamicColor = false
            ) {
                AppNavHost(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    isDarkTheme = isDarkTheme.value,
                    onToggleTheme = { isDarkTheme.value = !isDarkTheme.value },
                    onThemeChange = { isDarkTheme.value = it }
                )
            }
        }
    }
}