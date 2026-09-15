package com.itanes.appturismo.res.fragments

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.MainActivity
import com.itanes.appturismo.R
import com.itanes.appturismo.Resource
import com.itanes.appturismo.TourDetailViewModel
import utils.TourDetailViewModelFactory
import data.local.entity.Tour
import utils.adapter.TouristPointAdapter
import java.util.Locale

class TourDetailFragment : Fragment() {

    private lateinit var viewModel: TourDetailViewModel
    private lateinit var scrollContent: NestedScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var tourImage: ImageView
    private lateinit var tourName: TextView
    private lateinit var tourDescription: TextView
    private lateinit var pointsGrid: RecyclerView
    private lateinit var adapter: TouristPointAdapter
    private lateinit var tourDate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tour_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view) //Comentar?

        //val tourId = arguments?.getInt("tourId") ?: return
        val tourId = arguments?.getInt("tourId") ?: -1
        if(tourId == -1){
            Log.d("Log_TourDetailFragment","TourId Invalido")
            return
        }
        val repository = (requireActivity().application as AppTurismoApp).repository
        viewModel = ViewModelProvider(
            this,
            TourDetailViewModelFactory(repository, tourId)
        )[TourDetailViewModel::class.java]

        setupRecyclerView()
        setupObservers()
    }

    private fun initializeViews(view: View) {
        scrollContent = view.findViewById(R.id.scrollContent)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        tourImage = view.findViewById(R.id.tourImage)
        tourName = view.findViewById(R.id.tourName)
        tourDescription = view.findViewById(R.id.tourDescription)
        pointsGrid = view.findViewById(R.id.pointsGrid)
        tourDate = view.findViewById(R.id.tourDate)
    }

    private fun setupRecyclerView() {
        adapter = TouristPointAdapter { point ->
            val action = TourDetailFragmentDirections
                .actionTourDetailToPointDetail(point.touristPointId)
            findNavController().navigate(action)
        }

        pointsGrid.adapter = adapter
        val columns = calculateColumns()
        pointsGrid.layoutManager = GridLayoutManager(context, columns)
    }

    private fun setupObservers() {
        viewModel.tourWithPoints.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    //Log.d("TourDetailFragment", "Estado: Loading")
                    progressBar.visibility = View.VISIBLE
                    scrollContent.visibility = View.GONE
                    errorText.visibility = View.GONE
                }
                is Resource.Success -> {
                    /*Log.d("TourDetailFragment", "Estado: Success")
                    Log.d("TourDetailFragment", "Tour: ${resource.data.tour.name}")
                    Log.d("TourDetailFragment", "Puntos: ${resource.data.points.size}")
*/
                    progressBar.visibility = View.GONE
                    scrollContent.visibility = View.VISIBLE
                    errorText.visibility = View.GONE

                    bindTour(resource.data.tour)
                    adapter.submitList(resource.data.points)/*{
                        Log.d("TDT TourDetailFragment_SubmList", "submitList completado. itemCount: ${adapter.itemCount}")
                    }*/
                }
                is Resource.Error -> {
                    //Log.e("TourDetailFragment", "Estado: Error - ${resource.message}")
                    progressBar.visibility = View.GONE
                    scrollContent.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    errorText.text = resource.message
                }
            }
        }
    }

    private fun bindTour(tour: Tour) {
        tourName.text = tour.name
        tourDescription.text = tour.description
        tourDate.text = "${formatDate(tour.startDate)} • ${tour.schedule}"
        // Actualizar título de la toolbar
        (requireActivity() as MainActivity).supportActionBar?.title = tour.name
        Glide.with(this)
            .load(tour.imageUrl)
            .into(tourImage)
    }

    private fun calculateColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val itemWidthDp = 160f
        return (screenWidthDp / itemWidthDp).toInt().coerceAtLeast(2)
    }
    private fun formatDate(dateString: String): String {
        return try {
            val cleanDate = dateString.substringBefore("+").substringBefore(".").trim()
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            inputFormat.parse(cleanDate)?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}