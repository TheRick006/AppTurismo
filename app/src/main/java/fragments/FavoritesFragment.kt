package com.itanes.appturismo.res.fragments


import android.os.Bundle
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
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.FavoritesViewModel
import utils.FavoritesViewModelFactory
import com.itanes.appturismo.R
import com.itanes.appturismo.Resource
import utils.adapter.TouristPointAdapter

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView
    private lateinit var errorText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        val repository = (requireActivity().application as AppTurismoApp).repository
        viewModel = ViewModelProvider(
            this,
            FavoritesViewModelFactory(repository)
        )[FavoritesViewModel::class.java]

        setupRecyclerView()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyText = view.findViewById(R.id.emptyText)
        errorText = view.findViewById(R.id.errorText)
    }

    private fun setupRecyclerView() {
        val adapter = TouristPointAdapter { point ->
            val action = FavoritesFragmentDirections
                .actionFavoritesToPointDetail(point.touristPointId)
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.favoritePoints.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.isVisible = true
                    recyclerView.isVisible = false
                    emptyText.isVisible = false
                    errorText.isVisible = false
                }
                is Resource.Success -> {
                    progressBar.isVisible = false
                    recyclerView.isVisible = true
                    errorText.isVisible = false
                    adapter.submitList(resource.data)

                    emptyText.isVisible = resource.data.isEmpty()
                }
                is Resource.Error -> {
                    progressBar.isVisible = false
                    recyclerView.isVisible = false
                    emptyText.isVisible = false
                    errorText.isVisible = true
                    errorText.text = resource.message
                }
            }
        }
    }
}