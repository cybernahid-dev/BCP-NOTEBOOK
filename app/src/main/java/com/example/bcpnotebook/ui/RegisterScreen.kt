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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(com.example.bcpnotebook.R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context as Activity, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { 
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("notebook") { popUpTo("login") { inclusive = true } }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Google Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (NetworkUtils.isInternetAvailable(context)) {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            isLoading = false
                            if (it.isSuccessful) navController.navigate("notebook")
                            else Toast.makeText(context, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else Toast.makeText(context, "No Internet!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("SIGN UP", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(15.dp))
        Text("OR", color = TextSecondary)
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up with Google", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Log In", color = SoftBlue, fontWeight = FontWeight.Bold)
        }
    }
}
