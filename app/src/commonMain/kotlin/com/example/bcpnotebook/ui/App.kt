package com.example.bcpnotebook.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bcpnotebook.ui.theme.BCPNotebookTheme

@Composable
fun BCPNotebookApp() {
    val navController = rememberNavController()
    BCPNotebookTheme {
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") { SplashScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("notebook") { NotebookScreen(navController) }
            composable("add_note") { AddNoteScreen(navController) }
        }
    }
}
