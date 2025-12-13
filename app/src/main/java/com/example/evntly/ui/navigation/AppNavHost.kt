package com.example.evntly.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.evntly.ui.components.AppTopBar
import com.example.evntly.ui.components.AppDrawer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.evntly.ui.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.evntly.ui.screens.profile.LoginScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    viewModel: EventViewModel,
    authViewModel: AuthViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onThemeChange: (Boolean) -> Unit
) {
    val authUiState = authViewModel.uiState.collectAsStateWithLifecycle().value

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val startDestination = if (authUiState.isLoggedIn) {
        Destinations.MAP
    } else {
        Destinations.LOGIN
    }

    val currentRoute = navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val halfWidth = screenWidth / 2

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            AppDrawer(
                drawerWidth = halfWidth,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = (route != Destinations.MAP)
                        }
                        restoreState = (route != Destinations.MAP)
                    }
                },
                onToggleDarkMode = onToggleTheme,
                isDarkMode = isDarkTheme,
                authViewModel = authViewModel
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopBar(
                    title = "",
                    onMenuClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open()
                            else drawerState.close()
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
                    startDestination = startDestination,
                    modifier = modifier
                ) {
                    composable(Destinations.LOGIN) {
                        LoginScreen(
                            viewModel = authViewModel,
                            onAuthenticated = {
                                navController.navigate(Destinations.MAP) {
                                    popUpTo(Destinations.LOGIN) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Destinations.MAP) {
                        MapScreen(
                            onAddEvent = { navController.navigate(Destinations.ADD_EVENT) },
                            viewModel = viewModel,
                            isDarkMap = isDarkTheme
                        )
                    }
                    composable(Destinations.EVENTS) {
                        EventsScreen(viewModel = viewModel)
                    }
                    composable(Destinations.PROFILE) {
                        ProfileScreen(
                            authViewModel = authViewModel,
                            onThemeChange = onThemeChange
                        )
                    }
                    composable(Destinations.ADD_EVENT) {
                        AddEventScreen(
                            onBack = {
                                navController.popBackStack(Destinations.MAP, false)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}