package com.example.bcpnotebook.ui

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

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            firestore.collection("users").document(uid).collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) notesList = snapshot.toObjects(Note::class.java)
                }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF4F7FA),
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { navController.navigate("add_note") },
                containerColor = Color(0xFF007AFF),
                shape = CircleShape
            ) { Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(32.dp)) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // High-End Header with Profile & Logout
            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
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
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF007AFF))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
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

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(notesList) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("note_detail/${note.id}") },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1)
                            Spacer(Modifier.height(8.dp))
                            Text(note.notes, fontSize = 14.sp, color = Color.Gray, maxLines = 5)
                        }
                    }
                }
            }
        }
    }
}
