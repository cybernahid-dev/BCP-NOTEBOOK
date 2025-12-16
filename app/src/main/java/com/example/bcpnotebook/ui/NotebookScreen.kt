@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.bcpnotebook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bcpnotebook.model.NoteModel
import com.example.bcpnotebook.viewmodel.NoteViewModel

@Composable
fun NotebookScreen(navController: NavController, viewModel: NoteViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    
    // Notes লিস্ট লোড করা
    val noteList by viewModel.notes.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadNotes() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cornell Notebook") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(8.dp)) {
            
            // --- Input Section (Cornell Layout) ---
            Text("Create New Note", style = MaterialTheme.typography.titleLarge)
            
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title / Topic") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            
            // Main Cornell Layout: Divided into Cues and Notes
            Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                // Left Column: Cues (30% width)
                OutlinedTextField(
                    value = cues, onValueChange = { cues = it },
                    label = { Text("Cues/Keywords") },
                    modifier = Modifier.weight(0.3f).fillMaxHeight()
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Right Column: Notes (70% width)
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Main Notes / Details") },
                    modifier = Modifier.weight(0.7f).fillMaxHeight()
                )
            }
            
            // Bottom Section: Summary
            OutlinedTextField(
                value = summary, onValueChange = { summary = it },
                label = { Text("Summary (Briefly review main points)") },
                modifier = Modifier.fillMaxWidth().height(80.dp).padding(vertical = 8.dp)
            )

            Button(
                onClick = { 
                    if (title.isNotBlank() && notes.isNotBlank()) {
                        viewModel.saveNote(NoteModel(title = title, cues = cues, notes = notes, summary = summary))
                        // Clear fields
                        title = ""; cues = ""; notes = ""; summary = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Save Cornell Note")
            }

            // --- List Section ---
            Text("Saved Notes", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(bottom = 8.dp))
            
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(noteList) { note ->
                    CornellNoteCard(note)
                }
            }
        }
    }
}

@Composable
fun CornellNoteCard(note: NoteModel) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0FF)) // Light Purple background
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.headlineSmall)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
            
            // Row for Cues and Notes
            Row(modifier = Modifier.heightIn(min = 80.dp)) {
                // Cues (Left side)
                Column(modifier = Modifier.weight(0.3f).border(1.dp, Color(0xFFCCCCCC)).padding(4.dp)) {
                    Text("Cues:", style = MaterialTheme.typography.labelSmall)
                    Text(text = note.cues, style = MaterialTheme.typography.bodyMedium)
                }
                
                // Notes (Right side)
                Column(modifier = Modifier.weight(0.7f).border(1.dp, Color(0xFFCCCCCC)).padding(4.dp)) {
                    Text("Notes:", style = MaterialTheme.typography.labelSmall)
                    Text(text = note.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            // Summary (Bottom)
            Text(
                text = "Summary: ${note.summary}", 
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xFFDDDDFF)) // Lighter background for summary
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
