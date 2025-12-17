package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    var notesList by remember { mutableStateOf<List<Note>>(emptyList()) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId).collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        notesList = snapshot.toObjects(Note::class.java)
                    }
                }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Notebook") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_note") }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        if (notesList.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No notes found. Create one!")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(notesList) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { selectedNote = note },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(text = note.summary, maxLines = 1, color = Color.Gray, fontSize = 14.sp)
                            }
                            IconButton(onClick = {
                                firestore.collection("users").document(userId).collection("notes").document(note.id).delete()
                                    .addOnSuccessListener { Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show() }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedNote != null) {
        AlertDialog(
            onDismissRequest = { selectedNote = null },
            title = { Text(selectedNote!!.title, fontWeight = FontWeight.Bold) },
            text = {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text("Cues:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(selectedNote!!.cues)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Main Notes:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(selectedNote!!.notes)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Summary:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(selectedNote!!.summary)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedNote = null }) { Text("Close") }
            }
        )
    }
}
