package com.itanes.appturismo

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.itanes.appturismo.utils.SyncDataWorker
import data.local.AppDatabase
import data.remote.RetrofitInstance
import data.repository.TourRepository
import utils.CustomWorkerFactory


class AppTurismoApp : Application()/*, Configuration.Provider*/ {

    lateinit var repository: TourRepository
        private set

    override fun onCreate() {
        super.onCreate()
        try{
            val database = AppDatabase.getInstance(this)
            repository = TourRepository(
                database.tourDao(),
                database.touristPointDao(),
                database.favoriteDao(),
                RetrofitInstance.api
            )
        }catch(e: Exception){
            Log.e("LogError_AppTurismoApp","Error inicializando: ${e.message}")
            e.printStackTrace()
        }


        // Programar sincronización   Probar comentar luego
        SyncDataWorker.schedule(this)
    }

    /*override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(CustomWorkerFactory(repository))
            .build()*/
}