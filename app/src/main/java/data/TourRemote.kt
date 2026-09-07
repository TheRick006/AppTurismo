package data

data class TourRemote(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val points: List<TouristPointRemote>
)