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

    val gradient = Brush.verticalScroll(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CREATE NOTE", style = TextStyle(letterSpacing = 2.sp, fontWeight = FontWeight.ExtraBold)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (title.isNotEmpty()) {
                            isLoading = true
                            val noteRef = firestore.collection("users").document(userId).collection("notes").document()
                            val newNote = Note(id = noteRef.id, userId = userId, title = title, cues = cues, notes = notes, summary = summary)
                            noteRef.set(newNote).addOnSuccessListener {
                                Toast.makeText(context, "Saved to Cloud", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }) {
                        if (isLoading) CircularProgressIndicator(size = 24.dp)
                        else Icon(Icons.Default.Done, contentDescription = null, tint = Color(0xFF4ECCA3))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(gradient).padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
                TextField(
                    value = title, onValueChange = { title = it },
                    placeholder = { Text("Title of Topic", fontSize = 24.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    textStyle = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )

                Divider(color = Color.Gray.copy(0.3f), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                Row(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    // Cues Section (Left)
                    OutlinedTextField(
                        value = cues, onValueChange = { cues = it },
                        label = { Text("CUES") },
                        modifier = Modifier.weight(0.4f).fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // Notes Section (Right)
                    OutlinedTextField(
                        value = notes, onValueChange = { notes = it },
                        label = { Text("MAIN NOTES") },
                        modifier = Modifier.weight(0.6f).fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = summary, onValueChange = { summary = it },
                    label = { Text("SUMMARY") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
