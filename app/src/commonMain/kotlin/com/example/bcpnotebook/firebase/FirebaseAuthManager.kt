package com.example.bcpnotebook.firebase

expect class FirebaseAuthManager() {
    suspend fun login(email: String, pass: String): Result<String>
    suspend fun register(email: String, pass: String): Result<String>
    suspend fun logout()
}
