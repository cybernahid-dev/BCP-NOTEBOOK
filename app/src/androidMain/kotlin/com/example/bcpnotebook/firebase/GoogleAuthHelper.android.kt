package com.example.bcpnotebook.firebase

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
actual fun rememberGoogleAuthLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseAuth = Firebase.auth
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                scope.launch {
                    try {
                        val credential = GoogleAuthProvider.credential(idToken, null)
                        firebaseAuth.signInWithCredential(credential)
                        onResult(Result.success("Google Login Successful"))
                    } catch (e: Exception) {
                        onResult(Result.failure(e))
                    }
                }
            }
        } catch (e: ApiException) {
            onResult(Result.failure(e))
        }
    }

    return {
        // এখানে আপনার Firebase Console থেকে প্রাপ্ত Web Client ID বসাতে হবে
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1020803077254-jp79prj0g6eg8sj9jf7o35s0i7td1ok6.apps.googleusercontent.com") 
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        launcher.launch(client.signInIntent)
    }
}
