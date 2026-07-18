package com.tempnote.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY updatedAt DESC")
    fun observeActiveNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY deletedAt DESC, updatedAt DESC")
    fun observeTrashedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun observeNote(id: Long): Flow<Note?>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Query(
        """
        UPDATE notes
        SET isDeleted = 1,
            deletedAt = :deletedAt,
            updatedAt = :deletedAt
        WHERE id = :id
        """
    )
    suspend fun moveToTrash(id: Long, deletedAt: Long)

    @Query(
        """
        UPDATE notes
        SET isDeleted = 0,
            deletedAt = NULL,
            updatedAt = :restoredAt,
            lastEditedAt = :restoredAt
        WHERE id = :id
        """
    )
    suspend fun restore(id: Long, restoredAt: Long)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        UPDATE notes
        SET isDeleted = 1,
            deletedAt = :now,
            updatedAt = :now
        WHERE isDeleted = 0
          AND (lastEditedAt + (retentionHours * 3600000)) <= :now
        """
    )
    suspend fun moveExpiredToTrash(now: Long): Int

    @Query(
        """
        DELETE FROM notes
        WHERE isDeleted = 1
          AND deletedAt IS NOT NULL
          AND deletedAt <= :cutoff
        """
    )
    suspend fun purgeDeletedBefore(cutoff: Long): Int
}
