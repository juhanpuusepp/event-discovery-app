package com.example.evntly.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evntly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // Top bar with name
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Profile picture
            Image(
                painter = painterResource(id = R.drawable.orangewithbgandlogo), // add a placeholder in res/drawable
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = "Jane Doe",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = "jane.doe@example.com",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = { /* TODO: handle edit */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Edit Profile")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* TODO: handle logout */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Log Out")
            }
        }
    }
}
