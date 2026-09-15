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
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.CalendarViewModel
import utils.CalendarViewModelFactory
import com.itanes.appturismo.R
import com.itanes.appturismo.utils.adapter.CalendarDayAdapter
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

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)

        val repository = (requireActivity().application as AppTurismoApp).repository
                viewModel = ViewModelProvider(this, CalendarViewModelFactory(repository))[CalendarViewModel::class.java]

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
    }

    private fun setupListeners() {
        prevButton.setOnClickListener { viewModel.previousMonth() }
        nextButton.setOnClickListener { viewModel.nextMonth() }
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
    }
}