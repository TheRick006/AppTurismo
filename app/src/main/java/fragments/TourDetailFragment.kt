package com.itanes.appturismo.res.fragments

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.MainActivity
import com.itanes.appturismo.R
import com.itanes.appturismo.Resource
import com.itanes.appturismo.TourDetailViewModel
import com.itanes.appturismo.data.local.entity.TouristPoint
import utils.TourDetailViewModelFactory
import data.local.entity.Tour
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import utils.adapter.TouristPointAdapter
import java.util.Locale

class TourDetailFragment : Fragment() {

    private lateinit var viewModel: TourDetailViewModel

    private lateinit var scrollContent: NestedScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var tourImage: ImageView
    private lateinit var tourName: TextView
    private lateinit var tourDate: TextView
    private lateinit var tourDescription: TextView
    private lateinit var pointsGrid: RecyclerView
    private lateinit var favoriteButton: MaterialButton
    private lateinit var viewRouteButton: MaterialButton
    private lateinit var routePanel: LinearLayout
    private lateinit var routeMap: MapView
    private lateinit var openRouteInGoogleMapsButton: MaterialButton

    private lateinit var adapter: TouristPointAdapter
    private var currentTour: Tour? = null
    private var currentPoints: List<TouristPoint> = emptyList()
    private var routeVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicializar configuración de osmdroid antes de inflar
        Configuration.getInstance().load(
            requireContext(),
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName
        return inflater.inflate(R.layout.fragment_tour_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        val tourId = arguments?.getInt("tourId") ?: return
        val repository = (requireActivity().application as AppTurismoApp).repository
        viewModel = ViewModelProvider(
            this,
            TourDetailViewModelFactory(repository, tourId)
        )[TourDetailViewModel::class.java]

        setupRecyclerView()
        setupMap()
        setupClickListeners()
        setupObservers()
    }

    private fun initializeViews(view: View) {
        scrollContent = view.findViewById(R.id.scrollContent)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        tourImage = view.findViewById(R.id.tourImage)
        tourName = view.findViewById(R.id.tourName)
        tourDate = view.findViewById(R.id.tourDate)
        tourDescription = view.findViewById(R.id.tourDescription)
        pointsGrid = view.findViewById(R.id.pointsGrid)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        viewRouteButton = view.findViewById(R.id.viewRouteButton)
        routePanel = view.findViewById(R.id.routePanel)
        routeMap = view.findViewById(R.id.routeMap)
        openRouteInGoogleMapsButton = view.findViewById(R.id.openRouteInGoogleMapsButton)
    }

    private fun setupRecyclerView() {
        adapter = TouristPointAdapter { point ->
            val action = TourDetailFragmentDirections
                .actionTourDetailToPointDetail(point.touristPointId)
            findNavController().navigate(action)
        }
        val columns = calculateColumns()
        pointsGrid.layoutManager = GridLayoutManager(context, columns)
        pointsGrid.adapter = adapter
    }

    private fun setupMap() {
        routeMap.setTileSource(TileSourceFactory.MAPNIK)
        routeMap.setMultiTouchControls(true)
        routeMap.controller.setZoom(13.0)
    }

    private fun setupClickListeners() {
        favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
            val isFav = viewModel.isFavorite.value ?: false
            Toast.makeText(
                context,
                if (!isFav) "Añadido a favoritos" else "Eliminado de favoritos",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewRouteButton.setOnClickListener {
            toggleRoutePanel()
        }

        openRouteInGoogleMapsButton.setOnClickListener {
            openRouteInGoogleMaps()
        }
    }

    private fun setupObservers() {
        viewModel.tourWithPoints.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.isVisible = true
                    scrollContent.isVisible = false
                    errorText.isVisible = false
                }
                is Resource.Success -> {
                    progressBar.isVisible = false
                    scrollContent.isVisible = true
                    errorText.isVisible = false
                    currentTour = resource.data.tour
                    currentPoints = resource.data.points
                    bindTour(resource.data.tour)
                    adapter.submitList(resource.data.points)

                    // Si el panel estaba abierto, redibujar
                    if (routeVisible) drawRoute()
                }
                is Resource.Error -> {
                    progressBar.isVisible = false
                    scrollContent.isVisible = false
                    errorText.isVisible = true
                    errorText.text = resource.message
                }
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            updateFavoriteButton(isFav)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        favoriteButton.setIconResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        favoriteButton.text = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos"
    }

    private fun bindTour(tour: Tour) {
        tourName.text = tour.name
        tourDescription.text = tour.description
        tourDate.text = "${formatDate(tour.startDate)} • ${tour.schedule}"

        Glide.with(this)
            .load(tour.imageUrl)
            .into(tourImage)
    }

    private fun toggleRoutePanel() {
        if (!routeVisible) {
            if (currentPoints.isEmpty()) {
                Toast.makeText(context, "No hay puntos para mostrar", Toast.LENGTH_SHORT).show()
                return
            }
            routePanel.isVisible = true
            routeVisible = true
            viewRouteButton.text = "Ocultar recorrido"
            drawRoute()
        } else {
            routePanel.isVisible = false
            routeVisible = false
            viewRouteButton.text = "Ver recorrido"
        }
    }

    private fun drawRoute() {
        if (currentPoints.isEmpty()) return
        routeMap.overlays.clear()

        val geoPoints = currentPoints.map {
            GeoPoint(it.latitude, it.longitude)
        }

        // Marcadores numerados
        geoPoints.forEachIndexed { index, geoPoint ->
            val marker = Marker(routeMap).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = currentPoints[index].name
                snippet = currentPoints[index].description
            }
            routeMap.overlays.add(marker)
        }

        // Polyline conectando todos los puntos
        if (geoPoints.size >= 2) {
            val line = Polyline().apply {
                setPoints(geoPoints)
                outlinePaint.color = 0xFF6200EE.toInt() // purple_500
                outlinePaint.strokeWidth = 8f
            }
            routeMap.overlays.add(line)
        }

        // Centrar el mapa para mostrar todos los puntos
        if (geoPoints.isNotEmpty()) {
            routeMap.post {
                routeMap.zoomToBoundingBox(
                    org.osmdroid.util.BoundingBox.fromGeoPoints(geoPoints),
                    true,
                    100
                )
            }
        }

        routeMap.invalidate()
    }

    private fun openRouteInGoogleMaps() {
        if (currentPoints.isEmpty()) {
            Toast.makeText(context, "No hay puntos para mostrar", Toast.LENGTH_SHORT).show()
            return
        }

        // Google Maps Directions API URL
        // https://www.google.com/maps/dir/?api=1&origin=LAT,LNG&destination=LAT,LNG&waypoints=LAT1,LNG1|LAT2,LNG2
        val origin = currentPoints.first()
        val destination = currentPoints.last()
        val waypoints = if (currentPoints.size > 2) {
            currentPoints.subList(1, currentPoints.size - 1)
                .joinToString("|") { "${it.latitude},${it.longitude}" }
        } else ""

        val url = buildString {
            append("https://www.google.com/maps/dir/?api=1")
            append("&origin=${origin.latitude},${origin.longitude}")
            append("&destination=${destination.latitude},${destination.longitude}")
            if (waypoints.isNotEmpty()) append("&waypoints=$waypoints")
            append("&travelmode=driving")
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback: navegador
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e2: Exception) {
                Toast.makeText(context, "No hay app de mapas disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val cleanDate = dateString.substringBefore("+").substringBefore(".").trim()
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("es", "ES"))
            inputFormat.parse(cleanDate)?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun calculateColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val itemWidthDp = 160f
        return (screenWidthDp / itemWidthDp).toInt().coerceAtLeast(2)
    }

    override fun onResume() {
        super.onResume()
        if (::routeMap.isInitialized) routeMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (::routeMap.isInitialized) routeMap.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::routeMap.isInitialized) routeMap.onDetach()
    }
}