package com.example.evntly.ui.screens.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evntly.domain.model.Event
import com.example.evntly.ui.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for adding a new event.
 * Allows the user to input event details (name, date + time, price, description, location)
 * and saves the event to the database through the ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onBack: () -> Unit, // callback to return to the previous screen
    viewModel: EventViewModel = viewModel()
) {

    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Date?>(null) }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())


    val calendar = Calendar.getInstance()
    val context = LocalContext.current

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

        datePicker.datePicker.minDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000
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
            TopAppBar(
                title = { Text("Add Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Map")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = date?.let { dateFormat.format(it) } ?: "",
                onValueChange = {},
                label = { Text("Date & Time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha),
                    disabledLabelColor = LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha),
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha)
                ),
                trailingIcon = {
                    IconButton(onClick = { showDateTimePicker() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date and Time")
                    }
                }
            )

            OutlinedTextField(
                value = price,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d{0,8}(\\.\\d{0,2})?$"))) {
                        price = newValue
                    }
                },
                label = { Text("Price (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                )
            )

            if (!isPriceValid && price.isNotBlank()) {
                Text(
                    text = "Price must be a valid number (up to 2 decimals)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (!isPricePositive && price.isNotBlank()) {
                Text(
                    text = "Price cannot be negative",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    viewModel.addEvent(
                        Event(
                            name = name,
                            date = date!!,
                            price = price.toDouble(),
                            description = description,
                            location = location
                        )
                    )
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Save Event")
            }
        }
    }
}
