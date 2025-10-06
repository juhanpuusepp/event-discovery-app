package com.example.evntly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.room.Room
import com.example.evntly.data.EventDatabase
import com.example.evntly.ui.navigation.AppNavHost
import com.example.evntly.viewmodel.EventRepository
import com.example.evntly.viewmodel.EventViewModel
import com.example.evntly.viewmodel.EventViewModelFactory
import kotlin.getValue

class MainActivity : ComponentActivity() {
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
            AppNavHost(viewModel = viewModel)
        }
    }
}