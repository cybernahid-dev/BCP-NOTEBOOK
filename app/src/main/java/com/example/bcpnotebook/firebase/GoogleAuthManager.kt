package com.example.bcpnotebook.firebase

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()

    // 🔴 IMPORTANT: REPLACE THIS STRING WITH YOUR WEB CLIENT ID FROM FIREBASE CONSOLE!
    private val WEB_CLIENT_ID = "1020803077254-jp79prj0g6eg8sj9jf7o35s0i7td1ok6.apps.googleusercontent.com" 

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(WEB_CLIENT_ID) 
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val signInIntent: Intent
        get() = googleSignInClient.signInIntent

    suspend fun signInWithGoogle(intent: Intent): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.await()
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success("Google Login Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
