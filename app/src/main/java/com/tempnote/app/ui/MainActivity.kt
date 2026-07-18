package com.tempnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.tempnote.app.R
import com.tempnote.app.data.Note
import com.tempnote.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val noteAdapter by lazy {
        NoteListAdapter(
            onNoteSelected = { note -> openEditor(note.id) },
            onRestore = { note ->
                viewModel.restore(note.id)
                Toast.makeText(this, R.string.toast_restored, Toast.LENGTH_SHORT).show()
            },
            onDeleteForever = { note ->
                viewModel.deleteForever(note.id)
                Toast.makeText(this, R.string.toast_deleted_forever, Toast.LENGTH_SHORT).show()
            },
        )
    }

    private var activeNotes: List<Note> = emptyList()
    private var trashedNotes: List<Note> = emptyList()
    private var currentMode = NoteListAdapter.DisplayMode.ACTIVE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.recyclerNotes.layoutManager = GridLayoutManager(
            this,
            resources.getInteger(R.integer.note_list_columns),
        )
        binding.recyclerNotes.adapter = noteAdapter

        binding.modeToggle.check(binding.buttonNotes.id)
        binding.modeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }

            currentMode = if (checkedId == binding.buttonTrash.id) {
                NoteListAdapter.DisplayMode.TRASH
            } else {
                NoteListAdapter.DisplayMode.ACTIVE
            }
            renderCurrentMode()
        }

        binding.fabAdd.setOnClickListener {
            openEditor(noteId = null)
        }

        viewModel.activeNotes.observe(this) { notes ->
            activeNotes = notes
            renderCurrentMode()
        }

        viewModel.trashedNotes.observe(this) { notes ->
            trashedNotes = notes
            renderCurrentMode()
        }

        viewModel.runCleanupNow()
    }

    override fun onResume() {
        super.onResume()
        viewModel.runCleanupNow()
    }

    private fun renderCurrentMode() {
        val currentList = if (currentMode == NoteListAdapter.DisplayMode.ACTIVE) {
            activeNotes
        } else {
            trashedNotes
        }

        noteAdapter.displayMode = currentMode
        noteAdapter.submitList(currentList.toList())

        val isTrash = currentMode == NoteListAdapter.DisplayMode.TRASH
        binding.fabAdd.hide()
        if (!isTrash) {
            binding.fabAdd.show()
        }

        if (isTrash) {
            binding.textSummaryCount.text = getString(R.string.trash_count, trashedNotes.size)
            binding.textSummaryBody.text = getString(R.string.trash_policy)
            binding.textEmptyTitle.text = getString(R.string.empty_trash_title)
            binding.textEmptyBody.text = getString(R.string.empty_trash_body)
        } else {
            binding.textSummaryCount.text = getString(R.string.notes_count, activeNotes.size)
            binding.textSummaryBody.text = getString(R.string.summary_body)
            binding.textEmptyTitle.text = getString(R.string.empty_notes_title)
            binding.textEmptyBody.text = getString(R.string.empty_notes_body)
        }

        binding.emptyState.visibility = if (currentList.isEmpty()) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }

    private fun openEditor(noteId: Long?) {
        val intent = Intent(this, NoteEditorActivity::class.java)
        if (noteId != null) {
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, noteId)
        }
        startActivity(intent)
    }
}
