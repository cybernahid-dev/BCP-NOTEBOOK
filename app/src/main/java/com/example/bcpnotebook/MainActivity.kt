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
                val currentUser = FirebaseAuth.getInstance().currentUser
                
                // ল্যান্ডিং পেজ থেকে শুরু হবে
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("registration") { RegistrationScreen(navController) }
                    composable("notebook") { NotebookScreen(navController) }
                    composable("add_note") { AddNoteScreen(navController) }
                    composable("note_detail/{noteId}") { backStackEntry -> 
                        val noteId = backStackEntry.arguments?.getString("noteId") 
                        NoteDetailScreen(navController, noteId) 
                    }
                }
            }
        }
    }
}
