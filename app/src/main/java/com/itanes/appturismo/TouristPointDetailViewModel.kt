package com.itanes.appturismo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.itanes.appturismo.data.local.entity.TouristPoint
import data.repository.TourRepository
import kotlinx.coroutines.launch
import com.itanes.appturismo.Resource

class TouristPointDetailViewModel(
    private val repository: TourRepository,
    private val pointId: Int
) : ViewModel() {

    val point: LiveData<Resource<TouristPoint>> = liveData {
        emit(Resource.Loading())
        try {
            val result = repository.getPointById(pointId)
            if (result != null) {
                emit(Resource.Success(result))
            } else {
                emit(Resource.Error("Punto no encontrado"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    val isFavorite: LiveData<Boolean> = repository.isFavoriteLiveData(pointId)

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = isFavorite.value ?: false
            repository.toggleFavorite(pointId, !current)
        }
    }
}