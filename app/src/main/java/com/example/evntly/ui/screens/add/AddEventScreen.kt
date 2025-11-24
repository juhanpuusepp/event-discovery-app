package com.example.evntly.ui.screens.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

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
    val date by viewModel.selectedDate.collectAsState()
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    // Coordinates derived from selected suggestion
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLon by remember { mutableStateOf<Double?>(null) }

    val placeUi = viewModel.placeUiState.collectAsState().value
    val hasSelection = selectedLat != null && selectedLon != null

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var datePart by remember { mutableStateOf<Calendar?>(null) }

    // Typing in the location box triggers debounced API search
    fun onLocationChange(text: String) {
        location = text
        selectedLat = null
        selectedLon = null
        viewModel.searchPlacesDebounced(text)
    }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()

    // Date picker
    fun showDatePicker() {
        showDatePicker = true
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

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.add_event_title)) })
            }
        ) { innerPadding ->
            val listState = rememberLazyListState()
            val screenPad = dimensionResource(R.dimen.spacing_md)
            val itemGap = dimensionResource(R.dimen.spacing_sm)

            // DatePickerDialog composable
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    // Set the minimum selectable date to tomorrow
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= System.currentTimeMillis() // Allow dates from today onwards
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // When the user confirms, get the selected date
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val selectedDate = Calendar.getInstance().apply {
                                        timeInMillis = millis
                                    }
                                    // Save the date part and trigger the time picker
                                    datePart = selectedDate
                                    showTimePicker = true
                                }
                                showDatePicker = false // Close the date picker
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // TimePickerDialog composable
            if (showTimePicker && datePart != null) {
                TimePickerDialogWithInput(
                    initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                    initialMinute = calendar.get(Calendar.MINUTE),
                    onDismiss = { showTimePicker = false },
                    onConfirm = { hour, minute ->
                        datePart!!.set(Calendar.HOUR_OF_DAY, hour)
                        datePart!!.set(Calendar.MINUTE, minute)
                        viewModel.setSelectedDate(datePart!!.time) // Set the final date
                        showTimePicker = false
                        datePart = null // Clear the date part
                    }
                )
            }

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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = date?.let { dateFormat.format(it) } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.date_time_label)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("date_time_input"),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker() }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = stringResource(R.string.pick_date_time),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                        // Full-overlay click target so tapping anywhere opens the picker
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    onClick = { showDatePicker() },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() })
                        )
                    }
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
        Column(Modifier.padding(12.dp)) {
            Text(s.title, style = MaterialTheme.typography.titleSmall)
            s.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * Custom composable to display the Material 3 TimeInput inside a dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogWithInput(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Time", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TimeInput is the key change here
                TimeInput(state = timeState)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(timeState.hour, timeState.minute) }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}