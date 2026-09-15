package com.itanes.appturismo.res.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.MainActivity
import com.itanes.appturismo.R
import com.itanes.appturismo.Resource
import com.itanes.appturismo.TouristPointDetailViewModel
import utils.TouristPointDetailViewModelFactory
import com.itanes.appturismo.data.local.entity.TouristPoint
import com.itanes.appturismo.utils.extensions.getImageList
import utils.adapter.ImagePagerAdapter

class TouristPointDetailFragment : Fragment() {

    private lateinit var viewModel: TouristPointDetailViewModel
    private lateinit var scrollContent: NestedScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var pointName: TextView
    private lateinit var pointDescription: TextView
    private lateinit var pointCoordinates: TextView
    private lateinit var imagePager: ViewPager2
    private lateinit var favoriteButton: MaterialButton
    private lateinit var openMapButton: MaterialButton
    private lateinit var shareButton: MaterialButton
    private var currentPoint: TouristPoint? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tourist_point_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        val pointId = arguments?.getInt("touristPointId") ?: return
        val repository = (requireActivity().application as AppTurismoApp).repository
        viewModel = ViewModelProvider(
            this,
            TouristPointDetailViewModelFactory(repository, pointId)
        )[TouristPointDetailViewModel::class.java]

        setupObservers()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        scrollContent = view.findViewById(R.id.scrollContent)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        pointName = view.findViewById(R.id.pointName)
        pointDescription = view.findViewById(R.id.pointDescription)
        pointCoordinates = view.findViewById(R.id.pointCoordinates)
        imagePager = view.findViewById(R.id.imagePager)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        openMapButton = view.findViewById(R.id.openMapButton)
        shareButton = view.findViewById(R.id.shareButton)
    }

    private fun setupObservers() {
        viewModel.point.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    currentPoint = resource.data
                    bindPoint(resource.data)
                    showContent()
                }
                is Resource.Error -> showError(resource.message)
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            updateFavoriteButton(isFav)
        }
    }

    private fun setupClickListeners() {
        favoriteButton.setOnClickListener {
            val wasFavorite = viewModel.isFavorite.value ?: false
            viewModel.toggleFavorite()

            if (!wasFavorite) {
                Toast.makeText(context, R.string.added_to_favorites, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show()
            }
        }

        openMapButton.setOnClickListener {
            currentPoint?.let { point ->
                openInMaps(point)
            }
        }

        shareButton.setOnClickListener {
            currentPoint?.let { point ->
                sharePoint(point)
            }
        }
    }

    private fun bindPoint(point: TouristPoint) {
        pointName.text = point.name
        pointDescription.text = point.description

        (requireActivity() as MainActivity).supportActionBar?.title = point.name

        pointCoordinates.text = "${point.latitude}, ${point.longitude}"
        setupViewPager(point.getImageList())
    }

    private fun setupViewPager(images: List<String>) {
        val adapter = ImagePagerAdapter(images)
        imagePager.adapter = adapter
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        favoriteButton.setIconResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        favoriteButton.text = getString(
            if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
        )
    }

    private fun openInMaps(point: TouristPoint) {
        val uri = "geo:0,0?q=${point.latitude},${point.longitude}(${point.name})"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback a Google Maps web
            val webUri = "https://www.google.com/maps?q=${point.latitude},${point.longitude}"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri))

            try {
                startActivity(webIntent)
            } catch (e2: Exception) {
                Toast.makeText(context, R.string.no_maps_app, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sharePoint(point: TouristPoint) {
        val shareText = """
            ${point.name}
            ${point.description}
            
            Coordenadas: ${point.latitude}, ${point.longitude}
            Ubicación: https://www.google.com/maps?q=${point.latitude},${point.longitude}
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, point.name)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_point)))
        } catch (e: Exception) {
            Toast.makeText(context, R.string.share_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        scrollContent.isVisible = false
        errorText.isVisible = false
    }

    private fun showContent() {
        progressBar.isVisible = false
        scrollContent.isVisible = true
        errorText.isVisible = false
    }

    private fun showError(message: String) {
        progressBar.isVisible = false
        scrollContent.isVisible = false
        errorText.isVisible = true
        errorText.text = message
    }
}
