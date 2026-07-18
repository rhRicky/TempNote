package com.tempnote.app.data

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow

data class CleanupResult(
    val movedToTrash: Int,
    val permanentlyDeleted: Int,
)

class NoteRepository(
    private val noteDao: NoteDao,
) {
    val activeNotes: Flow<List<Note>> = noteDao.observeActiveNotes()
    val trashedNotes: Flow<List<Note>> = noteDao.observeTrashedNotes()

    fun observeNote(id: Long): Flow<Note?> = noteDao.observeNote(id)

    suspend fun upsertNote(
        noteId: Long?,
        title: String,
        content: String,
        retentionHours: Int,
    ): Long {
        val now = System.currentTimeMillis()
        val sanitizedTitle = title.trim()
        val sanitizedContent = content.trimEnd()

        return if (noteId == null) {
            noteDao.insert(
                Note(
                    title = sanitizedTitle,
                    content = sanitizedContent,
                    createdAt = now,
                    updatedAt = now,
                    lastEditedAt = now,
                    retentionHours = retentionHours,
                ),
            )
        } else {
            val existing = noteDao.getNoteById(noteId)
            noteDao.insert(
                existing?.copy(
                    title = sanitizedTitle,
                    content = sanitizedContent,
                    updatedAt = now,
                    lastEditedAt = now,
                    retentionHours = retentionHours,
                    isDeleted = false,
                    deletedAt = null,
                ) ?: Note(
                    id = noteId,
                    title = sanitizedTitle,
                    content = sanitizedContent,
                    createdAt = now,
                    updatedAt = now,
                    lastEditedAt = now,
                    retentionHours = retentionHours,
                ),
            )
        }
    }

    suspend fun moveToTrash(id: Long) {
        noteDao.moveToTrash(id, System.currentTimeMillis())
    }

    suspend fun restore(id: Long) {
        noteDao.restore(id, System.currentTimeMillis())
    }

    suspend fun deleteForever(id: Long) {
        noteDao.deleteById(id)
    }

    suspend fun cleanupExpiredNotes(): CleanupResult {
        val now = System.currentTimeMillis()
        val moved = noteDao.moveExpiredToTrash(now)
        val permanentlyDeleted = noteDao.purgeDeletedBefore(
            now - TimeUnit.DAYS.toMillis(30),
        )
        return CleanupResult(
            movedToTrash = moved,
            permanentlyDeleted = permanentlyDeleted,
        )
    }
}
