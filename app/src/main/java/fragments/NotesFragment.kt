package com.itanes.appturismo.res.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.R
import data.local.entity.Note
import data.repository.TourRepository
import utils.adapter.NoteAdapter
import kotlinx.coroutines.launch

class NotesFragment : Fragment() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var repository: TourRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_notes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = (requireActivity().application as AppTurismoApp).repository

        val recyclerView = view.findViewById<RecyclerView>(R.id.notesRecycler)
        noteAdapter = NoteAdapter(
            onDelete = { note ->
                lifecycleScope.launch { repository.deleteNote(note) }
            },
            onEdit = { /* navegar o abrir diálogo */ }
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = noteAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getAllNotes().collect { notes ->
                noteAdapter.submitList(notes)
            }
        }
    }
}