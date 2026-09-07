package utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.itanes.appturismo.utils.SyncDataWorker
import data.repository.TourRepository

class CustomWorkerFactory(
    private val repository: TourRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncDataWorker::class.java.name -> {
                SyncDataWorker(appContext, workerParameters)
            }
            else -> null
        }
    }
}