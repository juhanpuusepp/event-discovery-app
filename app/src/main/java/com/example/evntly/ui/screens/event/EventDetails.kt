package com.example.evntly.ui.screens.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.evntly.domain.model.Event
import com.example.evntly.R
import com.example.evntly.ui.theme.EvntlyOrange
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Shows an event detail sheet using Material 3 ModalBottomSheet.
 * Starts half-height (50%) and supports drag to expand to full screen.
 * Keeps a minimum height so short content still fills half the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetails(
    event: Event,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    // Lighter scrim so the map is dimmed but still visible
    val scrim = MaterialTheme.colorScheme.scrim.copy(alpha = 0.05f)

    val corner = dimensionResource(R.dimen.spacing_lg)
    val outerPadH = dimensionResource(R.dimen.spacing_2xl)
    val outerPadV = dimensionResource(R.dimen.spacing_md)
    val rowGap   = dimensionResource(R.dimen.spacing_sm)
    val minHalfHeight = (LocalConfiguration.current.screenHeightDp * 0.5f).dp

    // Date format
    val dt = SimpleDateFormat("EEE, dd.MM.yyyy HH:mm", Locale.getDefault())
    val formattedDate = dt.format(event.date)

    LaunchedEffect(Unit) {
        // Open to half height first
        if (sheetState.currentValue != SheetValue.PartiallyExpanded &&
            sheetState.hasPartiallyExpandedState) {
            sheetState.partialExpand()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = corner, topEnd = corner),
        dragHandle = { BottomSheetDefaults.DragHandle(color = EvntlyOrange) },
        scrimColor = scrim,
        sheetState = sheetState
    ) {
        // Ensure minimum 50% of the screen height even when content is short
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHalfHeight)
                .padding(horizontal = outerPadH, vertical = outerPadV)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(rowGap)
        ) {
            // title
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            // date & time
            Row(horizontalArrangement = Arrangement.spacedBy(rowGap)) {
                Text("📅")
                Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium)
            }

            // location
            Row(horizontalArrangement = Arrangement.spacedBy(rowGap)) {
                Text("📍")
                Text(text = event.location, style = MaterialTheme.typography.bodyMedium)
            }

            // price
            Row(horizontalArrangement = Arrangement.spacedBy(rowGap)) {
                Text("💶")
                Text(text = event.price.toString() + "€", style = MaterialTheme.typography.bodyMedium)
            }

            // description
            Spacer(Modifier.height(rowGap))
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(rowGap))
        }
    }
}
