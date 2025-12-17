package com.example.bcpnotebook.firebase

actual class FirebaseAuthManager {
    actual suspend fun login(email: String, pass: String): Result<String> = Result.success("Web Login Placeholder")
    actual suspend fun register(email: String, pass: String): Result<String> = Result.success("Web Register Placeholder")
    actual suspend fun logout() {}
}
