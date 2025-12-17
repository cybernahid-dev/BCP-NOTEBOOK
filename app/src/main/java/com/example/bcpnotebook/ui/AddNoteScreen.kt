package com.example.bcpnotebook.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignCenter
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.bcpnotebook.R
import com.example.bcpnotebook.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.pdf.PdfDocument as AndroidPdfDocument

// Helper functions remain the same...
private fun toggleSpanStyle(textFieldValue: TextFieldValue, style: SpanStyle): TextFieldValue {
    val selection = textFieldValue.selection
    if (selection.collapsed) return textFieldValue
    val existingStyles = textFieldValue.annotatedString.getSpanStyles(selection.start, selection.end)
    val hasStyle = existingStyles.any {
        (style.fontWeight != null && it.item.fontWeight == style.fontWeight) ||
        (style.fontStyle != null && it.item.fontStyle == style.fontStyle) ||
        (style.textDecoration != null && it.item.textDecoration == style.textDecoration)
    }
    val newString = buildAnnotatedString {
        append(textFieldValue.annotatedString)
        if (hasStyle) {
            val newStyle = SpanStyle(fontWeight = if (style.fontWeight != null) FontWeight.Normal else null, fontStyle = if (style.fontStyle != null) FontStyle.Normal else null, textDecoration = if (style.textDecoration != null) TextDecoration.None else null)
            addStyle(newStyle, selection.start, selection.end)
        } else { addStyle(style, selection.start, selection.end) }
    }
    return textFieldValue.copy(annotatedString = newString)
}

private fun createPdfFromContent(title: String, content: AnnotatedString, context: Context) {
    if (title.isBlank()) { Toast.makeText(context, "Please enter a title.", Toast.LENGTH_SHORT).show(); return }
    val document = AndroidPdfDocument()
    val pageInfo = AndroidPdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val textPaint = TextPaint()
    val leftMargin = 72f; val rightMargin = 595 - 72f
    val pageW = (rightMargin - leftMargin).toInt()
    var yPosition = 72f
    textPaint.textSize = 24f; textPaint.isFakeBoldText = true
    canvas.drawText(title, leftMargin, yPosition, textPaint); yPosition += 40
    val staticLayout = StaticLayout.Builder.obtain(content, 0, content.length, textPaint, pageW).build()
    canvas.save(); canvas.translate(leftMargin, yPosition); staticLayout.draw(canvas); canvas.restore()
    document.finishPage(page)
    val fileName = "${title.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
    val contentValues = ContentValues().apply { put(MediaStore.MediaColumns.DISPLAY_NAME, fileName); put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf"); put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
    if (uri != null) {
        try { resolver.openOutputStream(uri).use { document.writeTo(it) }; showDownloadNotification(context, title, uri)
        } catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show() }
    }
    document.close()
}

private fun showDownloadNotification(context: Context, title: String, uri: android.net.Uri) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "pdf_download_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "PDF Downloads", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }
    val intent = Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, "application/pdf"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("PDF Saved Successfully")
        .setContentText("'$title.pdf' saved to Downloads.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
    notificationManager.notify(1, notification)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    val context = LocalContext.current
    // =================== নতুন কোড শুরু (পারমিশন চাওয়ার জন্য) ===================
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Notification permission denied. You won't get download alerts.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // স্ক্রিনটি লোড হওয়ার সাথে সাথে পারমিশন চাইবে
    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // =================== নতুন কোড শেষ ===================

    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var cues by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var textAlign by remember { mutableStateOf(TextAlign.Start) }
    var isFontMenuExpanded by remember { mutableStateOf(false) }
    var isColorMenuExpanded by remember { mutableStateOf(false) }
    var selectedFontInfo by remember { mutableStateOf(appFonts.first()) }
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val paperColor = Color(0xFFFCF5E5)
    val lineBlue = Color(0xFFD1E4EC)
    val marginRed = Color(0xFFFF9999)
    val textColors = listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color(0xFF9C27B0))

    Scaffold(modifier = Modifier.fillMaxSize().imePadding(), bottomBar = {
        Surface(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(28.dp), color = Color.Black.copy(alpha = 0.9f), tonalElevation = 10.dp) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Box {
                        Button(onClick = { isFontMenuExpanded = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                            Text(selectedFontInfo.name, color = Color.White)
                            Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                        }
                        DropdownMenu(expanded = isFontMenuExpanded, onDismissRequest = { isFontMenuExpanded = false }) {
                            appFonts.forEach { fontInfo -> DropdownMenuItem(text = { Text(fontInfo.name, fontFamily = fontInfo.fontFamily) }, onClick = { selectedFontInfo = fontInfo; isFontMenuExpanded = false; notes = applyStyleToSelection(notes, SpanStyle(fontFamily = fontInfo.fontFamily)) }) }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { scale = 1f; offsetX = 0f; offsetY = 0f }) { Icon(Icons.Default.ZoomOutMap, "Reset Zoom", tint = Color.White) }
                        IconButton(onClick = { createPdfFromContent(title, notes.annotatedString, context) }) { Icon(Icons.Default.Download, "Export PDF", tint = Color.White) }
                        Button(onClick = {
                            if (title.isNotEmpty()) {
                                isLoading = true
                                val noteRef = firestore.collection("users").document(userId).collection("notes").document()
                                val newNote = Note(id = noteRef.id, userId = userId, title = title, cues = cues, notes = notes.text, summary = summary, timestamp = System.currentTimeMillis())
                                noteRef.set(newNote).addOnSuccessListener { isLoading = false; Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show(); navController.popBackStack() }
                                    .addOnFailureListener { e -> isLoading = false; Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show() }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)), shape = CircleShape) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White) else Text("SAVE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(onClick = { notes = toggleSpanStyle(notes, SpanStyle(fontWeight = FontWeight.Bold)) }) { Icon(Icons.Default.FormatBold, null, tint = Color.White) }
                    IconButton(onClick = { notes = toggleSpanStyle(notes, SpanStyle(fontStyle = FontStyle.Italic)) }) { Icon(Icons.Default.FormatItalic, null, tint = Color.White) }
                    IconButton(onClick = { notes = toggleSpanStyle(notes, SpanStyle(textDecoration = TextDecoration.Underline)) }) { Icon(Icons.Default.FormatUnderlined, null, tint = Color.White) }
                    Box {
                        IconButton(onClick = { isColorMenuExpanded = true }) { Icon(Icons.Default.FormatColorText, null, tint = Color.White) }
                        DropdownMenu(expanded = isColorMenuExpanded, onDismissRequest = { isColorMenuExpanded = false }) {
                            Row(modifier = Modifier.padding(8.dp)) { textColors.forEach { color -> Box(modifier = Modifier.size(32.dp).padding(4.dp).clip(CircleShape).background(color).clickable { notes = applyStyleToSelection(notes, SpanStyle(color = color)); isColorMenuExpanded = false }) } }
                        }
                    }
                    IconButton(onClick = { textAlign = TextAlign.Start }) { Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, null, tint = if (textAlign == TextAlign.Start) Color.Cyan else Color.White) }
                    IconButton(onClick = { textAlign = TextAlign.Center }) { Icon(Icons.AutoMirrored.Filled.FormatAlignCenter, null, tint = if (textAlign == TextAlign.Center) Color.Cyan else Color.White) }
                    IconButton(onClick = { textAlign = TextAlign.End }) { Icon(Icons.AutoMirrored.Filled.FormatAlignRight, null, tint = if (textAlign == TextAlign.End) Color.Cyan else Color.White) }
                }
            }
        }
    }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(paperColor).padding(innerPadding).pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale = (scale * zoom).coerceIn(1f, 3f)
                if (scale > 1f) {
                    val maxOffsetX = (size.width * (scale - 1)) / 2; val maxOffsetY = (size.height * (scale - 1)) / 2
                    offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX); offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                } else { offsetX = 0f; offsetY = 0f }
            }
        }) {
            Column(modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY).fillMaxSize().verticalScroll(rememberScrollState()).drawBehind {
                val spacing = 32.dp.toPx(); val headerOffset = 120.dp.toPx()
                for (i in 0..(size.height / spacing).toInt()) { val y = i * spacing + headerOffset; drawLine(lineBlue, Offset(0f, y), Offset(size.width, y), 1.dp.toPx()) }
            }) {
                Spacer(modifier = Modifier.height(56.dp))
                val textFieldColors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                TextField(value = title, onValueChange = { title = it }, placeholder = { Text("Topic Title", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black.copy(0.4f)) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp), textStyle = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.ExtraBold), colors = textFieldColors)
                Row(modifier = Modifier.fillMaxWidth().heightIn(min = 600.dp).drawBehind { val marginX = size.width * 0.28f; drawLine(color = marginRed, start = Offset(marginX, 0f), end = Offset(marginX, size.height), strokeWidth = 2.dp.toPx()) }) {
                    Box(modifier = Modifier.weight(0.28f).padding(start = 12.dp)) { TextField(value = cues, onValueChange = { cues = it }, placeholder = { Text("CUES", fontWeight = FontWeight.Bold, color = marginRed.copy(0.8f), fontSize = 14.sp) }, colors = textFieldColors) }
                    Box(modifier = Modifier.weight(0.72f).padding(start = 8.dp)) { TextField(value = notes, onValueChange = { notes = it }, placeholder = { Text("Take detailed notes here...") }, textStyle = TextStyle(fontSize = 18.sp, fontFamily = selectedFontInfo.fontFamily, lineHeight = 32.sp, textAlign = textAlign), modifier = Modifier.fillMaxWidth(), colors = textFieldColors) }
                }
                Divider(color = marginRed, thickness = 2.dp)
                Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Column {
                        Text("SUMMARY", fontSize = 14.sp, fontWeight = FontWeight.Black, color = marginRed)
                        TextField(value = summary, onValueChange = { summary = it }, placeholder = { Text("Summarize the key points here...", color = Color.Gray.copy(0.5f)) }, modifier = Modifier.fillMaxSize(), colors = textFieldColors)
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.TopStart).padding(12.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black) }
        }
    }
}