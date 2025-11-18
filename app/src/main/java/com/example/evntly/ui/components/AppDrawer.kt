package com.example.evntly.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.evntly.R
import com.example.evntly.ui.navigation.Destinations

/**
 * drawer with a profile header
 * and navigation items below.
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
            // profile header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Destinations.PROFILE) }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.orangewithbgandlogo),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Test User",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "View profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider()

            // other items
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
