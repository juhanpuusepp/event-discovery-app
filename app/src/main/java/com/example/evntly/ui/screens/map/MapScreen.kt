package com.example.evntly.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.evntly.R
import com.example.evntly.ui.viewmodel.EventViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Helper to create a BitmapDescriptor from a PNG in drawable,
 * scaled to the given size in dp.
 */
private fun bitmapDescriptorFromRes(
    context: Context,
    @DrawableRes resId: Int,
    sizeDp: Dp
): BitmapDescriptor? {
    val resources = context.resources
    val density = resources.displayMetrics.density
    val sizePx = (sizeDp.value * density).toInt().coerceAtLeast(1)

    val originalBitmap = BitmapFactory.decodeResource(resources, resId) ?: return null

    val scaledBitmap = Bitmap.createScaledBitmap(
        originalBitmap,
        sizePx,
        sizePx,
        true
    )

    // ensure Maps SDK is initialized before using BitmapDescriptorFactory
    return try {
        MapsInitializer.initialize(context.applicationContext)
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
/**
 * Shows the Google Map via Maps Compose,
 * requests location permission,
 * animates the camera to last known location once granted.
 * Creates a floating button to create an event
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onAddEvent: () -> Unit,
    viewModel: EventViewModel,
    isDarkMap: Boolean
) {
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current

    val mapStyle = remember(isDarkMap) {
        if (isDarkMap)
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_darkmode)
        else
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_lightmode)
    }

    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val cameraPositionState = rememberCameraPositionState()
    var locationEnabled by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            locationEnabled = granted
            if (granted) {
                tryCenterToLastLocation(fusedLocationClient, cameraPositionState, scope)
            }
        }

    LaunchedEffect(Unit) {
        val perm = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = ContextCompat.checkSelfPermission(context, perm) ==
                PackageManager.PERMISSION_GRANTED

        if (granted) {
            locationEnabled = true
            tryCenterToLastLocation(fusedLocationClient, cameraPositionState, scope)
        } else {
            permissionLauncher.launch(perm)
        }
    }

    // create the custom marker icon once and remember it
    val markerIcon = remember {
        bitmapDescriptorFromRes(
            context = context,
            resId = R.drawable.marker,
            sizeDp = 40.dp // marker size can be tweaked here
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapStyleOptions = mapStyle,
                isMyLocationEnabled = locationEnabled
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = locationEnabled,
                zoomControlsEnabled = false
            )
        ) {
            // draw a marker for each event that has coordinates
            events
                .filter { it.latitude != null && it.longitude != null }
                .forEach { event ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(event.latitude!!, event.longitude!!)
                        ),
                        title = event.name,
                        snippet = event.location,
                        icon = markerIcon
                    )
                }
        }

        FloatingActionButton(
            containerColor = Color(0xFFE86450),
            contentColor = Color.White,
            onClick = onAddEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_event))
        }
    }
}

// gets the last known location and moves the camera to that location
@SuppressLint("MissingPermission")
private fun tryCenterToLastLocation(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    scope: CoroutineScope
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val here = LatLng(location.latitude, location.longitude)
            scope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(here, 15f))
            }
        }
    }
}
