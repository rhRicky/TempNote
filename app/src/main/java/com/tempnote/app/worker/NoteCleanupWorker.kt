package com.tempnote.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tempnote.app.data.NoteDatabase
import com.tempnote.app.data.NoteRepository

class NoteCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            NoteRepository(
                NoteDatabase.getInstance(applicationContext).noteDao(),
            ).cleanupExpiredNotes()
            Result.success()
        } catch (error: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val PERIODIC_WORK_NAME = "temp-note-periodic-cleanup"
        const val IMMEDIATE_WORK_NAME = "temp-note-immediate-cleanup"
    }
}
