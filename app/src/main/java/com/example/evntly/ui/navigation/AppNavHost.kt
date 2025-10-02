package com.example.evntly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evntly.ui.screens.map.MapScreen
import com.example.evntly.ui.screens.events.EventsScreen

// navigation graph for the app
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Destinations.MAP,
        modifier = modifier
    ) {
        composable(Destinations.MAP) {
            MapScreen(
                onOpenEvents = { nav.navigate(Destinations.EVENTS) }
            )
        }
        composable(Destinations.EVENTS) {
            EventsScreen(
                onBack = { nav.popBackStack() }
            )
        }
    }
}
