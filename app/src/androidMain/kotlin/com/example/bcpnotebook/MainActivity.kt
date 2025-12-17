package com.example.bcpnotebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bcpnotebook.ui.BCPNotebookApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // এটি commonMain ফোল্ডারে থাকা আপনার মেইন কম্পোজেবল ফাংশন
            BCPNotebookApp()
        }
    }
}
