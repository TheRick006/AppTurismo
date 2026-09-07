package com.itanes.appturismo.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.Constraints
import com.itanes.appturismo.AppTurismoApp
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class SyncDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        val app = applicationContext as AppTurismoApp
        app.repository.getAllToursFlow().first()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncDataWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "sync_tours",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}