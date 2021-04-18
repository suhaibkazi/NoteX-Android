package com.example.notex.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notex.R
import com.example.notex.room.Note
import kotlinx.android.synthetic.main.adapter_main.view.*

class NoteAdapter (var dataset: MutableList<Note>, var listener: OnAdapterListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_main, parent, false))
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = dataset[position]
        holder.view.text_title.text = note.title
    }

    inner class NoteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.text_title.setOnClickListener {
                listener.onClick(dataset[adapterPosition])
            }

            view.icon_delete.setOnClickListener {
                listener.onDelete(dataset[adapterPosition])
            }
        }
    }

    fun setData(newList: MutableList<Note>) {
        dataset = newList
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(note: Note)
        fun onDelete(note: Note)
    }
}