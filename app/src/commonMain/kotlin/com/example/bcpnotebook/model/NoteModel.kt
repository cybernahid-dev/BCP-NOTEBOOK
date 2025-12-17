package com.example.bcpnotebook.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteModel(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val cues: String = "",
    val notes: String = "",
    val summary: String = "",
    val timestamp: Long = 0L
)
