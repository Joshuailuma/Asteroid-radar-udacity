package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

// implement AsteroidDao to get, to store and retrieve stuff in the database
@Dao
interface AsteroidDao {
    // Get all asteroids from the table according to closestApproach
    @Query("SELECT * FROM asteroid_table ORDER BY closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<AsteroidData>>

    // Get all asteroids from today
    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate = :startDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDay(startDate: String): LiveData<List<AsteroidData>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDate(startDate: String, endDate: String): LiveData<List<AsteroidData>>

    // Simply Replace in case of double entry
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: AsteroidData)

    // Implement Database
    @Database(entities = [AsteroidData::class], version = 2)
    abstract class AsteroidDatabase : RoomDatabase() {
        abstract val asteroidDao: AsteroidDao
    }
}

// use the singleton pattern to get an instance of the database.
private lateinit var INSTANCE: AsteroidDao.AsteroidDatabase
fun getDatabase(context: Context): AsteroidDao.AsteroidDatabase {
    // Check whether the database has been initialized:
    // If not, initialize it

    synchronized(AsteroidDao.AsteroidDatabase::class.java) {

        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDao.AsteroidDatabase::class.java,
                "Weather"
            ).build()
        }
    }
    return INSTANCE
}