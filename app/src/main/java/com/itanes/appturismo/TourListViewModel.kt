package com.itanes.appturismo

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
        emit(Resource.Loading())
        repository.getAllToursFlow().catch {
            emit(Resource.Error(it.message ?: "Error"))
        }.collect {
            emit(Resource.Success(it))
        }
    }
}