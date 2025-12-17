package com.example.bcpnotebook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(navController: NavController, noteId: String) {
    // এখানে আপনার নোটের ডেটা লোড করার লজিক থাকবে
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Note ID: $noteId", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            
            // এখানে নোটের টাইটেল, বডি এবং সামারি দেখানোর UI যোগ হবে
            Text(
                text = "নোটের বিস্তারিত এখানে দেখা যাবে", 
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
