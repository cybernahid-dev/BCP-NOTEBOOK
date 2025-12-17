package com.example.bcpnotebook.firebase

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGoogleAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    return {
        println("Web Google Auth: Placeholder")
    }
}
