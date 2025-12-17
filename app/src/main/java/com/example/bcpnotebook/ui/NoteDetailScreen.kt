package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
                title = { if (isEditing) TextField(value = noteTitle, onValueChange = { noteTitle = it }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)) else Text(noteTitle) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            val updatedData = mapOf("title" to noteTitle, "cues" to noteCues, "notes" to noteContent, "summary" to noteSummary)
                            firestore.collection("users").document(userId).collection("notes").document(noteId!!)
                                .update(updatedData).addOnSuccessListener {
                                    isEditing = false
                                    Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                                }
                        } else { isEditing = true }
                    }) { Icon(if (isEditing) Icons.Default.Check else Icons.Default.Edit, null) }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFDFCF0)).padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Cues", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text(noteCues, fontSize = 16.sp)
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
                Text("Main Notes", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text(noteContent, fontSize = 18.sp)
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
                Text("Summary", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text(noteSummary, fontSize = 16.sp)
            }
        }
    }
}
