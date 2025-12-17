package com.example.bcpnotebook.firebase

import com.example.bcpnotebook.model.NoteModel

expect class FirestoreManager() {
    suspend fun saveNote(note: NoteModel)
    suspend fun getNotes(): List<NoteModel>
}
