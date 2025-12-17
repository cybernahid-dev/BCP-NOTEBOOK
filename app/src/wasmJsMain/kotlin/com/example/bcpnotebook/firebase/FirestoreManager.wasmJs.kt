package com.example.bcpnotebook.firebase

import com.example.bcpnotebook.model.NoteModel

actual class FirestoreManager {
    actual suspend fun saveNote(note: NoteModel) {
        // Web-specific Firebase JS SDK logic can go here
        println("Web Save: Feature coming soon via JS Interop")
    }

    actual suspend fun getNotes(): List<NoteModel> {
        return emptyList()
    }
}
