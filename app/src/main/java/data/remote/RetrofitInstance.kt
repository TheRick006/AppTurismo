package data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utils.TourApiService

object RetrofitInstance {
    private const val BASE_URL = "https://apitourism.vercel.app/api/"

    val api: TourApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TourApiService::class.java)
    }
}