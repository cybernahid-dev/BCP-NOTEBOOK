package com.example.bcpnotebook.firebase

import com.example.bcpnotebook.model.NoteModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class FirestoreManager {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId: String get() = auth.currentUser?.uid ?: "anonymous"

    suspend fun saveNote(note: NoteModel) {
        try {
            val notesCollection = db.collection("users").document(userId).collection("notes")
            if (note.id.isEmpty()) {
                // Fix: document() খালি না রেখে add() ব্যবহার করা হলো
                notesCollection.add(note)
            } else {
                notesCollection.document(note.id).set(note)
            }
        } catch (e: Exception) {
            println("Firestore Error: ${e.message}")
        }
    }

    suspend fun getNotes(): List<NoteModel> {
        return try {
            val notesCollection = db.collection("users").document(userId).collection("notes")
            notesCollection.get().documents.map { it.data() }
        } catch (e: Exception) { emptyList() }
    }
}
