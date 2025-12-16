package com.example.bcpnotebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bcpnotebook.ui.*
import com.example.bcpnotebook.ui.theme.BCPNotebookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCPNotebookTheme {
                val navController = rememberNavController()
                // startDestination set kora holo splash screen e
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("notebook") { NotebookScreen(navController) }
                    composable("add_note") { AddNoteScreen(navController) }
                }
            }
        }
    }
}
