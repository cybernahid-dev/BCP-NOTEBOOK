package com.example.bcpnotebook.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success("Login Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success("Registration Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() = auth.signOut()
}
