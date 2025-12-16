package com.example.bcpnotebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.bcpnotebook.ui.*
import com.example.bcpnotebook.ui.theme.BCPNotebookTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCPNotebookTheme {
                val navController = rememberNavController()
                // চেক করা হচ্ছে ইউজার কি আগে থেকেই লগইন করা কি না
                val currentUser = FirebaseAuth.getInstance().currentUser
                val startDest = if (currentUser != null) "notebook" else "login"

                NavHost(navController = navController, startDestination = startDest) {
                    composable("login") { LoginScreen(navController) }
                    composable("notebook") { NotebookScreen(navController) }
                    composable("add_note") { AddNoteScreen(navController) }
                }
            }
        }
    }
}
