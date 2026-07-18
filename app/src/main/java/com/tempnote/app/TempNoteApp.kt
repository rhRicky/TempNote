package com.tempnote.app

import android.app.Application
import com.tempnote.app.worker.NoteCleanupScheduler

class TempNoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NoteCleanupScheduler.ensureScheduled(this)
        NoteCleanupScheduler.enqueueImmediate(this)
    }
}
