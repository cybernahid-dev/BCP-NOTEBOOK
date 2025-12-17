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
    var notes by remember { mutableStateOf("") }
    var cues by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Dynamic UI states
    var fontSize by remember { mutableStateOf(18.sp) }
    var selectedFont by remember { mutableStateOf(FontFamily.SansSerif) }

    val paperColor = Color(0xFFFCF5E5)
    val lineBlue = Color(0xFFD1E4EC)
    val marginRed = Color(0xFFFF9999)

    Box(modifier = Modifier.fillMaxSize().background(paperColor)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).drawBehind {
                val spacing = 32.dp.toPx()
                for (i in 0..(size.height / spacing).toInt()) {
                    val y = i * spacing + 140.dp.toPx()
                    drawLine(lineBlue, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                }
                val marginX = size.width * 0.30f
                drawLine(marginRed, Offset(marginX, 0f), Offset(marginX, size.height), 2.dp.toPx())
                drawLine(marginRed, Offset(marginX + 4.dp.toPx(), 0f), Offset(marginX + 4.dp.toPx(), size.height), 1.dp.toPx())
            }
        ) {
            TextField(
                value = title, onValueChange = { title = it },
                placeholder = { Text("Topic Title", fontSize = 32.sp, fontWeight = FontWeight.Black) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                textStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Black),
                colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )

            Row(modifier = Modifier.fillMaxWidth().heightIn(min = 1000.dp)) {
                Box(modifier = Modifier.weight(0.30f)) {
                    TextField(value = cues, onValueChange = { cues = it }, placeholder = { Text("CUES", fontWeight = FontWeight.Bold) }, colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
                }
                Box(modifier = Modifier.weight(0.70f)) {
                    TextField(
                        value = notes, onValueChange = { notes = it },
                        placeholder = { Text("Start writing deep notes...") },
                        textStyle = TextStyle(fontSize = fontSize, fontFamily = selectedFont, lineHeight = 32.sp),
                        colors = TextFieldDefaults.colors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
            }
        }

        // --- Glassmorphism Ultra Toolbar ---
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
                IconButton(onClick = { selectedFont = FontFamily.Serif }) { Icon(Icons.Default.TextFields, null, tint = Color.White) }
                IconButton(onClick = { fontSize = if(fontSize < 26.sp) fontSize + 2.sp else 16.sp }) { Icon(Icons.Default.FormatSize, null, tint = Color.White) }
                IconButton(onClick = {}) { Icon(Icons.Default.Palette, null, tint = Color.Cyan) }
                IconButton(onClick = {}) { Icon(Icons.Default.Mic, null, tint = Color.White) }
                
                VerticalDivider(modifier = Modifier.height(24.dp), color = Color.DarkGray)
                
                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            isLoading = true
                            val noteRef = firestore.collection("users").document(userId).collection("notes").document()
                            val newNote = Note(id = noteRef.id, userId = userId, title = title, cues = cues, notes = notes, summary = "", timestamp = System.currentTimeMillis())
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
                    if (isLoading) CircularProgressIndicator(size = 20.dp, color = Color.White)
                    else Text("SAVE", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(10.dp)) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.Black)
        }
    }
}
