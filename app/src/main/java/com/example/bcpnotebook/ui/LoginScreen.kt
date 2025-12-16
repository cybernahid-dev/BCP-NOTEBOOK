package com.example.bcpnotebook.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bcpnotebook.ui.theme.*
import com.example.bcpnotebook.utils.NetworkUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Google Sign In Configuration
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("YOUR_WEB_CLIENT_ID_HERE") // Firebase Console থেকে Web Client ID বসাতে হবে
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context as Activity, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // ফিক্স: শুধুমাত্র রেজাল্ট OK হলেই প্রসেস করবে, ব্যাক করলে ঢুকবে না
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                
                if (NetworkUtils.isInternetAvailable(context)) {
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("notebook") { popUpTo("login") { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Internet connection needed to verify!", Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Login Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Sign in to your workspace", color = TextSecondary, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(30.dp))

        // Email & Password Fields
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = SoftBlue, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = SoftBlue, unfocusedBorderColor = Color.LightGray)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Login Button
        Button(
            onClick = {
                if (!NetworkUtils.isInternetAvailable(context)) {
                    Toast.makeText(context, "Internet connection needed!", Toast.LENGTH_LONG).show()
                } else if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("notebook") { popUpTo("login") { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("LOGIN", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("OR", color = TextSecondary)
        Spacer(modifier = Modifier.height(20.dp))

        // Google Login Button
        OutlinedButton(
            onClick = {
                if (NetworkUtils.isInternetAvailable(context)) {
                    launcher.launch(googleSignInClient.signInIntent)
                } else {
                    Toast.makeText(context, "Internet connection needed!", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
        ) {
             // আইকন এরর এড়াতে ডিফল্ট আইকন বা টেক্সট ব্যবহার করছি, আপনি চাইলে ইমেজ রিসোর্স দিতে পারেন
            Text("Continue with Google", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = { navController.navigate("registration") }) {
            Text("Don't have an account? Sign Up", color = SoftBlue, fontWeight = FontWeight.Bold)
        }
    }
}
