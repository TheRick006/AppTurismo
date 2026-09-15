package com.itanes.appturismo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import data.local.entity.Tour

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ocultando la barra de notificaciones y navegacion
        val windowsIntentsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        windowsIntentsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        windowsIntentsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "MainActivity creada")

        setupToolbar()
        setupNavigation()
        insertTestData()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)

    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar destinos principales
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.tourListFragment,
                R.id.favoritesFragment,
                R.id.calendarFragment,
            ),
            drawerLayout
        )

        // Vincular toolbar con NavController
        // Vincular NavigationView con NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        navigationView.setupWithNavController(navController)



        // Manejar clics en el menú
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_tours -> {
                    navController.navigate(R.id.tourListFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.menu_favorites -> {
                    navController.navigate(R.id.favoritesFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.menu_settings -> {
                    // Navegar a ajustes (implementar después)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.menu_notes -> {
                    // Navegar a notas (implementar después)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.menu_calendar -> {
                    navController.navigate(R.id.calendarFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun insertTestData() {
        Tour(
            tourId = 1,
            name = "Tour Centro Histórico",
            description = "Recorre los lugares más emblemáticos",
            imageUrl = "https://picsum.photos/400/300?random=1",
            startDate = "2026-10-15 09:00:00+00",
            endDate = "2026-10-15 14:00:00+00",
            schedule = "09:00 - 14:00",
            updatedAt = System.currentTimeMillis()
        )
    }
}