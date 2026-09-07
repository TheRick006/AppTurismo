package utils

import data.ToursResponse
import retrofit2.Response
import retrofit2.http.GET

interface TourApiService {
    @GET("api/tours")
    suspend fun getAllTours(): Response<ToursResponse>
}