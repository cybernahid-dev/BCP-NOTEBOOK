package com.example.bcpnotebook.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2500)
        navController.navigate("login") { popUpTo("splash") { inclusive = true } }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = com.example.bcpnotebook.R.mipmap.ic_launcher),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp) // সাইজ বাড়ানো হয়েছে ক্লারিটির জন্য
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("BCP NOTEBOOK", color = NeonBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("developed by cybernahid-dev", color = GrayText, fontSize = 12.sp)
        }
    }
}
