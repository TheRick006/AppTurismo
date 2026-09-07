package com.itanes.appturismo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import data.repository.TourRepository
import data.local.entity.TourWithPoints
import kotlinx.coroutines.flow.catch

class TourDetailViewModel(
    private val repository: TourRepository,
    private val tourId: Int
) : ViewModel() {
    val tourWithPoints: LiveData<Resource<TourWithPoints>> = liveData {
        emit(Resource.Loading())
        Log.d("Log_TourDetailViewModel", "Cargando tour $tourId")
        try {
            repository.getTourWithPointsFlow(tourId).catch { e ->
                Log.e("TourDetailViewModel", "Error: ${e.message}")
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }.collect { tourWithPoints ->
                Log.d("TourDetailViewModel", "Tour obtenido: ${tourWithPoints.tour.name}")
                Log.d("TourDetailViewModel", "Puntos: ${tourWithPoints.points.size}")
                emit(Resource.Success(tourWithPoints))
            }
        } catch (e: Exception) {
            Log.e("TourDetailViewModel", "Excepción: ${e.message}")
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }
}