package data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Long = 0,
    val title: String,
    val content: String,
    val dateKey: String,   // Formato "YYYY-MM-DD" para asociar al calendario
    val tourId: Int?,      // Opcional: si la nota está vinculada a un tour
    val createdAt: Long,
    val updatedAt: Long
)