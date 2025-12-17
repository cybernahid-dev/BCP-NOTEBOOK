package com.example.bcpnotebook.model

data class Note(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val cues: String = "",
    val notes: String = "",
    val summary: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
