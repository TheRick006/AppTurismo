package utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itanes.appturismo.TouristPointDetailViewModel
import data.repository.TourRepository

class TouristPointDetailViewModelFactory(
    private val repository: TourRepository,
    private val pointId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TouristPointDetailViewModel(repository, pointId) as T
}