package data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tours")
data class Tour(
    @PrimaryKey val tourId: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val startDate: String,
    val endDate: String,
    val schedule: String,
    val updatedAt: Long
)