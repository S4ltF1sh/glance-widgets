package com.s4ltf1sh.glance_widgets.widget.widget.quotes

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.s4ltf1sh.glance_widgets.network.PicsumService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.work.ListenableWorker.Result
import java.util.concurrent.TimeUnit

@HiltWorker
class QuotesWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val picsumService: PicsumService,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return Result.success()
    }

    companion object {
        private val uniqueWorkName = QuotesWidgetWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val workManager = WorkManager.getInstance(context)
            val request =
                PeriodicWorkRequestBuilder<QuotesWidgetWorker>(15, TimeUnit.MINUTES).build()

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = uniqueWorkName,
                existingPeriodicWorkPolicy = if (force) {
                    ExistingPeriodicWorkPolicy.UPDATE
                } else {
                    ExistingPeriodicWorkPolicy.KEEP
                },
                request = request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }


}