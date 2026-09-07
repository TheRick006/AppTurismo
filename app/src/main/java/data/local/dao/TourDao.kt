package com.itanes.appturismo.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.itanes.appturismo.data.local.entity.TouristPoint
import data.local.entity.Tour

import kotlinx.coroutines.flow.Flow

@Dao
interface TourDao {

    @Query("SELECT * FROM tours WHERE tourId = :tourId")
    suspend fun getTourByIdSimple(tourId: Int): Tour?

    @Query("SELECT * FROM tourist_points WHERE tourId = :tourId")
    fun getPointsByTourId(tourId: Int): Flow<List<TouristPoint>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tours: List<Tour>)//: Unit

    @Query("SELECT * FROM tours")
    fun getAll(): Flow<List<Tour>>

    @Transaction
    @Query("SELECT * FROM tours WHERE tourId = :tourId")
    fun getTourById(tourId: Int): Flow<TourWithPoints>
}

data class TourWithPoints(
    @Embedded val tour: Tour,
    @Relation(
        parentColumn = "tourId",
        entityColumn = "tourId"
    )
    val points: List<TouristPoint>
)