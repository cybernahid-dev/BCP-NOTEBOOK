package com.example.bcpnotebook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("BCP NOTEBOOK", fontWeight = FontWeight.Bold, color = NeonBlue) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepSpace)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                // Plus button e click korle add_note screen e jabe
                onClick = { navController.navigate("add_note") },
                containerColor = NeonBlue
            ) {
                Text("+", fontSize = 24.sp, color = Color.Black)
            }
        },
        containerColor = DeepSpace
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Your Digital Cornell Workspace", color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))
            Box(Modifier.fillMaxSize()) {
                Text("No notes here yet", color = NeonBlue.copy(alpha = 0.5f), modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        }
    }
}
