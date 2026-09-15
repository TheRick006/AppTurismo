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
import data.remote.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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
        CoroutineScope(Dispatchers.IO).launch {
            syncFromApi()
        }
        emitAll(tourDao.getAll())
    }
    private suspend fun syncFromApi() {
        try {
            //Log.d("TourRepository", "Iniciando sincronización con API")

            val response = apiService.getAllTours()
            //Log.d("TourRepository", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { toursResponse ->
                    //Log.d("TourRepository", "Tours recibidos: ${toursResponse.tours.size}")

                    val tours = toursResponse.tours.map { it.toEntity() }
                    val points = toursResponse.tours.flatMap { tour ->
                        tour.points.map { it.toEntity(tour.id) }
                    }

                    //Log.d("TourRepository", "Insertando ${tours.size} tours y ${points.size} puntos")
                    tourDao.insertAll(tours)
                    //Log.d("TourRepository", "Insertados. Verificando...")
                    val count = tourDao.count()
                    //Log.d("TourRepository", "Total en BD: $count")
                    touristPointDao.insertAll(points)
                    //Log.d("TourRepository", "Sincronización completada")
                }
            } else {
                Log.e("TourRepository", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("TourRepository", "Excepción: ${e.message}", e)
        }
    }

   /* suspend fun getToursByMonth(month: String): List<Tour> {
        return try {
            val response = apiService.getToursByMonth(month)
            if (response.isSuccessful) {
                response.body()?.tours?.map { it.toEntity() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }*/

    fun getToursByMonthFlow(month: String): Flow<List<Tour>> =
        tourDao.getToursByMonth(month)
    fun getTourWithPointsFlow(tourId: Int): Flow<TourWithPoints> = flow {
        val tour = tourDao.getTourByIdSimple(tourId)
        //Log.d("TourRepository", "Tour encontrado: $tour")

        if (tour != null) {
            val points = tourDao.getPointsByTourId(tourId).first()
            //Log.d("TourRepository", "Puntos encontrados: ${points.size}")

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
        description = description ?:"isNull/notProvided",
        imageUrl = image ?:"isNull/notProvided",
        startDate = startDate ?:"isNull/notProvided",
        endDate = endDate ?:"isNull/notProvided",
        schedule = schedule ?:"isNull/notProvided",
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