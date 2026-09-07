package com.itanes.appturismo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import data.repository.TourRepository
import com.itanes.appturismo.data.local.entity.TouristPoint
import kotlinx.coroutines.flow.catch

class FavoritesViewModel(
    private val repository: TourRepository
) : ViewModel() {

    val favoritePoints: LiveData<Resource<List<TouristPoint>>> = liveData {
        emit(Resource.Loading())
        try {
            repository.getFavoritePointsFlow().catch { e ->
                emit(Resource.Error(e.message ?: "Error desconocido 1"))
            }.collect { points ->
                emit(Resource.Success(points))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido 2"))
        }
    }
}