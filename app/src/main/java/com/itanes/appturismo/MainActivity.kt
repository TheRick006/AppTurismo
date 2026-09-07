package com.itanes.appturismo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import data.local.AppDatabase
import data.local.entity.Tour
import com.itanes.appturismo.data.local.entity.TouristPoint
import kotlinx.coroutines.launch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Log_MainActivity", "MainActivity creada")
        setContentView(R.layout.activity_main)
        Log.d("Log_MainActivity", "Layout cargado")

        // Verificar si NavHost existe
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        Log.d("Log_MainActivity", "NavHostFragment: $navHostFragment")

        if (navHostFragment == null) {
            Log.e("Log_MainActivity", "NavHostFragment es NULL")
        } else {
            val navController = navHostFragment.navController
            Log.d("Log_MainActivity", "NavController: $navController")
            Log.d("Log_MainActivity", "Current destination: ${navController.currentDestination}")
            Log.d("Log_MainActivity", "Graph: ${navController.graph}")
        }
        // Insertar datos de prueba en Room
        insertTestData()
    }

    private fun insertTestData() {
        lifecycleScope.launch {
            try {
                val database = AppDatabase.getInstance(this@MainActivity)
                val tourDao = database.tourDao()
                val pointDao = database.touristPointDao()

                // Crear tours de prueba
                val testTours = listOf(
                    Tour(
                        tourId = 1,
                        name = "Tour Centro Histórico",
                        description = "Recorre los lugares más emblemáticos del centro de la ciudad",
                        imageUrl = "https://picsum.photos/400/300",
                        updatedAt = System.currentTimeMillis()
                    ),
                    Tour(
                        tourId = 2,
                        name = "Tour Naturaleza",
                        description = "Descubre los parques y reservas naturales",
                        imageUrl = "https://picsum.photos/400/300",
                        updatedAt = System.currentTimeMillis()
                    ),
                    Tour(
                        tourId = 3,
                        name = "Tour Gastronómico",
                        description = "Prueba los mejores sabores locales",
                        imageUrl = "https://picsum.photos/400/300",
                        updatedAt = System.currentTimeMillis()
                    )
                )

                // Crear puntos turísticos de prueba
                val testPoints = listOf(
                    TouristPoint(
                        touristPointId = 101,
                        tourId = 1,
                        name = "Plaza Principal",
                        description = "La plaza más antigua de la ciudad, rodeada de edificios históricos",
                        latitude = 19.4326,
                        longitude = -99.1332,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    ),
                    TouristPoint(
                        touristPointId = 102,
                        tourId = 1,
                        name = "Catedral",
                        description = "Impresionante catedral del siglo XVI con arquitectura colonial",
                        latitude = 19.4342,
                        longitude = -99.1338,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    ),
                    TouristPoint(
                        touristPointId = 103,
                        tourId = 1,
                        name = "Museo de Arte",
                        description = "Colección de arte moderno y contemporáneo",
                        latitude = 19.4352,
                        longitude = -99.1412,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    ),
                    TouristPoint(
                        touristPointId = 201,
                        tourId = 2,
                        name = "Parque Nacional",
                        description = "Reserva natural con senderos y miradores",
                        latitude = 19.5023,
                        longitude = -99.2032,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    ),
                    TouristPoint(
                        touristPointId = 202,
                        tourId = 2,
                        name = "Lago Principal",
                        description = "Lago artificial con actividades acuáticas",
                        latitude = 19.4982,
                        longitude = -99.2012,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    ),
                    TouristPoint(
                        touristPointId = 301,
                        tourId = 3,
                        name = "Mercado Municipal",
                        description = "Productos frescos y comida típica local",
                        latitude = 19.4382,
                        longitude = -99.1522,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    ),
                    TouristPoint(
                        touristPointId = 302,
                        tourId = 3,
                        name = "Restaurante Tradicional",
                        description = "Cocina tradicional con recetas familiares",
                        latitude = 19.4412,
                        longitude = -99.1422,
                        imageUrls = "[\"https://picsum.photos/400/300\",\"https://picsum.photos/400/300\"]"
                    )
                )

                // Insertar en Room
                tourDao.insertAll(testTours)
                pointDao.insertAll(testPoints)

                Log.d("MainActivity", "Datos de prueba insertados: ${testTours.size} tours, ${testPoints.size} puntos")

                // Verificar que los datos se insertaron
                val toursCount = tourDao.getAll().let { flow ->
                    // No podemos usar .first() aquí porque getAll() retorna Flow
                    // Solo para verificación
                    null
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Error insertando datos: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}