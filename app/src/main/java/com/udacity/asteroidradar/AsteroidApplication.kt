

package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
class AsteroidApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    // It's important to note that WorkManager.initialize should be called from inside..
    // onCreate without using a background thread to avoid issues caused when ..
    // initialization happens after WorkManager is used.
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        delayedInit()
    }


    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .setRequiresCharging(true)
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setRequiresDeviceIdle(true)
            }
        }.build()

    // Create a setupRecurringWork() function. In it, define a repeatingRequest..
    // variable that uses a PeriodicWorkRequestBuilder to create a PeriodicWorkRequest..
    // for your RefreshDataWorker
    private fun setupRecurringWork() {
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        // Schedule work
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}
