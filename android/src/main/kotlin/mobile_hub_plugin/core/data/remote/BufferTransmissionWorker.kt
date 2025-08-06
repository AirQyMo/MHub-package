package br.pucrio.inf.lac.mobilehub.core.data.remote

import android.content.Context
import androidx.work.*
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.di.modules.factories.ChildWorkerFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import java.util.concurrent.TimeUnit

internal class BufferTransmissionWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val wlanTechnology: WLAN
) : Worker(context, params) {
    companion object {
        private const val TAG = "WorkManagerBuffer"

        private const val INTERVAL = 15.toLong()

        private const val ATTEMPTS = 5

        fun init(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val work = PeriodicWorkRequestBuilder<BufferTransmissionWorker>(INTERVAL, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, work)
        }

        fun cancel(context: Context) = WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
    }

    override fun doWork(): Result {
        return try {
            wlanTechnology.publishQueuedMessages()
            Result.success()
        } catch (ex: Exception) {
            if (runAttemptCount < ATTEMPTS) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    @AssistedInject.Factory
    internal interface Factory : ChildWorkerFactory
}

