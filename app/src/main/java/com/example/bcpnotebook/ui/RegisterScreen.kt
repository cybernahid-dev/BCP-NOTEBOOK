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
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Google Sign Up Logic (Same as login)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("YOUR_WEB_CLIENT_ID_HERE") 
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context as Activity, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(context, "Account Created via Google!", Toast.LENGTH_SHORT).show()
                        navController.navigate("notebook") { popUpTo("login") { inclusive = true } }
                    } else {
                        Toast.makeText(context, "Google Sign Up Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

        OutlinedTextField(
            value = name, onValueChange = { name = it }, label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = SoftBlue, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = SoftBlue, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = SoftBlue, unfocusedBorderColor = Color.LightGray)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Sign Up Button
        Button(
            onClick = {
                if (!NetworkUtils.isInternetAvailable(context)) {
                    Toast.makeText(context, "Internet connection needed!", Toast.LENGTH_LONG).show()
                } else if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                            navController.navigate("notebook") { popUpTo("login") { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
            else Text("SIGN UP", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("OR", color = TextSecondary)
        Spacer(modifier = Modifier.height(20.dp))

        // Google Sign Up Button
        OutlinedButton(
            onClick = {
                if (NetworkUtils.isInternetAvailable(context)) {
                    launcher.launch(googleSignInClient.signInIntent)
                } else {
                    Toast.makeText(context, "Internet connection needed!", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up with Google", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))
        
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Log In", color = SoftBlue)
        }
    }
}
