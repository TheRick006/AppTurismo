package data

data class TouristPointRemote(
    val id: Int,
    val name: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val images: List<String>
)