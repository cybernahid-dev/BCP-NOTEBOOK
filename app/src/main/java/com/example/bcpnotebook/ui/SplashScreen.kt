package com.example.bcpnotebook.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.R
import com.example.bcpnotebook.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 1000, easing = { t ->
                var tVal = t
                tVal -= 1.0f
                val tension = 2f
                tVal * tVal * ((tension + 1) * tVal + tension) + 1.0f
            })
        )
        delay(2000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepSpace, Color.Black))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp).scale(scale.value)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "BCP NOTEBOOK",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonBlue,
                letterSpacing = 4.sp
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "DEVELOPED BY", fontSize = 10.sp, color = Color.Gray, letterSpacing = 2.sp)
            Text(text = "cybernahid-dev", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Team Bangladesh Cyber Panthers (BCP)",
                fontSize = 12.sp,
                color = NeonBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
