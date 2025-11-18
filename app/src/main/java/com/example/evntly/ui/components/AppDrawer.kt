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
import com.google.android.gms.maps.model.MapStyleOptions

/**
 * Component to create the drawer
 */

@Composable
fun AppDrawer(
    drawerWidth: Dp,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onToggleDarkMap: () -> Unit,
    isDarkMap: Boolean
) {
    Box(modifier = Modifier.width(drawerWidth)) {
        ModalDrawerSheet {
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
                        if (isDarkMap) stringResource(R.string.light_mode)
                        else stringResource(R.string.dark_mode)
                    )
                },
                selected = false,
                onClick = { onToggleDarkMap },
                badge = { //The slider, left there for now if there is interest in it
                    Switch(
                        checked = isDarkMap,
                        onCheckedChange = { onToggleDarkMap() }
                    )
                }
            )
        }
    }
}
