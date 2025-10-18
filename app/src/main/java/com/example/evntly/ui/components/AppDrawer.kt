package com.example.evntly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.evntly.ui.navigation.Destinations

/**
 * Component to create the drawer
 */

@Composable
fun AppDrawer(
    drawerWidth: Dp,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(modifier = Modifier.width(drawerWidth)) {
        ModalDrawerSheet {
            NavigationDrawerItem(
                label = { Text("Map") },
                selected = currentRoute == Destinations.MAP,
                onClick = { onNavigate(Destinations.MAP) }
            )
            NavigationDrawerItem(
                label = { Text("Events") },
                selected = currentRoute == Destinations.EVENTS,
                onClick = { onNavigate(Destinations.EVENTS) }
            )
            NavigationDrawerItem(
                label = { Text("Profile") },
                selected = currentRoute == Destinations.PROFILE,
                onClick = { onNavigate(Destinations.PROFILE) }
            )
        }
    }
}
