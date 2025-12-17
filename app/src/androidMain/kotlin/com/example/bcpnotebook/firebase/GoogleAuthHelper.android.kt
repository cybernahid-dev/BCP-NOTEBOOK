package com.example.bcpnotebook.firebase

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
actual fun rememberGoogleAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    
    // আপনার Web Client ID এখানে দিন
    val WEB_CLIENT_ID = "1020803077254-jp79prj0g6eg8sj9jf7o35s0i7td1ok6.apps.googleusercontent.com"
    
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(WEB_CLIENT_ID)
        .requestEmail()
        .build()
        
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            CoroutineScope(Dispatchers.Main).launch {
                auth.signInWithCredential(credential).await()
                onResult(Result.success("Success"))
            }
        } catch (e: Exception) {
            onResult(Result.failure(e))
        }
    }
    
    return { launcher.launch(googleSignInClient.signInIntent) }
}
