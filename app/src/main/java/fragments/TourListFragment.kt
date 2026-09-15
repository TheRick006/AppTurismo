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
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.R
import com.itanes.appturismo.Resource
import com.itanes.appturismo.TourListViewModel
import utils.TourListViewModelFactory
import utils.adapter.TourAdapter

class TourListFragment : Fragment() {

    private lateinit var viewModel: TourListViewModel
    private lateinit var adapter: TourAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tour_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)

        val repository = (requireActivity().application as AppTurismoApp).repository
        viewModel = ViewModelProvider(
            this,
            TourListViewModelFactory(repository)
        )[TourListViewModel::class.java]

        adapter = TourAdapter { tour ->
            Log.d("Log_TourListFragment", "Click en tour ${tour.tourId}")
            val action = TourListFragmentDirections
                .actionTourListToTourDetail(tour.tourId)
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.tours.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    //Log.d("TourListFragment", "Loading...")
                    progressBar.isVisible = true
                    recyclerView.isVisible = false
                }
                is Resource.Success -> {
                    /*Log.d("TourListFragment_Success", "Success: ${resource.data.size} tours")
                    Log.d("TourListFragment_Success", "RecyclerView visible: ${recyclerView.visibility}")
                    Log.d("TourListFragment_Success", "Adapter itemCount: ${adapter.itemCount}")*/
                    progressBar.isVisible = false
                    recyclerView.isVisible = true
                    adapter.submitList(resource.data) /*{
                        Log.d("TLFragment TourListFragment_SubmList", "submitList completado. itemCount: ${adapter.itemCount}")
                    }*/
                }
                is Resource.Error -> {
                    Log.e("TourListFragment_Error", "Error: ${resource.message}")
                    progressBar.isVisible = false
                    errorText.isVisible = true
                    errorText.text = resource.message
                }
            }
        }
    }
}