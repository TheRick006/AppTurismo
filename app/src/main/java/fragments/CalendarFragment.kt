package com.itanes.appturismo.res.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.CalendarViewModel
import utils.CalendarViewModelFactory
import com.itanes.appturismo.R
import com.itanes.appturismo.utils.adapter.CalendarDayAdapter
import data.local.entity.Note
import utils.adapter.NoteAdapter
import utils.adapter.TourAdapter

class CalendarFragment : Fragment() {

    private lateinit var viewModel: CalendarViewModel
    private lateinit var dayAdapter: CalendarDayAdapter
    private lateinit var tourAdapter: TourAdapter

    private lateinit var monthLabel: TextView
    private lateinit var selectedDayLabel: TextView
    private lateinit var emptyText: TextView
    private lateinit var calendarGrid: RecyclerView
    private lateinit var dayToursList: RecyclerView
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton

    private lateinit var toggleNotesButton: ImageButton
    private lateinit var addNoteButton: MaterialButton
    private lateinit var notesList: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private var editingNote: Note? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)

        val app = requireActivity().application as AppTurismoApp
        val repository = app.repository
        val settingsManager = app.settingsManager

        viewModel = ViewModelProvider(
            this,
            CalendarViewModelFactory(repository, settingsManager)
        )[CalendarViewModel::class.java]

        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }

    private fun bindViews(view: View) {
        monthLabel = view.findViewById(R.id.monthLabel)
        selectedDayLabel = view.findViewById(R.id.selectedDayLabel)
        emptyText = view.findViewById(R.id.emptyText)
        calendarGrid = view.findViewById(R.id.calendarGrid)
        dayToursList = view.findViewById(R.id.dayToursList)
        prevButton = view.findViewById(R.id.prevMonthButton)
        nextButton = view.findViewById(R.id.nextMonthButton)
        toggleNotesButton = view.findViewById(R.id.toggleNotesButton)
        addNoteButton = view.findViewById(R.id.addNoteButton)
        notesList = view.findViewById(R.id.notesList)
    }

    private fun setupRecyclerViews() {
        dayAdapter = CalendarDayAdapter { day -> viewModel.selectDay(day.day) }
        calendarGrid.layoutManager = GridLayoutManager(context, 7)
        calendarGrid.adapter = dayAdapter

        tourAdapter = TourAdapter { tour ->
                val action = CalendarFragmentDirections
                .actionCalendarToTourDetail(tour.tourId)
            findNavController().navigate(action)
        }
        dayToursList.layoutManager = LinearLayoutManager(context)
        dayToursList.adapter = tourAdapter

        noteAdapter = NoteAdapter(
            onDelete = { note -> confirmDeleteNote(note) },
            onEdit = { note -> showNoteDialog(note) }
        )
        notesList.layoutManager = LinearLayoutManager(context)
        notesList.adapter = noteAdapter
    }

    private fun setupListeners() {
        prevButton.setOnClickListener { viewModel.previousMonth() }
        nextButton.setOnClickListener { viewModel.nextMonth() }
        toggleNotesButton.setOnClickListener { viewModel.toggleNotesVisibility() }
        addNoteButton.setOnClickListener { showNoteDialog(null) }
    }

    private fun observeViewModel() {
        viewModel.monthLabel.observe(viewLifecycleOwner) {
            monthLabel.text = it
        }

        viewModel.calendarDays.observe(viewLifecycleOwner) {
            dayAdapter.submitList(it)
        }

        viewModel.selectedDayLabel.observe(viewLifecycleOwner) { label ->
                selectedDayLabel.isVisible = label != null
            selectedDayLabel.text = label
        }

        viewModel.selectedDayTours.observe(viewLifecycleOwner) { tours ->
                tourAdapter.submitList(tours)
            emptyText.isVisible = viewModel.selectedDayLabel.value != null && tours.isEmpty()
        }

        viewModel.notesVisible.observe(viewLifecycleOwner) { visible ->
            notesList.isVisible = visible
            addNoteButton.isVisible = visible
            toggleNotesButton.setImageResource(
                if (visible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
        }

        viewModel.notesForSelectedDay.observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)
        }
    }
    private fun showNoteDialog(note: Note?) {
        editingNote = note
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_note, null)
        val titleInput = view.findViewById<android.widget.EditText>(R.id.titleInput)
        val contentInput = view.findViewById<android.widget.EditText>(R.id.contentInput)

        note?.let {
            titleInput.setText(it.title)
            contentInput.setText(it.content)
        }

        builder.setTitle(if (note == null) "Nueva nota" else "Editar nota")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val title = titleInput.text.toString().trim()
                val content = contentInput.text.toString().trim()
                if (title.isNotEmpty()) {
                    if (note == null) {
                        viewModel.saveNoteForSelectedDay(title, content)
                    } else {
                        viewModel.updateNote(note.copy(title = title, content = content))
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDeleteNote(note: Note) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar nota")
            .setMessage("¿Eliminar esta nota?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteNote(note) }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    //Metodos de Nota

}