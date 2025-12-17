package com.example.bcpnotebook.ui

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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

    var fontSizeVal by remember { mutableStateOf(18) }
    var selectedFont by remember { mutableStateOf(FontFamily.SansSerif) }

    val paperColor = Color(0xFFFCF5E5)
    val lineBlue = Color(0xFFD1E4EC)
    val marginRed = Color(0xFFFF9999)

    Box(modifier = Modifier.fillMaxSize().background(paperColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .drawBehind {
                    val spacing = 32.dp.toPx()
                    val headerHeight = 160.dp.toPx()
                    
                    // Horizontal Ruling Lines
                    for (i in 0..(size.height / spacing).toInt()) {
                        val y = i * spacing + headerHeight
                        drawLine(lineBlue, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    }
                    
                    // Vertical Cornell Margin Line
                    val marginX = size.width * 0.30f
                    drawLine(marginRed, Offset(marginX, headerHeight), Offset(marginX, size.height - 180.dp.toPx()), 2.dp.toPx())
                    
                    // Summary Section Horizontal Line
                    val summaryTopY = size.height - 180.dp.toPx()
                    drawLine(marginRed, Offset(0f, summaryTopY), Offset(size.width, summaryTopY), 2.dp.toPx())
                }
        ) {
            // 1. Header: Topic Title (Now adjusted to stay outside margins)
            TextField(
                value = title, onValueChange = { title = it },
                placeholder = { Text("Topic Title", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Gray.copy(0.6f)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp),
                textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Black),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )

            // 2. Cornell Main Body (Cues and Notes)
            Row(modifier = Modifier.fillMaxWidth().heightIn(min = 600.dp)) {
                // Left side: Cues
                Box(modifier = Modifier.weight(0.30f).padding(start = 10.dp)) {
                    TextField(
                        value = cues, onValueChange = { cues = it },
                        placeholder = { Text("CUES / Qs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = marginRed) },
                        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
                // Right side: Main Notes
                Box(modifier = Modifier.weight(0.70f)) {
                    TextField(
                        value = notes, onValueChange = { notes = it },
                        placeholder = { Text("Take detailed notes here...", color = Color.Gray.copy(0.4f)) },
                        textStyle = TextStyle(fontSize = fontSizeVal.sp, fontFamily = selectedFont, lineHeight = 32.sp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
            }

            // 3. Summary Section (Added New)
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp).height(250.dp)) {
                Text("SUMMARY", modifier = Modifier.padding(start = 20.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                TextField(
                    value = summary, onValueChange = { summary = it },
                    placeholder = { Text("Summarize the key points here...", color = Color.Gray.copy(0.4f)) },
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )
            }
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for floating toolbar
        }

        // --- Futuristic Glass Toolbar ---
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp).navigationBarsPadding().imePadding(),
            shape = RoundedCornerShape(28.dp),
            color = Color.Black.copy(alpha = 0.85f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp).horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                IconButton(onClick = { selectedFont = if(selectedFont == FontFamily.SansSerif) FontFamily.Serif else FontFamily.SansSerif }) { 
                    Icon(Icons.Default.Menu, null, tint = Color.White) 
                }
                IconButton(onClick = { if(fontSizeVal < 30) fontSizeVal += 2 else fontSizeVal = 16 }) { 
                    Icon(Icons.Default.Add, null, tint = Color.White) 
                }
                IconButton(onClick = { }) { Icon(Icons.Default.Build, null, tint = Color.Cyan) }
                IconButton(onClick = { }) { Icon(Icons.Default.Info, null, tint = Color.White) }
                
                VerticalDivider(modifier = Modifier.height(24.dp), color = Color.DarkGray)
                
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    shape = CircleShape
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text("SAVE", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(10.dp)) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.Black)
        }
    }
}
