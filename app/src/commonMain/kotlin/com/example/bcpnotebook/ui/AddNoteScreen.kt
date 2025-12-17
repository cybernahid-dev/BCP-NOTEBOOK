package com.example.bcpnotebook.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        showToast("Saving...") 
                        generateNotePdf(title, notes, summary)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            TextField(
                value = title, 
                onValueChange = { title = it }, 
                placeholder = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
            )
            
            // Cornell Layout
            Row(modifier = Modifier.height(500.dp)) {
                Box(modifier = Modifier.weight(0.3f).fillMaxHeight().background(Color.White)) {
                    TextField(value = cues, onValueChange = { cues = it }, placeholder = { Text("CUES") })
                }
                Box(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    TextField(value = notes, onValueChange = { notes = it }, placeholder = { Text("NOTES") })
                }
            }
            
            Divider(color = marginRed)
            TextField(value = summary, onValueChange = { summary = it }, placeholder = { Text("SUMMARY") }, modifier = Modifier.fillMaxWidth())
        }
    }
}
