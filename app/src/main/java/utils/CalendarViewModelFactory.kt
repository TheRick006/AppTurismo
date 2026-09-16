package utils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itanes.appturismo.CalendarViewModel
import data.repository.TourRepository

class CalendarViewModelFactory(
    private val repository: TourRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CalendarViewModel(repository, settingsManager) as T
}