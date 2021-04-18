package com.example.notex.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notex.R
import com.example.notex.room.Constant
import com.example.notex.room.Note
import com.example.notex.room.NoteDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private val db by lazy { NoteDB(this) }
    private var noteId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupView()
        setupListener()
    }

    private fun setupView() {
        when (intentType()) {
            Constant.TYPE_OPEN -> {
                getNote()
            }
        }
    }

    private fun setupListener() {
        back_button.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getNote() {
        noteId = intent.getIntExtra("note_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId).get(0)
            edit_title.setText(notes.title)
            edit_note.setText(notes.note)
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()

        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId)
            val count = notes.count()
            if (count > 0) {
                if (edit_title.text.isEmpty() && edit_note.text.isEmpty()) {
                    db.noteDao().deleteNote(Note(
                        noteId,
                        edit_title.text.toString(),
                        edit_note.text.toString()
                    ))
                }
                db.noteDao().updateNote(
                    Note(
                        noteId,
                        edit_title.text.toString(),
                        edit_note.text.toString()
                    )
                )
            } else {
                if (edit_title.text.isNotEmpty() || edit_note.text.isNotEmpty()) {
                    db.noteDao().addNote(
                        Note(
                            0,
                            edit_title.text.toString(),
                            edit_note.text.toString()
                        )
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}