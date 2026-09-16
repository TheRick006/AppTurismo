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
import utils.SettingsManager


class AppTurismoApp : Application()/*, Configuration.Provider*/ {

    lateinit var repository: TourRepository
    private set
    lateinit var settingsManager : SettingsManager
    private set
    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        settingsManager.applyTheme()
        //try{
            val database = AppDatabase.getInstance(this)
            repository = TourRepository(
                database.tourDao(),
                database.touristPointDao(),
                database.favoriteDao(),
                database.noteDao(),
                RetrofitInstance.api
            )
        //}catch(e: Exception){
          //  Log.e("LogError_AppTurismoApp","Error inicializando: ${e.message}")
        //    e.printStackTrace()
       // }


        // Programar sincronización   Probar comentar luego
        //SyncDataWorker.schedule(this)
    }

    /*override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(CustomWorkerFactory(repository))
            .build()*/
}