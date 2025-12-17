package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    var title by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val mainGradient = Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)))

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("NEW NOTE", style = TextStyle(letterSpacing = 3.sp, fontWeight = FontWeight.Black, color = Color.White)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White) } },
                actions = {
                    IconButton(
                        enabled = !isLoading,
                        onClick = {
                            if (title.isNotEmpty()) {
                                isLoading = true
                                val noteRef = firestore.collection("users").document(userId).collection("notes").document()
                                val newNote = Note(id = noteRef.id, userId = userId, title = title, cues = cues, notes = notes, summary = summary, timestamp = System.currentTimeMillis())
                                
                                noteRef.set(newNote)
                                    .addOnSuccessListener {
                                        isLoading = false // লোডিং বন্ধ হবে
                                        Toast.makeText(context, "Successfully Saved!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // সাথে সাথে ব্যাক করবে
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF4ECCA3), strokeWidth = 2.dp)
                        else Icon(Icons.Default.Done, contentDescription = null, tint = Color(0xFF4ECCA3))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(mainGradient).padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
                TextField(
                    value = title, onValueChange = { title = it },
                    placeholder = { Text("Topic Title...", fontSize = 28.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = Color(0xFF4ECCA3), focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    OutlinedTextField(value = cues, onValueChange = { cues = it }, label = { Text("Cues", color = Color(0xFF4ECCA3)) }, modifier = Modifier.weight(0.4f).fillMaxHeight(), shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4ECCA3), unfocusedBorderColor = Color.White.copy(0.2f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes", color = Color(0xFF4ECCA3)) }, modifier = Modifier.weight(0.6f).fillMaxHeight(), shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4ECCA3), unfocusedBorderColor = Color.White.copy(0.2f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Summary", color = Color(0xFF4ECCA3)) }, modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4ECCA3), unfocusedBorderColor = Color.White.copy(0.2f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
            }
        }
    }
}
