package com.tempnote.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tempnote.app.data.NoteDatabase
import com.tempnote.app.data.NoteRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(
        NoteDatabase.getInstance(application).noteDao(),
    )

    val activeNotes = repository.activeNotes.asLiveData()
    val trashedNotes = repository.trashedNotes.asLiveData()

    fun moveToTrash(noteId: Long) {
        viewModelScope.launch {
            repository.moveToTrash(noteId)
        }
    }

    fun restore(noteId: Long) {
        viewModelScope.launch {
            repository.restore(noteId)
        }
    }

    fun deleteForever(noteId: Long) {
        viewModelScope.launch {
            repository.deleteForever(noteId)
        }
    }

    fun runCleanupNow() {
        viewModelScope.launch {
            repository.cleanupExpiredNotes()
        }
    }
}
