package data.local.entity
import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["targetId", "type"]
)
data class Favorite(
    val targetId: Int,
    val type: String
) {
    companion object {
        const val TYPE_TOUR = "tour"
        const val TYPE_POINT = "point"
    }
}