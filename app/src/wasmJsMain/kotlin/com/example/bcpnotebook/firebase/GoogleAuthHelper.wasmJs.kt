package com.example.bcpnotebook.firebase

import androidx.compose.runtime.Composable

@Composable
actual fun rememberFirebaseAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    return {
        println("Google Auth on Web: Placeholder")
    }
}
