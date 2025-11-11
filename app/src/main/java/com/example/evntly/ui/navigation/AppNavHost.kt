package com.example.evntly.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.evntly.ui.components.AppTopBar
import com.example.evntly.ui.components.AppDrawer

/**
 * Main navigation host for the application.
 * Builds a NavController.
 * Calls the TopBar with the menu icon.
 * Calls the drawer (sidebar) that handles navigation between Map, Events, Profile.
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
    val isDarkMap = remember { mutableStateOf(false) }

    val currentRoute = navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val halfWidth = screenWidth / 2

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // tap to close, swiping disabled
        drawerContent = {
            AppDrawer(
                drawerWidth = halfWidth,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    // avoid building up duplicates on the stack
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = (route != Destinations.MAP) }
                        restoreState = (route != Destinations.MAP)
                    }
                },
                onToggleDarkMap = {
                    isDarkMap.value = !isDarkMap.value
                },
                isDarkMap = isDarkMap.value
                )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "",
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
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
                            onAddEvent = { navController.navigate(Destinations.ADD_EVENT) },
                            viewModel = viewModel,
                            isDarkMap = isDarkMap.value
                        )
                    }
                    composable(Destinations.EVENTS) {
                        EventsScreen(
                            onBack = { navController.popBackStack(Destinations.MAP, false) },
                            viewModel = viewModel
                        )
                    }
                    composable(Destinations.PROFILE) {
                        ProfileScreen()
                    }
                    composable(Destinations.ADD_EVENT) {
                        AddEventScreen(
                            onBack = { navController.popBackStack(Destinations.MAP, false) },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}