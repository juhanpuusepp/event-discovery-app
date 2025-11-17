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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.evntly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(
                    start = dimensionResource(R.dimen.spacing_md),
                    end = dimensionResource(R.dimen.spacing_md),
                    top = dimensionResource(R.dimen.spacing_xl)
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // picture
            Image(
                painter = painterResource(id = R.drawable.orangewithbgandlogo),
                contentDescription = stringResource(R.string.profile_picture),
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacing_xl) * 3)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            // username
            Text(text = stringResource(R.string.test_user), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
            // email
            Text(
                text = stringResource(R.string.test_user_email),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            // edit and log out buttons
            Button(
                onClick = { /* TODO: handle edit */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) { Text(stringResource(R.string.edit_profile)) }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            OutlinedButton(
                onClick = { /* TODO: handle logout */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) { Text(stringResource(R.string.log_out)) }
        }
    }
}
