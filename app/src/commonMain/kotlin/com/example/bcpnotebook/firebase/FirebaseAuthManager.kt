package com.example.bcpnotebook.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseAuthManager {
    private val auth = Firebase.auth

    val currentUser get() = auth.currentUser

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, pass)
            Result.success("Login Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, pass)
            Result.success("Registration Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() = auth.signOut()
}
