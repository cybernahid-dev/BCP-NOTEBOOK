package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
    
    // States for holding note data
    var noteTitle by remember { mutableStateOf("") }
    var noteCues by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) } // For rich text editing
    var noteSummary by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Toolbar state
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderlined by remember { mutableStateOf(false) }
    var textAlign by remember { mutableStateOf(TextAlign.Start) }
    var isFontMenuExpanded by remember { mutableStateOf(false) }
    var isColorMenuExpanded by remember { mutableStateOf(false) }
    var selectedFontFamily by remember { mutableStateOf(FontFamily.Default) }

    val fontFamilies = listOf(FontFamily.Default, FontFamily.Serif, FontFamily.Cursive)
    val textColors = listOf(Color.Black, Color.Red, Color.Blue, Color.Green)

    // Fetch note data from Firestore
    LaunchedEffect(noteId) {
        if (noteId != null) {
            firestore.collection("users").document(userId).collection("notes").document(noteId)
                .get().addOnSuccessListener { doc ->
                    val note = doc.toObject(Note::class.java)
                    if (note != null) {
                        noteTitle = note.title
                        noteCues = note.cues
                        noteContent = TextFieldValue(note.notes) // Load plain text for now
                        noteSummary = note.summary
                    }
                    isLoading = false
                }.addOnFailureListener {
                    isLoading = false
                    Toast.makeText(context, "Failed to load note.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to permanently delete this note?") },
            confirmButton = {
                Button(
                    onClick = {
                        firestore.collection("users").document(userId).collection("notes").document(noteId!!).delete()
                            .addOnSuccessListener {
                                showDeleteDialog = false
                                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
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
        containerColor = Color(0xFFFCF5E5),
        topBar = {
            TopAppBar(
                title = { Text("Edit Note", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            // Futuristic Bottom Action Bar with Toolbar
             Surface(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.Black.copy(alpha = 0.9f),
                tonalElevation = 10.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Main actions: Save and Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.DeleteOutline, null, tint = Color.Red, modifier = Modifier.padding(end = 8.dp))
                            Text("Delete Note", color = Color.Red)
                        }
                        
                        Button(
                            onClick = {
                                val updatedData = mapOf(
                                    "title" to noteTitle,
                                    "cues" to noteCues,
                                    "notes" to noteContent.text, // Saving plain text
                                    "summary" to noteSummary
                                )
                                firestore.collection("users").document(userId).collection("notes").document(noteId!!)
                                    .update(updatedData).addOnSuccessListener {
                                        Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.padding(end = 8.dp))
                            Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
                    
                    // Rich text editing tools
                     Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Style Toggles
                        IconButton(onClick = { isBold = !isBold; noteContent = applyStyleToSelection(noteContent, androidx.compose.ui.text.SpanStyle(fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)) }, modifier = Modifier.background(if(isBold) Color.Gray else Color.Transparent, CircleShape)) { Icon(Icons.Default.FormatBold, null, tint = Color.White) }
                        IconButton(onClick = { isItalic = !isItalic; noteContent = applyStyleToSelection(noteContent, androidx.compose.ui.text.SpanStyle(fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal)) }, modifier = Modifier.background(if(isItalic) Color.Gray else Color.Transparent, CircleShape)) { Icon(Icons.Default.FormatItalic, null, tint = Color.White) }
                        IconButton(onClick = { isUnderlined = !isUnderlined; noteContent = applyStyleToSelection(noteContent, androidx.compose.ui.text.SpanStyle(textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None)) }, modifier = Modifier.background(if(isUnderlined) Color.Gray else Color.Transparent, CircleShape)) { Icon(Icons.Default.FormatUnderlined, null, tint = Color.White) }

                        // Color Picker
                        Box {
                             IconButton(onClick = { isColorMenuExpanded = true }) { Icon(Icons.Default.FormatColorText, null, tint = Color.White) }
                             DropdownMenu(expanded = isColorMenuExpanded, onDismissRequest = { isColorMenuExpanded = false }) {
                                 Row(modifier = Modifier.padding(8.dp)) {
                                     textColors.forEach { color -> Box(modifier = Modifier.size(32.dp).padding(4.dp).clip(CircleShape).background(color).clickable { noteContent = applyStyleToSelection(noteContent, androidx.compose.ui.text.SpanStyle(color = color)); isColorMenuExpanded = false }) }
                                 }
                             }
                        }

                        // Alignment Toggles
                        IconButton(onClick = { textAlign = TextAlign.Start }) { Icon(Icons.Default.FormatAlignLeft, null, tint = if (textAlign == TextAlign.Start) Color.Cyan else Color.White) }
                        IconButton(onClick = { textAlign = TextAlign.Center }) { Icon(Icons.Default.FormatAlignCenter, null, tint = if (textAlign == TextAlign.Center) Color.Cyan else Color.White) }
                        IconButton(onClick = { textAlign = TextAlign.End }) { Icon(Icons.Default.FormatAlignRight, null, tint = if (textAlign == TextAlign.End) Color.Cyan else Color.White) }
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
                    .verticalScroll(rememberScrollState())
                    .drawBehind {
                        // Re-drawing the Cornell note lines for consistency
                        val lineBlue = Color(0xFFD1E4EC)
                        val marginRed = Color(0xFFFF9999)
                        val spacing = 32.dp.toPx()
                        val headerOffset = 120.dp.toPx()

                        for (i in 0..(size.height / spacing).toInt()) {
                            val y = i * spacing + headerOffset
                            drawLine(lineBlue, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                        }
                    }
            ) {
                // Title Field
                TextField(
                    value = noteTitle, onValueChange = { noteTitle = it },
                    placeholder = { Text("Topic Title", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black.copy(0.4f)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    textStyle = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.ExtraBold),
                    colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )

                // Main Body: Cues and Notes
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 600.dp)
                        .drawBehind {
                             val marginRed = Color(0xFFFF9999)
                             val marginX = size.width * 0.28f
                             drawLine(color = marginRed, start = Offset(marginX, 0f), end = Offset(marginX, size.height), strokeWidth = 2.dp.toPx())
                         }
                ) {
                    // Cues Area
                    Box(modifier = Modifier.weight(0.28f).padding(start = 12.dp)) {
                        TextField(value = noteCues, onValueChange = { noteCues = it }, placeholder = { Text("CUES", fontWeight = FontWeight.Bold, color = Color(0xFFFF9999).copy(0.8f), fontSize = 14.sp) }, colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                    }
                    // Notes Area
                    Box(modifier = Modifier.weight(0.72f).padding(start = 8.dp)) {
                        TextField(value = noteContent, onValueChange = { noteContent = it }, placeholder = { Text("Take detailed notes here...") }, textStyle = TextStyle(fontSize = 18.sp, fontFamily = selectedFontFamily, lineHeight = 32.sp, textAlign = textAlign), modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                    }
                }

                Divider(color = Color(0xFFFF9999), thickness = 2.dp)

                // Summary Section
                Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Column {
                        Text("SUMMARY", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9999))
                        TextField(value = noteSummary, onValueChange = { noteSummary = it }, placeholder = { Text("Summarize the key points here...", color = Color.Gray.copy(0.5f)) }, modifier = Modifier.fillMaxSize(), colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                    }
                }
            }
        }
    }
}