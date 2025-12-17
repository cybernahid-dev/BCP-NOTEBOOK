package com.example.bcpnotebook.firebase

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFirebaseAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit
