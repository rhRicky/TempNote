package com.tempnote.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tempnote.app.data.Note
import com.tempnote.app.databinding.ListItemNoteBinding

class NoteListAdapter(
    private val onNoteSelected: (Note) -> Unit,
    private val onRestore: (Note) -> Unit,
    private val onDeleteForever: (Note) -> Unit,
) : ListAdapter<Note, NoteListAdapter.NoteViewHolder>(DiffCallback) {

    enum class DisplayMode {
        ACTIVE,
        TRASH,
    }

    var displayMode: DisplayMode = DisplayMode.ACTIVE
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(ListItemNoteBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position), displayMode)
    }

    inner class NoteViewHolder(
        private val binding: ListItemNoteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note, mode: DisplayMode) {
            val context = binding.root.context
            binding.textTitle.text = NoteUiFormatter.titleFor(note, context)
            binding.textPreview.text = NoteUiFormatter.previewFor(note, context)

            if (mode == DisplayMode.ACTIVE) {
                binding.textMetaPrimary.text = NoteUiFormatter.autoTrashText(note, context)
                binding.textMetaSecondary.text = NoteUiFormatter.editedText(note, context)
                binding.actionRow.isVisible = false
                binding.root.setOnClickListener { onNoteSelected(note) }
            } else {
                binding.textMetaPrimary.text = NoteUiFormatter.deletedText(note, context)
                binding.textMetaSecondary.text = context.getString(
                    com.tempnote.app.R.string.meta_retention,
                    NoteUiFormatter.retentionLabel(context, note.retentionHours),
                )
                binding.actionRow.isVisible = true
                binding.buttonRestore.setOnClickListener { onRestore(note) }
                binding.buttonDeleteForever.setOnClickListener { onDeleteForever(note) }
                binding.root.setOnClickListener(null)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
    }
}
