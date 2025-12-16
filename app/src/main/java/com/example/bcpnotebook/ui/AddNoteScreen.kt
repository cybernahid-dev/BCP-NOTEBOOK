package com.example.bcpnotebook.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(DeepSpace).padding(16.dp)) {
        TextField(
            value = title, onValueChange = { title = it },
            placeholder = { Text("Title / Topic", color = GrayText) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, textColor = NeonBlue)
        )
        
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Cues Section (Left)
            OutlinedTextField(
                value = cues, onValueChange = { cues = it },
                label = { Text("Cues / Keywords", fontSize = 10.sp) },
                modifier = Modifier.weight(0.35f).fillMaxHeight(),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Main Notes Section (Right)
            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Main Notes / Details") },
                modifier = Modifier.weight(0.65f).fillMaxHeight(),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White)
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Summary Section (Bottom)
        OutlinedTextField(
            value = summary, onValueChange = { summary = it },
            label = { Text("Summary (Briefly review main points)") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White)
        )

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Cornell Note", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
