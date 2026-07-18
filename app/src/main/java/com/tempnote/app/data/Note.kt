package com.tempnote.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastEditedAt: Long,
    val retentionHours: Int,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
)
