package com.example.bcpnotebook.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    var notesList by remember { mutableStateOf<List<Note>>(emptyList()) }
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            firestore.collection("users").document(uid).collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        notesList = snapshot.toObjects(Note::class.java)
                    }
                }
        }
    }

    val filteredNotes = if (searchQuery.isEmpty()) {
        notesList
    } else {
        notesList.filter { it.title.contains(searchQuery, ignoreCase = true) || it.notes.contains(searchQuery, ignoreCase = true) }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to permanently delete '${noteToDelete!!.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        firestore.collection("users").document(user!!.uid).collection("notes").document(noteToDelete!!.id).delete()
                        showDeleteDialog = false
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF4F7FA),
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { navController.navigate("add_note") },
                containerColor = Color(0xFF007AFF),
                shape = CircleShape
            ) { Icon(Icons.Default.Add, "Add Note", tint = Color.White, modifier = Modifier.size(32.dp)) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Upgraded Futuristic Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE3F2FD), Color(0xFFF4F7FA)),
                            endY = 250f
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("My Notebook", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A1A1A))
                            Text(user?.email ?: "Personal Workspace", fontSize = 14.sp, color = Color.Gray)
                        }
                        
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(50.dp).background(Color.White, CircleShape).clip(CircleShape)
                            ) {
                                // Future: Replace with user's profile picture
                                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF007AFF))
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Profile") }, // Future: Navigate to profile customization screen
                                    leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                                    onClick = { /* navController.navigate("profile_screen") */ }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout", fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) },
                                    onClick = {
                                        auth.signOut()
                                        navController.navigate("login") { popUpTo(0) }
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Search Bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search notes...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            AnimatedVisibility(visible = filteredNotes.isEmpty() && searchQuery.isBlank(), enter = fadeIn(), exit = fadeOut()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
                    Text("Your notebook is empty.\nTap '+' to add a new note!", color = Color.Gray, modifier = Modifier.padding(32.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredNotes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("note_detail/${note.id}") },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 2)
                            Spacer(Modifier.height(8.dp))
                            Text(note.notes, fontSize = 14.sp, color = Color.Gray, maxLines = 5)
                            Spacer(Modifier.height(12.dp))
                            IconButton(
                                onClick = {
                                    noteToDelete = note
                                    showDeleteDialog = true
                                },
                                modifier = Modifier.align(Alignment.End).size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Note", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}