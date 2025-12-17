package com.example.bcpnotebook.firebase

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit
