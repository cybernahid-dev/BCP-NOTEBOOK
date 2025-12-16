package com.example.bcpnotebook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
                title = { Text("BCP NOTEBOOK", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepSpace,
                    titleContentColor = NeonBlue
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Add logic */ }, containerColor = NeonBlue) {
                Text("+", fontSize = 24.sp, color = Color.Black)
            }
        },
        containerColor = DeepSpace
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Your Digital Cornell Workspace", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Box(Modifier.fillMaxSize().padding(20.dp)) {
                    Text(
                        "No notes here yet", 
                        color = NeonBlue.copy(alpha = 0.5f), 
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
