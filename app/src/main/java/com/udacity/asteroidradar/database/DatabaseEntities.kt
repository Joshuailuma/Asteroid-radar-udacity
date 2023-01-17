package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid

// Create the AsteroidData database object

@Entity(tableName = "asteroid_table")
data class AsteroidData constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val absoluteMagnitude: Double,
    val closeApproachDate: String,
    val relativeVelocity: Double,
    val estimatedDiameter: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<AsteroidData>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}
// Add an extension function which converts from Asteroid objects to database objects:

fun List<Asteroid>.asDatabaseModel(): Array<AsteroidData> {
    return map {
        AsteroidData(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}