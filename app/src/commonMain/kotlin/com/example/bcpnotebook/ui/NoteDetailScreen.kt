package com.example.bcpnotebook.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*

@Composable
fun NoteDetailScreen(navController: NavController, noteId: String?) {
    var title by remember { mutableStateOf("Loading...") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .drawBehind {
                    val marginX = size.width * 0.28f
                    drawLine(color = Color.Red.copy(0.3f), start = Offset(marginX, 0f), end = Offset(marginX, size.height), strokeWidth = 2.dp.toPx())
                }
        ) {
            Text("Cornell Note Layout Content Here", modifier = Modifier.padding(16.dp))
            // আপনার আগের Cornell UI লজিক এখানে হুবহু কাজ করবে
        }
    }
}
