package com.app.realmsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteListAdapter(private val noteList: MutableList<Note>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, null))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoteViewHolder).bind(noteList[position])
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    inner class NoteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(value: Note) {
            view.findViewById<TextView>(R.id.txtNote).text = value.noteName
        }
    }
}