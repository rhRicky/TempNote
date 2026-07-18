package com.tempnote.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.tempnote.app.data.NoteDatabase
import com.tempnote.app.data.NoteRepository

class NoteEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(
        NoteDatabase.getInstance(application).noteDao(),
    )

    private val noteId = MutableLiveData<Long?>()

    val note = noteId.switchMap { id ->
        if (id == null) {
            MutableLiveData(null)
        } else {
            repository.observeNote(id).asLiveData()
        }
    }

    fun setNoteId(id: Long?) {
        noteId.value = id
    }

    fun currentNoteId(): Long? = noteId.value

    suspend fun saveNote(
        title: String,
        content: String,
        retentionHours: Int,
    ): Long {
        val id = repository.upsertNote(noteId.value, title, content, retentionHours)
        noteId.postValue(id)
        return id
    }

    suspend fun moveCurrentNoteToTrash(): Boolean {
        val currentId = noteId.value ?: return false
        repository.moveToTrash(currentId)
        return true
    }
}
