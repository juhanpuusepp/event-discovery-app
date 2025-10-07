package com.example.evntly.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evntly.ui.screens.add.AddEventScreen
import com.example.evntly.ui.screens.events.EventsScreen
import com.example.evntly.ui.screens.map.MapScreen
import com.example.evntly.ui.screens.profile.ProfileScreen
import com.example.evntly.ui.viewmodel.EventViewModel
import kotlinx.coroutines.launch

/**
 * Main navigation host for the application.
 * Handles navigation between Map, Add Event, Events, and Profile screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    viewModel: EventViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get screen width to calculate half width dynamically
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val halfWidth = screenWidth / 2

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            // Wrap drawer sheet in a Box with half the screen width
            Box(modifier = Modifier.width(halfWidth)) {
                ModalDrawerSheet {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()

                    NavigationDrawerItem(
                        label = { Text("Map") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Destinations.MAP)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Events") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Destinations.EVENTS)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Profile") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("profile")
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Destinations.MAP,
                    modifier = modifier
                ) {
                    composable(Destinations.MAP) {
                        MapScreen(
                            onAddEvent = { navController.navigate(Destinations.ADD_EVENT) }
                        )
                    }
                    composable(Destinations.EVENTS) {
                        EventsScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = viewModel
                        )
                    }
                    composable(Destinations.PROFILE) {
                        ProfileScreen()
                    }
                    composable(Destinations.ADD_EVENT) {
                        AddEventScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}