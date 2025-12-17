package com.example.bcpnotebook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("BCP NOTEBOOK", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = SoftBlue)
            Text("Your Digital Cornell Workspace", fontSize = 14.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(100.dp))
            CircularProgressIndicator(color = SoftBlue, strokeWidth = 2.dp)
        }
    }
}
