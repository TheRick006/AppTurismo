package com.itanes.appturismo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itanes.appturismo.data.local.entity.TouristPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface TouristPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<TouristPoint>)

    @Query("SELECT * FROM tourist_points WHERE tourId = :tourId")
    fun getPointsForTour(tourId: Int): Flow<List<TouristPoint>>

    @Query("SELECT * FROM tourist_points WHERE touristPointId = :pointId")
    suspend fun getPointById(pointId: Int): TouristPoint?

    @Query("""
        SELECT tp.* FROM tourist_points tp
        INNER JOIN favorites f ON tp.touristPointId = f.touristPointId
    """)
    fun getFavoritePoints(): Flow<List<TouristPoint>>
}