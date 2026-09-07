package com.itanes.appturismo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "tourist_points")
data class TouristPoint(
    @PrimaryKey val touristPointId: Int,
    val tourId: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: String // JSON array
)
/*
fun TouristPoint.getImageList(): List<String> {
    return try {
        Gson().fromJson(imageUrls, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }
}*/
