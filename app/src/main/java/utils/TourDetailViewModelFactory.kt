package utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itanes.appturismo.TourDetailViewModel
import data.repository.TourRepository

class TourDetailViewModelFactory(
    private val repository: TourRepository,
    private val tourId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TourDetailViewModel(repository, tourId) as T
}