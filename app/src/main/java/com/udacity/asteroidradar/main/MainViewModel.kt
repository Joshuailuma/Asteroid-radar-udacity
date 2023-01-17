package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.ApiService
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


/**
 * DevByteViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
enum class Filter{
    TODAY_ASTEROIDS,
    WEEK_ASTEROIDS,
    SAVED_ASTEROIDS
}
@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Define a database variable and assign it to getDatabase(), passing the application.
    private val database = getDatabase(application)

//     var asteroids: LiveData<List<Asteroid>>? = null

    // create your repository..
    // of val asteroidRepository and assign it to a Asteroid using the database singleton.
    private val asteroidsRepository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()

    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    // Refresh the asteroids using the repository.
    //Create an init block and launch a coroutine to call videosRepository.refreshVideos().
    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
            refreshPictureOfDay()
        }
    }

    // For navigating to details screen
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()

    val navigateToSelectedAsteroid: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    // Pass an asteroid to the next screen
    fun displayAsteroidDetails(asteroid: Asteroid){
        _navigateToSelectedAsteroid.value = asteroid
    }

    // set the asteroid to null after the navigation is completed to prevent..
    // unwanted extra navigations:
    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

   private suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                _pictureOfDay.postValue(ApiService.AsteroidApi.retrofitService.getPictureOfTheDay(Constants.API_KEY))

            } catch (err: Exception) {
                Timber.e(err.printStackTrace().toString())
            }
        }
    }

    // Filter the asteroids accordingly
    private var _filter = MutableLiveData(Filter.SAVED_ASTEROIDS)

    @RequiresApi(Build.VERSION_CODES.O)
    var  asteroids = switchMap(_filter) {
        when (it!!) {
            Filter.WEEK_ASTEROIDS -> asteroidsRepository.weekAsteroids
            Filter.TODAY_ASTEROIDS -> asteroidsRepository.todayAsteroids
            else -> asteroidsRepository.allAsteroids
        }
    }

    // This will take a value from main thread
    fun changeFilter(filter: Filter) {
        _filter.postValue(filter)
    }

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
