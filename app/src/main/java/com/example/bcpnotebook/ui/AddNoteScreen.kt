package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    
    // Formatting States
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var isHighlighter by remember { mutableStateOf(false) }
    var fontSizeValue by remember { mutableStateOf(18) }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cornell Note", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.Close, null) } },
                actions = {
                    TextButton(onClick = {
                        if (title.isNotEmpty()) {
                            isLoading = true
                            val noteRef = firestore.collection("users").document(userId).collection("notes").document()
                            val newNote = Note(id = noteRef.id, userId = userId, title = title, cues = cues, notes = notes, summary = summary, timestamp = System.currentTimeMillis())
                            noteRef.set(newNote).addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        else Text("SAVE", color = Color(0xFF007AFF), fontWeight = FontWeight.ExtraBold)
                    }
                }
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth().imePadding(), tonalElevation = 4.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp).horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Only using most basic Icons that are guaranteed to exist
                    ToolbarIcon(Icons.Default.List, "List")
                    ToolbarIcon(Icons.Default.Add, "Size Up") { if(fontSizeValue < 30) fontSizeValue += 2 }
                    ToolbarIcon(Icons.Default.KeyboardArrowUp, "Bold", isBold) { isBold = !isBold }
                    ToolbarIcon(Icons.Default.Info, "Italic", isItalic) { isItalic = !isItalic }
                    ToolbarIcon(Icons.Default.KeyboardArrowDown, "Underline", isUnderline) { isUnderline = !isUnderline }
                    ToolbarIcon(Icons.Default.Create, "Highlight", isHighlighter) { isHighlighter = !isHighlighter }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).drawBehind {
            val redLineX = size.width * 0.35f
            drawLine(Color.Red.copy(0.2f), Offset(redLineX, 0f), Offset(redLineX, size.height), 2.dp.toPx())
        }.padding(16.dp)) {
            TextField(value = title, onValueChange = { title = it }, placeholder = { Text("Topic Title") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold))
            Row(modifier = Modifier.fillMaxWidth().heightIn(min = 500.dp)) {
                Box(modifier = Modifier.weight(0.35f)) {
                    TextField(value = cues, onValueChange = { cues = it }, placeholder = { Text("Cues") }, colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                }
                Box(modifier = Modifier.weight(0.65f)) {
                    val fStyle: FontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal
                    TextField(
                        value = notes, onValueChange = { notes = it },
                        placeholder = { Text("Notes") },
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), 
                        textStyle = TextStyle(
                            fontSize = fontSizeValue.sp, 
                            fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal, 
                            fontStyle = fStyle, 
                            textDecoration = if(isUnderline) TextDecoration.Underline else TextDecoration.None, 
                            background = if(isHighlighter) Color.Yellow.copy(alpha = 0.5f) else Color.Transparent
                        )
                    )
                }
            }
            HorizontalDivider(color = Color.Red.copy(0.2f))
            Text("SUMMARY", modifier = Modifier.padding(top = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            TextField(value = summary, onValueChange = { summary = it }, modifier = Modifier.fillMaxWidth().height(150.dp), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
        }
    }
}

@Composable
fun ToolbarIcon(icon: ImageVector, label: String, active: Boolean = false, onClick: () -> Unit = {}) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp).background(if (active) Color(0xFFE3F2FD) else Color.Transparent, CircleShape)) {
        Icon(icon, contentDescription = label, tint = if (active) Color(0xFF007AFF) else Color.DarkGray)
    }
}
