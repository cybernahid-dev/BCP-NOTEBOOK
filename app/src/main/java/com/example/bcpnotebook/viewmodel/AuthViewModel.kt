package com.example.bcpnotebook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bcpnotebook.firebase.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authManager = FirebaseAuthManager()
    val authState = MutableStateFlow<String?>(null)
    
    // Auth State Reset করার জন্য
    fun resetState() { authState.value = null } 

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val result = authManager.login(email, pass)
            authState.value = result.getOrNull() ?: result.exceptionOrNull()?.message ?: "Error"
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            val result = authManager.register(email, pass)
            if (result.isSuccess) {
                // ভেরিফিকেশন মেইল পাঠানো
                authManager.currentUser?.sendEmailVerification()
                authState.value = "Registration Successful. Please check your email for verification."
            } else {
                authState.value = result.exceptionOrNull()?.message ?: "Error"
            }
        }
    }
}
