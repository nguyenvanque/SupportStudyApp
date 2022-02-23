package com.example.suportstudy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suportstudy.databinding.LayoutItemNoteBinding
import com.example.suportstudy.model.Note

class NoteAdapter(private val listener: OnItemNoteListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var notes = mutableListOf<Note>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class NoteViewHolder(
        private val binding: LayoutItemNoteBinding,
        private val listener: OnItemNoteListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            this.binding.noteItem = note
            this.itemView.setOnLongClickListener {
                listener.onItemLongClickListener(note)
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    interface OnItemNoteListener {
        fun onItemLongClickListener(note: Note)
    }
}