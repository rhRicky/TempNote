package com.tempnote.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tempnote.app.R
import com.tempnote.app.data.Note
import com.tempnote.app.databinding.ActivityNoteEditorBinding
import kotlinx.coroutines.launch

class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding
    private val viewModel: NoteEditorViewModel by viewModels()

    private var currentNoteId: Long? = null
    private var initialContentApplied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            saveAndFinish(fromBackAction = true)
        }

        binding.retentionToggle.check(binding.button24Hours.id)

        val noteId = intent.getLongExtra(EXTRA_NOTE_ID, INVALID_NOTE_ID).takeIf { it != INVALID_NOTE_ID }
        currentNoteId = noteId
        updateToolbarTitle()
        viewModel.setNoteId(noteId)
        observeNote()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveAndFinish(fromBackAction = true)
                }
            },
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        menu.findItem(R.id.action_trash)?.isVisible = currentNoteId != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveAndFinish(fromBackAction = false, showSavedToast = true)
                true
            }

            R.id.action_trash -> {
                moveCurrentNoteToTrash()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeNote() {
        viewModel.note.observe(this) { note ->
            if (note != null && !initialContentApplied) {
                bindNote(note)
            }
        }
    }

    private fun bindNote(note: Note) {
        initialContentApplied = true
        currentNoteId = note.id
        binding.inputTitle.setText(note.title)
        binding.inputContent.setText(note.content)
        binding.retentionToggle.check(RetentionOption.fromHours(note.retentionHours).buttonId)
        updateToolbarTitle()
        invalidateOptionsMenu()
    }

    private fun updateToolbarTitle() {
        binding.toolbar.title = if (currentNoteId == null) {
            getString(R.string.new_note)
        } else {
            getString(R.string.edit_note)
        }
    }

    private fun selectedRetentionHours(): Int {
        return RetentionOption.fromButtonId(binding.retentionToggle.checkedButtonId).hours
    }

    private fun saveAndFinish(
        fromBackAction: Boolean,
        showSavedToast: Boolean = false,
    ) {
        if (currentNoteId != null && !initialContentApplied) {
            finish()
            return
        }

        val title = binding.inputTitle.text?.toString().orEmpty()
        val content = binding.inputContent.text?.toString().orEmpty()

        if (currentNoteId == null && title.isBlank() && content.isBlank()) {
            if (fromBackAction) {
                Toast.makeText(this, R.string.toast_skipped_empty, Toast.LENGTH_SHORT).show()
            }
            finish()
            return
        }

        lifecycleScope.launch {
            currentNoteId = viewModel.saveNote(
                title = title,
                content = content,
                retentionHours = selectedRetentionHours(),
            )
            initialContentApplied = true
            updateToolbarTitle()
            invalidateOptionsMenu()

            if (showSavedToast) {
                Toast.makeText(this@NoteEditorActivity, R.string.toast_saved, Toast.LENGTH_SHORT).show()
            }

            if (!showSavedToast || fromBackAction) {
                finish()
            }
        }
    }

    private fun moveCurrentNoteToTrash() {
        lifecycleScope.launch {
            val moved = viewModel.moveCurrentNoteToTrash()
            if (moved) {
                Toast.makeText(
                    this@NoteEditorActivity,
                    R.string.toast_moved_to_trash,
                    Toast.LENGTH_SHORT,
                ).show()
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
        private const val INVALID_NOTE_ID = -1L
    }
}
