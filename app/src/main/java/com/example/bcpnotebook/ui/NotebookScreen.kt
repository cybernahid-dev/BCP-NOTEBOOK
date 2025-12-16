package com.example.bcpnotebook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bcpnotebook.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var showProfileDialog by remember { mutableStateOf(false) }

    // Profile Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Account Info", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    if (user?.photoUrl != null) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.size(60.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(60.dp), tint = SoftBlue)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = user?.displayName ?: "User Name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = user?.email ?: "No Email", color = TextSecondary, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        // লগআউট করে লগইন পেজে নিয়ে যাবে
                        navController.navigate("login") { popUpTo("notebook") { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Close") }
            },
            containerColor = SurfaceWhite
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BCP Notebook", color = SoftBlue, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showProfileDialog = true }) {
                        if (user?.photoUrl != null) {
                            AsyncImage(model = user.photoUrl, contentDescription = "Profile", modifier = Modifier.size(30.dp).clip(CircleShape))
                        } else {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = SoftBlue)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_note") },
                containerColor = SoftBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Your Cornell Notes will appear here", color = TextSecondary)
        }
    }
}
