package com.example.evntly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.evntly.R
import com.example.evntly.ui.navigation.Destinations
import com.example.evntly.ui.theme.DarkGrey
import com.example.evntly.ui.theme.LightGrey

/**
 * Component to create the drawer
 */
@Composable
fun AppDrawer(
    drawerWidth: Dp,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean
) {
    Box(modifier = Modifier.width(drawerWidth)) {
        ModalDrawerSheet(
            drawerContainerColor = MaterialTheme.colorScheme.surface
        ) {
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.map)) },
                selected = currentRoute == Destinations.MAP,
                onClick = { onNavigate(Destinations.MAP) }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.events)) },
                selected = currentRoute == Destinations.EVENTS,
                onClick = { onNavigate(Destinations.EVENTS) }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.profile)) },
                selected = currentRoute == Destinations.PROFILE,
                onClick = { onNavigate(Destinations.PROFILE) }
            )
            NavigationDrawerItem(
                label = {
                    Text(
                        if (isDarkMode) stringResource(R.string.light_mode)
                        else stringResource(R.string.dark_mode)
                    )
                },
                selected = false,
                onClick = onToggleDarkMode,
                badge = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() }
                    )
                }
            )
        }
    }
}