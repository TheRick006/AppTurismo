package data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.itanes.appturismo.data.local.entity.TouristPoint

data class TourWithPoints(
    @Embedded val tour: Tour,
    @Relation(
        parentColumn = "tourId",
        entityColumn = "tourId"
    )
    val points: List<TouristPoint>
)