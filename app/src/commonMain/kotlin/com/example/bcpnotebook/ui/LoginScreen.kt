package com.example.bcpnotebook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bcpnotebook.firebase.rememberGoogleAuthLauncher

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val googleLogin = rememberGoogleAuthLauncher { result ->
        if (result.isSuccess) navController.navigate("notebook")
        else showToast("Google Login Failed")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("BCP Notebook Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            if(email.isNotEmpty() && password.isNotEmpty()) {
                showToast("Logging in...") 
                navController.navigate("notebook")
            } else {
                showToast("Fill all fields")
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
        TextButton(onClick = { googleLogin() }) {
            Text("Sign In with Google")
        }
        TextButton(onClick = { navController.navigate("registration") }) {
            Text("Don't have an account? Register")
        }
    }
}
