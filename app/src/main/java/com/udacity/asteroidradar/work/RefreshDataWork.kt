package com.udacity.asteroidradar.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository


//Make app prefetch data in the background when app is not running


// WorkManager workers always extend a Worker class. We're going to use a..
// CoroutineWorker, because we want to use coroutines to handle our asynchronous..
// code and threading. Have RefreshDataWorker extend from the CoroutineWorker class
class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }

    // Do work make stuff to run in the background
    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        // What happens if its successful and if its not
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}