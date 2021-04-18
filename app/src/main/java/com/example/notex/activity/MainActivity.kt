package com.example.notex.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notex.R
import com.example.notex.room.Constant
import com.example.notex.room.Note
import com.example.notex.room.NoteDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val db by lazy { NoteDB(this) }
    lateinit var noteAdapter: NoteAdapter
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecyclerView()
        setupSearchListener()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val dataset: List<Note> = db.noteDao().getNotes()
            withContext(Dispatchers.Main) {
                noteAdapter.setData(dataset.toMutableList())
            }
        }
    }

    private fun setupListener() {
        button_create.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    private fun setupRecyclerView() {

        noteAdapter = NoteAdapter(
                arrayListOf(),
                object : NoteAdapter.OnAdapterListener {
                    override fun onClick(note: Note) {
                        intentEdit(Constant.TYPE_OPEN, note.id)
                    }

//                    override fun onUpdate(note: Note) {
//                        intentEdit(Constant.TYPE_UPDATE, note.id)
//                    }

                    override fun onDelete(note: Note) {
                        deleteAlert(note)
                    }
                })

        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }

    }

    private fun intentEdit(intent_type: Int, note_id: Int) {
        startActivity(
                Intent(this, EditActivity::class.java)
                        .putExtra("intent_type", intent_type)
                        .putExtra("note_id", note_id)
        )

    }

    private fun deleteAlert(note: Note) {
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Confirm Delete Action?")
            setMessage("Do you want to delete ${note.title}?")
            setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Yes") { dialogInterface, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }

        dialog.show()
    }

    private fun setupSearchListener() {

        searchView = findViewById(R.id.searchView)
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchDatabase(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchDatabase(query)
                }
                return true
            }

            private fun searchDatabase(query: String) {
                val searchQuery = "%$query%"

                CoroutineScope(Dispatchers.IO).launch {
                    val dataset: List<Note> = db.noteDao().searchDatabase(searchQuery)
                    withContext(Dispatchers.Main) {
                        noteAdapter.setData(dataset.toMutableList())
                    }
                }
            }
        })
    }
}