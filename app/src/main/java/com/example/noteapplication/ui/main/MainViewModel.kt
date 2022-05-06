package com.example.noteapplication.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.noteapplication.database.Note
import com.example.noteapplication.repository.NoteRepository

class MainViewModel(application: Application): ViewModel() {
    private val mNoteRepository: NoteRepository = NoteRepository(application)

    fun getAllNotes(): LiveData<List<Note>> = mNoteRepository.getAllNotes()
}