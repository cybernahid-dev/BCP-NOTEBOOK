package com.example.bcpnotebook.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cornell Note") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title / Topic") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.height(300.dp)) {
                OutlinedTextField(
                    value = clues, onValueChange = { clues = it },
                    label = { Text("Clues") },
                    modifier = Modifier.weight(0.4f).fillMaxHeight()
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Main Notes") },
                    modifier = Modifier.weight(0.6f).fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = summary, onValueChange = { summary = it },
                label = { Text("Summary") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.popBackStack() }, // এটি এখন ঠিকমতো ব্যাক করবে
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Save Cornell Note")
            }
        }
    }
}
