package com.itanes.appturismo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.TourRepository
import data.local.entity.CalendarDay
import data.local.entity.Note
import data.local.entity.Tour
import kotlinx.coroutines.launch
import utils.SettingsManager
import java.util.Calendar

class CalendarViewModel(private val repository: TourRepository,
                        private val settingsManager: SettingsManager
) : ViewModel() {

    private val today = Calendar.getInstance()
    private var currentYear = today.get(Calendar.YEAR)
    private var currentMonth = today.get(Calendar.MONTH) // 0-indexed
    private var selectedDay: Int? = null
    private var currentMonthTours: List<Tour> = emptyList()

    private val _calendarDays = MutableLiveData<List<CalendarDay>>()
    val calendarDays: LiveData<List<CalendarDay>> = _calendarDays

    private val _monthLabel = MutableLiveData<String>()
    val monthLabel: LiveData<String> = _monthLabel

    private val _selectedDayLabel = MutableLiveData<String?>()
    val selectedDayLabel: LiveData<String?> = _selectedDayLabel

    private val _selectedDayTours = MutableLiveData<List<Tour>>()
    val selectedDayTours: LiveData<List<Tour>> = _selectedDayTours

    private val _notesVisible = MutableLiveData<Boolean>()
    val notesVisible: LiveData<Boolean> = _notesVisible

    private val _notesForSelectedDay = MutableLiveData<List<Note>>()
    val notesForSelectedDay: LiveData<List<Note>> = _notesForSelectedDay

    private val _datesWithNotes = MutableLiveData<Set<String>>()
    val datesWithNotes: LiveData<Set<String>> = _datesWithNotes


    init {
        _notesVisible.value = settingsManager.showNotesByDefault
        loadMonth()
        observeNotesDates()
    }

    fun previousMonth() {
        currentMonth--
        if (currentMonth < 0) { currentMonth = 11; currentYear-- }
        selectedDay = null
        _selectedDayTours.value = emptyList()
        _selectedDayLabel.value = null
        loadMonth()
    }

    fun nextMonth() {
        currentMonth++
        if (currentMonth > 11) { currentMonth = 0; currentYear++ }
        selectedDay = null
        _selectedDayTours.value = emptyList()
        _selectedDayLabel.value = null
        loadMonth()
    }

    fun selectDay(day: Int) {
        selectedDay = day
        updateCalendarDays()
        val dayTours = currentMonthTours.filter { extractDay(it.startDate) == day }
        _selectedDayTours.value = dayTours
        _selectedDayLabel.value = "Tours del $day de ${monthName(currentMonth)}"

        val dateKey = "%04d-%02d-%02d".format(currentYear, currentMonth + 1, day)
        viewModelScope.launch {
            repository.getNotesForDate(dateKey).collect { notes ->
                _notesForSelectedDay.value = notes
            }
        }
    }
    private fun currentSelectedDateKey(): String? {
        val day = selectedDay ?: return null
        return "%04d-%02d-%02d".format(currentYear, currentMonth + 1, day)
    }

    private fun loadMonth() {
        val monthKey = "%04d-%02d".format(currentYear, currentMonth + 1)
        _monthLabel.value = "${monthName(currentMonth)} $currentYear"

        viewModelScope.launch {
            repository.getToursByMonthFlow(monthKey).collect { tours ->
                currentMonthTours = tours
                updateCalendarDays()
                selectedDay?.let { selectDay(it) }
            }
        }
    }

    private fun updateCalendarDays() {
        val toursByDay = currentMonthTours
            .mapNotNull { extractDay(it.startDate) }
            .toSet()

        val cal = Calendar.getInstance()
        cal.set(currentYear, currentMonth, 1)
        // Lunes = 0 ... Domingo = 6
        val offset = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val prevCal = cal.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val daysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val days = mutableListOf<CalendarDay>()

        // Días del mes anterior
        for (i in offset - 1 downTo 0) {
            days.add(CalendarDay(prevCal.get(Calendar.YEAR), prevCal.get(Calendar.MONTH),
                daysInPrevMonth - i, false, false, false, false))
        }

        // Días del mes actual
        for (d in 1..daysInMonth) {
            val isToday = today.get(Calendar.YEAR) == currentYear &&
                    today.get(Calendar.MONTH) == currentMonth &&
                    today.get(Calendar.DAY_OF_MONTH) == d
            days.add(CalendarDay(currentYear, currentMonth, d, true,
                toursByDay.contains(d), selectedDay == d, isToday))
        }

        // Días del siguiente mes (hasta completar 42 celdas)
        val nextCal = cal.clone() as Calendar
        nextCal.add(Calendar.MONTH, 1)
        var nextDay = 1
        while (days.size < 42) {
            days.add(CalendarDay(nextCal.get(Calendar.YEAR), nextCal.get(Calendar.MONTH),
                nextDay++, false, false, false, false))
        }

        _calendarDays.value = days
    }

    private fun extractDay(dateString: String): Int? = try {
        dateString.substring(8, 10).toIntOrNull()
    } catch (e: Exception) { null }

    private fun monthName(month: Int): String = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )[month]

    // Integracion con Notas
    private fun observeNotesDates() {
        viewModelScope.launch {
            repository.getDatesWithNotes().collect { dates ->
                _datesWithNotes.value = dates.toSet()
                updateCalendarDays()
            }
        }
    }
    fun toggleNotesVisibility() {
        val newValue = !(_notesVisible.value ?: true)
        _notesVisible.value = newValue
    }

    fun saveNoteForSelectedDay(title: String, content: String) {
        val dateKey = currentSelectedDateKey() ?: return
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                dateKey = dateKey,
                tourId = null,
                createdAt = 0,
                updatedAt = 0
            )
            repository.saveNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.saveNote(note) }
    }
}