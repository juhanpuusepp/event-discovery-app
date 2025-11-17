package com.example.evntly.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.evntly.R
import com.example.evntly.domain.model.Event
import com.example.evntly.ui.viewmodel.EventViewModel

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
 * object that defines how one event should look like
 */
@Composable
fun EventCard(event: Event, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_mid))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_md)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.name, style = MaterialTheme.typography.titleMedium)
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_xs))
                    )
                }
                if (event.location.isNotBlank()) {
                    Text(
                        text = "📍 ${event.location}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_xs))
                    )
                }
                /*
                if (event.date.isNotBlank()) {
                    Text(
                        text = "📅 ${event.date}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                 */
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_event),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
