package com.example.bcpnotebook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(navController: NavController) {
    // থিম স্টেজ ম্যানেজমেন্ট (আপাতত লোকাল, পরে গ্লোবাল করা যাবে)
    var isDarkTheme by remember { mutableStateOf(true) }

    BCPNotebookTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("BCP NOTEBOOK", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                                contentDescription = "Toggle Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("add_note") },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("+", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text(
                    "Your Cornell Workspace", 
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                // নোটের লিস্ট এখানে আসবে
            }
        }
    }
}
