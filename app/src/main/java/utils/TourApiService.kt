package utils

import data.TourDetailResponse
import data.ToursResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface TourApiService {
    @GET("tours/getAll")
    suspend fun getAllTours(): Response<ToursResponse>

    /*@GET
    suspend fun getAllToursTest(@Url url: String): Response<ToursResponse>*/
    @GET("tours/getById")
    suspend fun getTourById(@Path("id") tourId: Int): Response<TourDetailResponse>

    @GET("tours/getByMonth")
    suspend fun getToursByMonth(@Path("month") month: String): Response<ToursResponse>
}