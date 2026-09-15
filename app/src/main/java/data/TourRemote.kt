package data
import com.google.gson.annotations.SerializedName
data class TourRemote(
    val id: Int,
    val name: String,
    val description: String?,
    val image: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("ends_date") val endDate: String?,
    val schedule: String?,
    val points: List<TouristPointRemote> = emptyList()
)