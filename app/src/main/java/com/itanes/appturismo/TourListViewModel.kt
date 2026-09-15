package com.itanes.appturismo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import data.repository.TourRepository
import data.local.entity.Tour
import kotlinx.coroutines.flow.catch

class TourListViewModel(
    private val repository: TourRepository
) : ViewModel() {
    val tours: LiveData<Resource<List<Tour>>> = liveData {
        //Log.d("TourListViewModel", "=== Iniciando carga ===")
        emit(Resource.Loading())
        repository.getAllToursFlow().catch {
            emit(Resource.Error(it.message ?: "Error"))
        }.collect { list ->
            //Log.d("TourListViewModel", "Emisión recibida: ${list.size} tours")
            /*list.forEach {
                Log.d("TourListViewModel", "  - ${it.tourId}: ${it.name}")
            }*/
            emit(Resource.Success(list))
        }
    }
}