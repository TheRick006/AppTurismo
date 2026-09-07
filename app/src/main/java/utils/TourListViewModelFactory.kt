package utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itanes.appturismo.TourListViewModel
import data.repository.TourRepository

class TourListViewModelFactory(
    private val repository: TourRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TourListViewModel(repository) as T
}