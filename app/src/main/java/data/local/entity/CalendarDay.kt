package data.local.entity

data class CalendarDay(
    val year: Int,
    val month: Int,          // 0-indexed (Calendar.MONTH)
    val day: Int,
    val isCurrentMonth: Boolean,
    val hasTours: Boolean,
    val isSelected: Boolean,
    val isToday: Boolean
)