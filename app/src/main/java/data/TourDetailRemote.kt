package data

data class TourDetailRemote(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val startDate: String,
    val endDate: String,
    val schedule: String,
    val points: List<TouristPointRemote>
)