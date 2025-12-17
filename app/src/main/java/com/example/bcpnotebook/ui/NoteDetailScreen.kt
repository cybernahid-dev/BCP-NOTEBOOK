package com.example.bcpnotebook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var note by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            firestore.collection("users").document(userId).collection("notes").document(noteId)
                .get().addOnSuccessListener { note = it.toObject(Note::class.java) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note?.title ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* এডিট ফিচার পরে যোগ করব */ }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        note?.let {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFCF0)), // Paper color
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Cues:", fontWeight = FontWeight.Bold, color = Color.Blue)
                        Text(it.cues, fontSize = 16.sp)
                        Divider(modifier = Modifier.padding(vertical = 10.dp))
                        
                        Text("Main Notes:", fontWeight = FontWeight.Bold, color = Color.Blue)
                        Text(it.notes, fontSize = 18.sp, lineHeight = 24.sp)
                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Text("Summary:", fontWeight = FontWeight.Bold, color = Color.Blue)
                        Text(it.summary, fontSize = 16.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }
        }
    }
}
