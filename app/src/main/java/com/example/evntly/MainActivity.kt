package com.example.evntly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.evntly.data.local.EventDatabase
import com.example.evntly.ui.navigation.AppNavHost
import com.example.evntly.data.repository.EventRepository
import com.example.evntly.domain.model.Event
import com.example.evntly.ui.theme.EvntlyTheme
import com.example.evntly.ui.viewmodel.EventViewModel
import com.example.evntly.ui.viewmodel.EventViewModelFactory
import kotlinx.coroutines.launch
import java.util.Date
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

        // temporary hardcoded event
        lifecycleScope.launch {
            val sampleEvent = Event(
                name = "Test Concert",
                date = Date(),
                price = 15.0,
                description = "Sample event for testing map markers",
                location = "Tartu Town Hall Square",
                latitude = 58.3780,   // Tartu Town Hall
                longitude = 26.7225
            )
            viewModel.addEvent(sampleEvent)
        }

        installSplashScreen()
        setContent {
            EvntlyTheme {
                AppNavHost(viewModel = viewModel)
            }
        }
    }
}