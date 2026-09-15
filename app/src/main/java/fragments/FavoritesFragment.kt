package com.itanes.appturismo.res.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.FavoritesViewModel
import utils.FavoritesViewModelFactory
import com.itanes.appturismo.R
import com.itanes.appturismo.Resource
import utils.adapter.TourAdapter
import utils.adapter.TouristPointAdapter

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView
    private lateinit var errorText: TextView

    private lateinit var tourAdapter: TourAdapter
    private lateinit var pointAdapter: TouristPointAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        Log.d("FavoritesFragment", "TabLayout: $tabLayout")

        val repository = (requireActivity().application as AppTurismoApp).repository
        viewModel = ViewModelProvider(this, FavoritesViewModelFactory(repository))[FavoritesViewModel::class.java]

        setupAdapters()
        setupTabs()
        observeViewModel()

        showSection(0)  // Tours por defecto
    }

    private fun bindViews(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyText = view.findViewById(R.id.emptyText)
        errorText = view.findViewById(R.id.errorText)
    }

    private fun setupAdapters() {
        tourAdapter = TourAdapter { tour ->
            val action = FavoritesFragmentDirections
                .actionFavoritesToTourDetail(tour.tourId)
            findNavController().navigate(action)
        }

        pointAdapter = TouristPointAdapter { point ->
            val action = FavoritesFragmentDirections
                .actionFavoritesToPointDetail(point.touristPointId)
            findNavController().navigate(action)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("FavoritesFragment", "Tab seleccionado: ${tab.position}")
                showSection(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun showSection(position: Int) {
        if (position == 0) {
            recyclerView.adapter = tourAdapter
        } else {
            recyclerView.adapter = pointAdapter
        }
        updateEmptyState()
    }

    private fun observeViewModel() {
        viewModel.favoriteTours.observe(viewLifecycleOwner) { resource ->
            Log.d("FavoritesFragment", "favoriteTours: $resource")
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    tourAdapter.submitList(resource.data)
                    Log.d("FavoritesFragment", "Tours favoritos: ${resource.data.size}")
                    showContent()
                }
                is Resource.Error -> showError(resource.message)
            }
        }

        viewModel.favoritePoints.observe(viewLifecycleOwner) { resource ->
            Log.d("FavoritesFragment", "favoritePoints: $resource")
            when (resource) {
                is Resource.Loading -> { /* ignorar */ }
                is Resource.Success -> {
                    pointAdapter.submitList(resource.data)
                    Log.d("FavoritesFragment", "Puntos favoritos: ${resource.data.size}")
                    if (tabLayout.selectedTabPosition == 1) showContent()
                }
                is Resource.Error -> { /* ignorar */ }
            }
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        recyclerView.isVisible = false
        emptyText.isVisible = false
        errorText.isVisible = false
    }

    private fun showContent() {
        progressBar.isVisible = false
        recyclerView.isVisible = true
        errorText.isVisible = false
        updateEmptyState()
    }

    private fun showError(message: String) {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        emptyText.isVisible = false
        errorText.isVisible = true
        errorText.text = message
    }

    private fun updateEmptyState() {
        val count = recyclerView.adapter?.itemCount ?: 0
        val isEmpty = count == 0 && !progressBar.isVisible && !errorText.isVisible
        emptyText.isVisible = isEmpty
        if (isEmpty) {
            emptyText.text = if (tabLayout.selectedTabPosition == 0)
                "No hay tours favoritos" else "No hay lugares favoritos"
        }
    }
}