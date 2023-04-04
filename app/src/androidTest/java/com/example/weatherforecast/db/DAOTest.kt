package com.example.weatherforecast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DAOTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var database: AppDataBase
    val current=Current(1,1.2,1,2.1,1,1,1,1,2.2,2.3,1, listOf(),1,2.4,2.5)

    val weather: WeatherResponse = WeatherResponse(current, listOf(), listOf(), listOf(),1.1,1.2,"",1)


    @Before
    fun createDataBase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).build()
    }

    @After
    fun closeDataBase() = database.close()

    @Test
    fun insertFavLocation_sizeIncreasedByOne() = runBlockingTest {
        var size=0
        launch {
            database.currentWeatherDao().getFavLocations().collect{
                size=it.size
                cancel()
            }
        }
        val favWeather = FavWeather(5.0, 6.0, "fav1")
        database.currentWeatherDao().insertFavLocation(favWeather)

        //When
        launch {
            database.currentWeatherDao().getFavLocations().collect{
                assertThat(it.size, `is`(size+1))
                cancel()
            }
        }
    }

    @Test
    fun getCurrentWeather()= runBlockingTest {
        database.currentWeatherDao().insertCurrentWeather(weather)
        launch {
            database.currentWeatherDao().getCurrentWeather().collect {

                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteCurrentWeather() = runBlockingTest {

        database.currentWeatherDao().insertCurrentWeather(weather)
        launch {
            database.currentWeatherDao().getCurrentWeather().collect {
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
        database.currentWeatherDao().deleteCurrentWeather()
        launch {
            database.currentWeatherDao().getCurrentWeather().collect {
                assertNull(it)
                cancel()
            }
        }

    }

}