package com.example.bcpnotebook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NotebookScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_note") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Your Notes", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
            // নোট লিস্ট রেন্ডারিং কোড এখানে থাকবে
        }
    }
}
