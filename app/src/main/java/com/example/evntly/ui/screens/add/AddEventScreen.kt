package com.example.evntly.ui.screens.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evntly.R
import com.example.evntly.domain.model.Event
import com.example.evntly.domain.model.PlaceSuggestion
import com.example.evntly.ui.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for adding a new event.
 * Allows the user to input event details (name, date + time, price, description, location)
 * and saves the event to the database through the ViewModel.
 *
 * On save, constructs an Event.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onBack: () -> Unit, // callback to return to the previous screen
    viewModel: EventViewModel = viewModel()
) {
    // Form fields
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Date?>(null) }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    // Coordinates derived from selected suggestion
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLon by remember { mutableStateOf<Double?>(null) }

    val placeUi = viewModel.placeUiState.collectAsState().value
    val hasSelection = selectedLat != null && selectedLon != null

    // Typing in the location box triggers debounced API search
    fun onLocationChange(text: String) {
        location = text
        selectedLat = null
        selectedLon = null
        viewModel.searchPlacesDebounced(text)
    }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    // Date + time picker
    fun showDateTimePicker() {
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val timePicker = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                        selectedDate.set(Calendar.MINUTE, minute)
                        date = selectedDate.time
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() + 24L * 60 * 60 * 1000
        datePicker.show()
    }

    // Validation rules
    val isPriceValid = remember(price) {
        // Allows only numbers with up to two decimal places
        price.matches(Regex("^\\d{0,8}(\\.\\d{0,2})?$"))
    }

    val isPricePositive = price.toDoubleOrNull()?.let { it >= 0 } ?: false
    val isFormValid = name.isNotBlank() &&
            date != null &&
            isPriceValid &&
            isPricePositive &&
            description.isNotBlank() &&
            location.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.add_event_title)) })
        }
    ) { innerPadding ->
        val listState = rememberLazyListState()
        val screenPad = dimensionResource(R.dimen.spacing_md)
        val itemGap = dimensionResource(R.dimen.spacing_sm)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPad),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(itemGap)
        ) {
            // Event name
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.event_name_label)) },
                    modifier = Modifier.fillMaxWidth().testTag("name_input"),
                    singleLine = true
                )
            }

            // Date & time
            item {
                OutlinedTextField(
                    value = date?.let { dateFormat.format(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.date_time_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDateTimePicker() }
                        .testTag("date_time_input"),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showDateTimePicker() }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.pick_date_time),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            // Price
            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d{0,8}(\\.\\d{0,2})?$"))) {
                            price = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.price_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("price_input"),
                    singleLine = true
                )
            }

            // Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_label)) },
                    modifier = Modifier.fillMaxWidth().testTag("description_input"),
                    maxLines = 3
                )
            }

            // Location
            item {
                OutlinedTextField(
                    value = location,
                    onValueChange = { onLocationChange(it) },
                    label = { Text(stringResource(R.string.location_label)) },
                    modifier = Modifier.fillMaxWidth().testTag("location_input"),
                    singleLine = true
                )
            }

            // Loading indicator
            if (placeUi.isLoading) {
                item {
                    Row(modifier = Modifier.padding(top = itemGap)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                        Text(stringResource(R.string.searching_places))
                    }
                }
            }

            // Error/No results
            if (!placeUi.isLoading
                && !hasSelection
                && (placeUi.error != null || (location.length >= 3 && placeUi.suggestions.isEmpty()))
            ) {
                item {
                    Text(
                        text = placeUi.error ?: stringResource(R.string.no_results),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = itemGap)
                    )
                }
            }

            // Suggestions list
            if (placeUi.suggestions.isNotEmpty()) {
                items(placeUi.suggestions) { s: PlaceSuggestion ->
                    SuggestionRow(s) {
                        location = listOfNotNull(s.title, s.subtitle).joinToString(", ")
                        selectedLat = s.latitude
                        selectedLon = s.longitude
                        viewModel.clearPlaceSuggestions()
                    }
                }
            }

            // Save Event button
            item {
                Button(
                    onClick = {
                        viewModel.addEvent(
                            Event(
                                name = name,
                                date = date!!,
                                price = price.toDouble(),
                                description = description,
                                location = location,
                                latitude = selectedLat,
                                longitude = selectedLon
                            )
                        )
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_button"),
                    enabled = isFormValid && hasSelection
                ) {
                    Text(stringResource(R.string.save_event))
                }
            }

            // Spacer at the bottom so the button has room above the keyboard
            item { Spacer(Modifier.height(dimensionResource(R.dimen.spacing_sm))) }
        }
    }
}

/**
 * One suggestion row (card) in the autocomplete list
 */
@Composable
private fun SuggestionRow(s: PlaceSuggestion, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_low))
    ) {
        Column(Modifier.padding(dimensionResource(R.dimen.spacing_md))) {
            Text(s.title, style = MaterialTheme.typography.titleSmall)
            s.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
