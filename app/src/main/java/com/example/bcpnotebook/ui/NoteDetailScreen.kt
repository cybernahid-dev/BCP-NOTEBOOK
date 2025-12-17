package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
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
    
    // Formatting States
    var isBold by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.Black) }

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
                title = { 
                    if (isEditing) {
                        TextField(
                            value = noteTitle, 
                            onValueChange = { noteTitle = it },
                            placeholder = { Text("Title") },
                            colors = TextFieldDefaults.colors(containerColor = Color.Transparent)
                        )
                    } else {
                        Text(noteTitle, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            val updatedData = mapOf(
                                "title" to noteTitle,
                                "cues" to noteCues,
                                "notes" to noteContent,
                                "summary" to noteSummary,
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("users").document(userId).collection("notes").document(noteId!!)
                                .update(updatedData).addOnSuccessListener {
                                    isEditing = false
                                    Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show()
                                }
                        }) { Icon(Icons.Default.Check, null, tint = Color(0xFF4ECCA3)) }
                    } else {
                        IconButton(onClick = { isEditing = true }) { Icon(Icons.Default.Edit, null) }
                    }
                }
            )
        },
        bottomBar = {
            if (isEditing) {
                // Xiaomi Style Formatting Toolbar
                Surface(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterHorizontally,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { isBold = !isBold }) {
                            Icon(Icons.Default.FormatBold, null, tint = if (isBold) Color.Blue else Color.Gray)
                        }
                        IconButton(onClick = { selectedColor = Color.Red }) {
                            Box(Modifier.size(24.dp).background(Color.Red, CircleShape))
                        }
                        IconButton(onClick = { selectedColor = Color(0xFF4ECCA3) }) {
                            Box(Modifier.size(24.dp).background(Color(0xFF4ECCA3), CircleShape))
                        }
                        IconButton(onClick = { selectedColor = Color.Black }) {
                            Box(Modifier.size(24.dp).background(Color.Black, CircleShape))
                        }
                        IconButton(onClick = { /* Future: Image Add */ }) {
                            Icon(Icons.Default.Image, null)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFFDFCF0)) // Realistic Paper Color
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isEditing) {
                    Text("Cues Section", fontWeight = FontWeight.Bold, color = Color.Gray)
                    OutlinedTextField(
                        value = noteCues, 
                        onValueChange = { noteCues = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    
                    Text("Main Notes", fontWeight = FontWeight.Bold, color = Color.Gray)
                    OutlinedTextField(
                        value = noteContent, 
                        onValueChange = { noteContent = it },
                        modifier = Modifier.fillMaxWidth().height(400.dp),
                        textStyle = TextStyle(
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            color = selectedColor,
                            fontSize = 18.sp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    Text("Summary Section", fontWeight = FontWeight.Bold, color = Color.Gray)
                    OutlinedTextField(
                        value = noteSummary, 
                        onValueChange = { noteSummary = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    // View Mode
                    Text("CUES", color = Color.Blue, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Text(noteCues, fontSize = 16.sp)
                    HorizontalDivider(Modifier.padding(vertical = 15.dp), color = Color.LightGray)
                    
                    Text("NOTES", color = Color.Blue, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Text(noteContent, fontSize = 18.sp, lineHeight = 26.sp)
                    HorizontalDivider(Modifier.padding(vertical = 15.dp), color = Color.LightGray)
                    
                    Text("SUMMARY", color = Color.Blue, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Text(noteSummary, fontSize = 16.sp, fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}
