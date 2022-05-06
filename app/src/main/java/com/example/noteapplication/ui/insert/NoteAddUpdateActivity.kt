package com.example.noteapplication.ui.insert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.noteapplication.R
import com.example.noteapplication.database.Note
import com.example.noteapplication.databinding.ActivityMainBinding
import com.example.noteapplication.databinding.ActivityNoteAddUpdateBinding
import com.example.noteapplication.helper.DateHelper
import com.example.noteapplication.helper.ViewModelFactory
import com.example.noteapplication.ui.main.MainActivity

class NoteAddUpdateActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    private var isEdit = false
    private var note: Note? = null

    private lateinit var noteAddUpdateViewModel: NoteAddUpdateViewModel

    private var _activityNoteAddUpdateViewBinding: ActivityNoteAddUpdateBinding? = null
    private val binding get() = _activityNoteAddUpdateViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_add_update)

        _activityNoteAddUpdateViewBinding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        noteAddUpdateViewModel = obtainViewModel(this@NoteAddUpdateActivity)


        //memperbarui, menambah, menghapus item
        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null) {
            isEdit = true
        } else {
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            actionBarTitle = getString(R.string.change)
            btnTitle = getString(R.string.update)

            if (note != null) {
                note?.let { note ->
                    binding?.edtTitle?.setText(note.title)
                    binding?.edtDescription?.setText(note.description)
                }
            }
        } else {
            actionBarTitle = getString(R.string.add)
            btnTitle = getString(R.string.save)
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.btnSubmit?.text = btnTitle

        //menambahkan aksi button
        binding?.btnSubmit?.setOnClickListener {
            val title = binding?.edtTitle?.text.toString().trim()
            val description = binding?.edtDescription?.text.toString().trim()

            when {
                title.isEmpty() -> {
                    binding?.edtTitle?.error = getString(R.string.empty)
                }
                description.isEmpty() -> {
                    binding?.edtDescription?.error = getString(R.string.empty)
                }
                else -> {
                    note.let { note ->
                        note?.title = title
                        note?.description = description
                    }
                    if (isEdit) {
                        noteAddUpdateViewModel.update(note as Note)
                        showToast(getString(R.string.change))
                    } else {
                        note.let { note ->
                            note?.date = DateHelper.getCurrentDate()
                        }
                        noteAddUpdateViewModel.insert(note as Note)
                        showToast(getString(R.string.added))
                    }
                    finish()
                }
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //menghapus item dari database
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog (ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String
        if (isDialogClose) {
            dialogTitle = getString(R.string.cancel)
            dialogMessage = getString(R.string.message_cancel)
        } else {
            dialogMessage = getString(R.string.message_delete)
            dialogTitle = getString(R.string.delete)
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!isDialogClose) {
                    noteAddUpdateViewModel.delete(note as Note)
                    showToast(getString(R.string.delete))
                }
                finish()
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel()}
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _activityNoteAddUpdateViewBinding = null
    }

    private fun obtainViewModel (activity: AppCompatActivity): NoteAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory). get(NoteAddUpdateViewModel::class.java)
    }

}