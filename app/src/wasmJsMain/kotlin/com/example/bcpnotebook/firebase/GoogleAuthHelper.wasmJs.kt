package com.example.bcpnotebook.firebase

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
actual fun rememberGoogleAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    return {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val auth = Firebase.auth
                val provider = GoogleAuthProvider()
                // ব্রাউজারে এটি পপ-আপ ওপেন করবে
                auth.signInWithPopup(provider)
                onResult(Result.success("Web Login Success"))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}
