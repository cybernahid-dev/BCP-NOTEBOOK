package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(navController: NavController, noteId: String?) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    var isEditing by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteCues by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var noteSummary by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            firestore.collection("users").document(userId).collection("notes").document(noteId)
                .get().addOnSuccessListener { doc ->
                    val n = doc.toObject(Note::class.java)
                    if (n != null) {
                        noteTitle = n.title
                        noteCues = n.cues
                        noteContent = n.notes
                        noteSummary = n.summary
                    }
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { if (isEditing) OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it }, singleLine = true) else Text(noteTitle) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            // আপডেট লজিক
                            val updatedData = mapOf("title" to noteTitle, "cues" to noteCues, "notes" to noteContent, "summary" to noteSummary)
                            firestore.collection("users").document(userId).collection("notes").document(noteId!!)
                                .update(updatedData).addOnSuccessListener {
                                    isEditing = false
                                    Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            isEditing = true
                        }
                    }) {
                        Icon(if (isEditing) Icons.Default.Check else Icons.Default.Edit, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFDFCF0)).padding(16.dp).verticalScroll(rememberScrollState())) {
                if (isEditing) {
                    Text("Cues:", fontWeight = FontWeight.Bold, color = Color.Blue)
                    OutlinedTextField(value = noteCues, onValueChange = { noteCues = it }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Main Notes:", fontWeight = FontWeight.Bold, color = Color.Blue)
                    OutlinedTextField(value = noteContent, onValueChange = { noteContent = it }, modifier = Modifier.fillMaxWidth().height(300.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Summary:", fontWeight = FontWeight.Bold, color = Color.Blue)
                    OutlinedTextField(value = noteSummary, onValueChange = { noteSummary = it }, modifier = Modifier.fillMaxWidth())
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Cues", color = Color.Blue, fontWeight = FontWeight.Bold)
                            Text(noteCues, fontSize = 16.sp)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                            Text("Main Notes", color = Color.Blue, fontWeight = FontWeight.Bold)
                            Text(noteContent, fontSize = 18.sp)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                            Text("Summary", color = Color.Blue, fontWeight = FontWeight.Bold)
                            Text(noteSummary, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
