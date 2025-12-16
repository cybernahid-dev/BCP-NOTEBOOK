package com.example.bcpnotebook.model

data class NoteModel(
    val id: String = "",
    val title: String = "",
    val cues: String = "",       // Cornell Cues/Questions column
    val notes: String = "",      // Cornell Main Notes column
    val summary: String = "",    // Cornell Summary section
    val timestamp: Long = System.currentTimeMillis()
)
