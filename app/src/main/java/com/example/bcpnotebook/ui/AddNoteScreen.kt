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

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(), // কিবোর্ড ফিক্স
        bottomBar = {
            // Futuristic Glass Toolbar
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.Black.copy(alpha = 0.9f),
                tonalElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconButton(onClick = { selectedFont = if(selectedFont == FontFamily.SansSerif) FontFamily.Serif else FontFamily.SansSerif }) { 
                            Icon(Icons.Default.Menu, null, tint = Color.White) 
                        }
                        IconButton(onClick = { if(fontSizeVal < 30) fontSizeVal += 2 else fontSizeVal = 16 }) { 
                            Icon(Icons.Default.Add, null, tint = Color.White) 
                        }
                        IconButton(onClick = { }) { Icon(Icons.Default.Build, null, tint = Color.Cyan) }
                        IconButton(onClick = { }) { Icon(Icons.Default.Info, null, tint = Color.White) }
                    }
                    
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(paperColor).padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .drawBehind {
                        val spacing = 32.dp.toPx()
                        val headerHeight = 130.dp.toPx()
                        
                        // Horizontal Lines
                        for (i in 0..(size.height / spacing).toInt()) {
                            val y = i * spacing + headerHeight
                            drawLine(lineBlue, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                        }
                        
                        // Red Margin Line (Stops before summary)
                        val marginX = size.width * 0.28f
                        drawLine(marginRed, Offset(marginX, headerHeight), Offset(marginX, size.height - 220.dp.toPx()), 2.dp.toPx())
                    }
            ) {
                // Topic Title Section
                TextField(
                    value = title, onValueChange = { title = it },
                    placeholder = { Text("Topic Title", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 15.dp),
                    textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )

                // Cornell Body
                Row(modifier = Modifier.fillMaxWidth().heightIn(min = 600.dp)) {
                    // Cues Column
                    Box(modifier = Modifier.weight(0.28f).padding(start = 10.dp)) {
                        TextField(
                            value = cues, onValueChange = { cues = it },
                            placeholder = { Text("CUES", fontWeight = FontWeight.Bold, color = marginRed.copy(0.7f)) },
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                        )
                    }
                    // Main Notes Column
                    Box(modifier = Modifier.weight(0.72f)) {
                        TextField(
                            value = notes, onValueChange = { notes = it },
                            placeholder = { Text("Take detailed notes here...") },
                            textStyle = TextStyle(fontSize = fontSizeVal.sp, fontFamily = selectedFont, lineHeight = 32.sp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                        )
                    }
                }

                // Summary Section (Clean and Professional Placement)
                Divider(color = marginRed, thickness = 2.dp, modifier = Modifier.padding(horizontal = 10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.Black.copy(alpha = 0.02f))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("SUMMARY", fontSize = 14.sp, fontWeight = FontWeight.Black, color = marginRed)
                        TextField(
                            value = summary, onValueChange = { summary = it },
                            placeholder = { Text("Summarize key points here...", color = Color.Gray.copy(0.5f)) },
                            modifier = Modifier.fillMaxSize(),
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }

            // Back Button
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(10.dp)) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.Black)
            }
        }
    }
}
