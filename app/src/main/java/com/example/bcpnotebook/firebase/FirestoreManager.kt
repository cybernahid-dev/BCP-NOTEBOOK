package com.example.bcpnotebook.firebase

import com.example.bcpnotebook.model.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
    private val notesCollection = db.collection("users").document(userId).collection("notes")

    suspend fun saveNote(note: NoteModel) {
        if (note.id.isEmpty()) {
            val newDoc = notesCollection.document()
            notesCollection.document(newDoc.id).set(note.copy(id = newDoc.id)).await()
        } else {
            notesCollection.document(note.id).set(note).await()
        }
    }

    suspend fun getNotes(): List<NoteModel> {
        return try {
            notesCollection.get().await().toObjects(NoteModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
