package com.example.evntly.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.remember
import com.example.evntly.R
import com.example.evntly.domain.model.Event
import com.example.evntly.ui.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Uses a Flow<List<Event>> exposed by EventViewModel (collected as state)
 * and renders either an empty state or a LazyColumn of cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(onBack: () -> Unit, viewModel: EventViewModel) {
    val events by viewModel.events.collectAsState()

    Scaffold { padding ->
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_events_yet))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(dimensionResource(R.dimen.spacing_md)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onDelete = { viewModel.deleteEvent(event) }
                    )
                }
            }
        }
    }
}

/**
 * One event card: title, location, date/time, price + delete action
 */
@Composable
fun EventCard(event: Event, onDelete: () -> Unit) {
    val gap = dimensionResource(R.dimen.spacing_xs)
    val cardPad = dimensionResource(R.dimen.spacing_md)
    val iconTint = MaterialTheme.colorScheme.primary
    val fmt = rememberDateFormat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.elevation_mid)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPad),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                // Title
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium
                )
                // Location
                InfoRow(
                    icon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp)) },
                    text = event.location
                )
                // Date & time
                InfoRow(
                    icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp)) },
                    text = fmt.format(event.date)
                )
                // Price
                InfoRow(
                    icon = { Icon(Icons.Outlined.Euro, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp)) },
                    text = String.format(Locale.getDefault(), "%.2f", event.price)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_event),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Small row with a leading icon and a single line of text,
 * used to display event attributes (location, date/time, price)
 */
@Composable
private fun InfoRow(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // small fixed gap between icon/text
    ) {
        icon()
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Memorizes a date-time formatter ("EEE, dd.MM.yyyy HH:mm") in the current Locale
 * to avoid recreating the SimpleDateFormat on every recomposition
 */
@Composable
private fun rememberDateFormat(): SimpleDateFormat =
    remember {
        SimpleDateFormat("EEE, dd.MM.yyyy HH:mm", Locale.getDefault())
    }
