package com.example.evntly.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import com.example.evntly.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(onBack: () -> Unit, viewModel: EventViewModel) {
    // Show the top bar with the name
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events") }
            )
        }
    ) { padding ->
        // show the added events, if none then relevant text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("No events yet")
        }
    }
}
