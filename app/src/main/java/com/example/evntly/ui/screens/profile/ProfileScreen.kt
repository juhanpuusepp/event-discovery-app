package com.example.evntly.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.evntly.ui.viewmodel.AuthUiState
import com.example.evntly.ui.viewmodel.AuthViewModel

/**
 * Screen displaying the currently authenticated user's profile information
 */
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onThemeChange: (Boolean) -> Unit
) {
    val authUiState: AuthUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val user = authUiState.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (user == null) {
            Text(
                text = "No user is currently signed in.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Name: ${user.displayName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gender: ${user.gender}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onThemeChange(false)
                    authViewModel.signOut() }
            ) {
                Text(text = "Log out")
            }
        }
    }
}
