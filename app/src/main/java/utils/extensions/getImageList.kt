package com.itanes.appturismo.utils.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itanes.appturismo.data.local.entity.TouristPoint

fun TouristPoint.getImageList(): List<String> {
    return try {
        val type = object : TypeToken<List<String>>() {}.type
        Gson().fromJson(imageUrls, type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}