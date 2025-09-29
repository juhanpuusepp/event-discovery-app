package com.example.evntly

import android.annotation.SuppressLint
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Build once per composition
            val context = this
            val fusedLocationClient = remember {
                LocationServices.getFusedLocationProviderClient(context)
            }
            val cameraPositionState = rememberCameraPositionState()

            MapScreen(
                fusedLocationClient = fusedLocationClient,
                cameraPositionState = cameraPositionState
            )
        }
    }
}

@Composable
fun MapScreen(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var locationEnabled by remember { mutableStateOf(false) }

    // Launcher to request the single permission
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            locationEnabled = granted
            if (granted) {
                tryCenterToLastLocation(fusedLocationClient, cameraPositionState, scope)
            }
        }

    // On first composition, check and request permission
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
        properties = MapProperties(
            isMyLocationEnabled = locationEnabled
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = locationEnabled,
            zoomControlsEnabled = false
        )
    )
}

/**
 * Fetches the last known location and focuses the camera to it
 * Safe to call multiple times, does nothing if location is null
 */
@SuppressLint("MissingPermission") // we only call this after permission is granted
private fun tryCenterToLastLocation(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    scope: CoroutineScope
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val here = LatLng(location.latitude, location.longitude)
            scope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(here, 15f)
                )
            }
        }
    }
}
