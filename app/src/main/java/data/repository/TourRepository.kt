package data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import data.TourRemote
import data.TouristPointRemote
import data.local.entity.Favorite
import data.local.dao.FavoriteDao
import data.local.entity.Tour
import com.itanes.appturismo.data.local.dao.TourDao
import data.local.entity.TourWithPoints
import com.itanes.appturismo.data.local.entity.TouristPoint
import com.itanes.appturismo.data.local.dao.TouristPointDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import utils.TourApiService

class TourRepository {
    private val tourDao: TourDao
    private val touristPointDao: TouristPointDao
    private val favoriteDao: FavoriteDao
    private val apiService: TourApiService

    constructor(
        tourDao: TourDao,
        touristPointDao: TouristPointDao,
        favoriteDao: FavoriteDao,
        apiService: TourApiService
    ) {
        this.tourDao = tourDao
        this.touristPointDao = touristPointDao
        this.favoriteDao = favoriteDao
        this.apiService = apiService
    }

    fun getAllToursFlow(): Flow<List<Tour>> = flow {
        // Emitir datos locales primero
        emitAll(tourDao.getAll())

        // Sincronizar en segundo plano
        try {
            val response = apiService.getAllTours()
            if (response.isSuccessful) {
                response.body()?.let { toursResponse ->
                    val tours = toursResponse.tours.map { it.toEntity() }
                    val points = toursResponse.tours.flatMap { tour ->
                        tour.points.map { it.toEntity(tour.id) }
                    }

                    tourDao.insertAll(tours)
                    touristPointDao.insertAll(points)

                    // Emitir datos actualizados
                    emitAll(tourDao.getAll())
                }
            }
        } catch (e: Exception) {
            // Silencioso - mantener datos locales
        }
    }

    fun getTourWithPointsFlow(tourId: Int): Flow<TourWithPoints> = flow {
        val tour = tourDao.getTourByIdSimple(tourId)
        Log.d("TourRepository", "Tour encontrado: $tour")

        if (tour != null) {
            val points = tourDao.getPointsByTourId(tourId).first()
            Log.d("TourRepository", "Puntos encontrados: ${points.size}")

            emit(TourWithPoints(tour, points))
        } else {
            Log.e("TourRepository", "Tour no encontrado")
        }
    }

    suspend fun toggleFavorite(pointId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.insert(Favorite(pointId))
        } else {
            favoriteDao.delete(pointId)
        }
    }

    fun isFavoriteLiveData(pointId: Int): LiveData<Boolean> =
        favoriteDao.isFavorite(pointId).asLiveData()
    suspend fun getPointById(pointId: Int): TouristPoint? =
        touristPointDao.getPointById(pointId)
    fun getFavoritePointsFlow(): Flow<List<TouristPoint>> =
        touristPointDao.getFavoritePoints()//  Extensiones de mapeo abajo, deberian ir en utils??
    fun TourRemote.toEntity(): Tour = Tour(
        tourId = id,
        name = name,
        description = description,
        imageUrl = image,
        updatedAt = System.currentTimeMillis()
    )

    fun TouristPointRemote.toEntity(tourId: Int): TouristPoint = TouristPoint(
        touristPointId = id,
        tourId = tourId,
        name = name,
        description = description,
        latitude = lat,
        longitude = lng,
        imageUrls = Gson().toJson(images)
    )

}