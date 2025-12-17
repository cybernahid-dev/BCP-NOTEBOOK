package com.example.bcpnotebook.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    val marginRed = Color(0xFFFF9999)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Cornell Note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showToast("Saving Note...") }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            OutlinedTextField(
                value = title, 
                onValueChange = { title = it }, 
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(400.dp).fillMaxWidth().border(1.dp, Color.LightGray)) {
                Box(modifier = Modifier.weight(0.35f).fillMaxHeight().background(Color(0xFFF9F9F9))) {
                    TextField(value = cues, onValueChange = { cues = it }, placeholder = { Text("Cues", fontSize = 12.sp) }, modifier = Modifier.fillMaxSize(), colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent))
                }
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(marginRed))
                Box(modifier = Modifier.weight(0.65f).fillMaxHeight()) {
                    TextField(value = notes, onValueChange = { notes = it }, placeholder = { Text("Main Notes", fontSize = 12.sp) }, modifier = Modifier.fillMaxSize(), colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent))
                }
            }
            HorizontalDivider(thickness = 2.dp, color = marginRed)
            Text("Summary", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(value = summary, onValueChange = { summary = it }, modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(12.dp))
        }
    }
}
