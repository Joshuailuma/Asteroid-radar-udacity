package com.udacity.asteroidradar.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.ApiService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * For caching of data
 * */
class AsteroidRepository(private val database: AsteroidDao.AsteroidDatabase) {

    // Get current time of the device
    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().plusDays(7)

    // We want the UI to have a list of asteroid, not network videos or database videos
    // Transform database asteroids to asteroids.
    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsDay(startDate.format(DateTimeFormatter.ISO_DATE))) {
            it.asDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsDate(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asDomainModel()
        }

    // To update the offline cache
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                //  Get the data from the network and then put it in the database
                //  make a network call to get asteriods, and use the await() function to tell the
                //  coroutine to suspend until the data is available
                val asteroids = ApiService.AsteroidApi.retrofitService.getAsteroids(API_KEY)
                // Parse the data
                val result = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(*result.asDatabaseModel())
                Timber.d("Success by timber")
            } catch (err: Exception) {
                Timber.e("$err.message.toString() timber")
            }
        }
    }
}