package com.itanes.appturismo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import data.repository.TourRepository
import com.itanes.appturismo.data.local.entity.TouristPoint
import data.local.entity.Tour
import kotlinx.coroutines.flow.catch

class FavoritesViewModel(private val repository: TourRepository) : ViewModel() {

    val favoriteTours: LiveData<Resource<List<Tour>>> = liveData {
        emit(Resource.Loading())
        try {
            repository.getFavoriteToursFlow().catch { e ->
                emit(Resource.Error(e.message ?: "Error"))
            }.collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error"))
        }
    }

    val favoritePoints: LiveData<Resource<List<TouristPoint>>> = liveData {
        emit(Resource.Loading())
        try {
            repository.getFavoritePointsFlow().catch { e ->
                emit(Resource.Error(e.message ?: "Error"))
            }.collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error"))
        }
    }
}