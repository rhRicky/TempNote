package com.tempnote.app.ui

import android.content.Context
import android.text.format.DateUtils
import com.tempnote.app.R
import com.tempnote.app.data.Note
import java.util.concurrent.TimeUnit

object NoteUiFormatter {
    fun titleFor(note: Note, context: Context): String {
        return note.title.ifBlank { context.getString(R.string.label_untitled) }
    }

    fun previewFor(note: Note, context: Context): String {
        return note.content
            .replace('\n', ' ')
            .trim()
            .ifBlank { context.getString(R.string.label_empty_content) }
    }

    fun retentionLabel(context: Context, retentionHours: Int): String {
        return context.getString(RetentionOption.fromHours(retentionHours).labelRes)
    }

    fun relativeTime(timeInMillis: Long): CharSequence {
        return DateUtils.getRelativeTimeSpanString(
            timeInMillis,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE,
        )
    }

    fun autoTrashText(note: Note, context: Context): String {
        val deadline = note.lastEditedAt + TimeUnit.HOURS.toMillis(note.retentionHours.toLong())
        return context.getString(
            R.string.meta_auto_trash_at,
            relativeTime(deadline),
        )
    }

    fun editedText(note: Note, context: Context): String {
        return context.getString(
            R.string.meta_last_edited,
            relativeTime(note.lastEditedAt),
        )
    }

    fun deletedText(note: Note, context: Context): String {
        val deletedAt = note.deletedAt ?: note.updatedAt
        return context.getString(
            R.string.meta_deleted,
            relativeTime(deletedAt),
        )
    }
}
