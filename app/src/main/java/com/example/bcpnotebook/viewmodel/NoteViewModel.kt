package com.example.bcpnotebook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bcpnotebook.firebase.FirestoreManager
import com.example.bcpnotebook.model.NoteModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val firestoreManager = FirestoreManager()
    val notes = MutableStateFlow<List<NoteModel>>(emptyList())

    fun loadNotes() {
        viewModelScope.launch {
            notes.value = firestoreManager.getNotes()
        }
    }

    fun saveNote(note: NoteModel) {
        viewModelScope.launch {
            firestoreManager.saveNote(note)
            loadNotes() 
        }
    }
}
