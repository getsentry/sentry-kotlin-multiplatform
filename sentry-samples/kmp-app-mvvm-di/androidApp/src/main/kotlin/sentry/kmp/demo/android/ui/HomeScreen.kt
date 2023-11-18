package sentry.kmp.demo.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import sentry.kmp.demo.models.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel) {
  var showDialog by remember { mutableStateOf(false) }
  var dialogMessage by remember { mutableStateOf("") }

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.Center) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp))
        Text(
            text = homeViewModel.homeText,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp))
        Button(
            onClick = {
              homeViewModel.updateProfileWithErr()
              dialogMessage = "An error occurred during profile update"
              showDialog = true
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
              Text("Update Profile (error)")
            }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
              Text("Log Out")
            }
        if (showDialog) {
          AlertDialog(
              onDismissRequest = { showDialog = false },
              title = { Text("Error") },
              text = { Text(dialogMessage) },
              confirmButton = { Button(onClick = { showDialog = false }) { Text("OK") } })
        }
      }
}
