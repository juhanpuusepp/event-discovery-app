package com.example.evntly.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val cameraPositionState = rememberCameraPositionState()
    var locationEnabled by remember { mutableStateOf(false) }

    // Ask for permission
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

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = locationEnabled),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = locationEnabled,
            zoomControlsEnabled = false
        )
    )
}

/** Fetches the last known location and animates camera if available. */
@SuppressLint("MissingPermission")
private fun tryCenterToLastLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
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
