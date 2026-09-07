package utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itanes.appturismo.FavoritesViewModel
import data.repository.TourRepository

class FavoritesViewModelFactory(
    private val repository: TourRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FavoritesViewModel(repository) as T
}