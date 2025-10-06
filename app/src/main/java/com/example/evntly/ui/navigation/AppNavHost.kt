package com.example.evntly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evntly.ui.screens.map.MapScreen
import com.example.evntly.ui.screens.events.EventsScreen
import com.example.evntly.viewmodel.EventViewModel

/**
 * navigation graph for the app
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier, viewModel: EventViewModel) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Destinations.MAP, // declare the landing page
        modifier = modifier
    ) {
        composable(Destinations.MAP) {
            MapScreen(
                onOpenEvents = { nav.navigate(Destinations.EVENTS) }
            )
        }
        composable(Destinations.EVENTS) {
            EventsScreen(
                onBack = { nav.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
