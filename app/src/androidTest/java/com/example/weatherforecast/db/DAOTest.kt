package com.example.weatherforecast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
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

    private lateinit var database: AppDataBase
    private val current=Current(1,1.2,1,2.1,1,1,1,1,
        2.2,2.3,1, listOf(),1,2.4,2.5)

    private val weather: WeatherResponse = WeatherResponse(current, listOf(), listOf(), listOf(),
        1.1,1.2,"",1)
    private val favWeather = FavWeather(5.0, 6.0, "fav1")
    private val alert=AlertModel("1","","","","")
    private val alert2=AlertModel("2","","","","")


    @Before
    fun createDataBase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDataBase() = database.close()


    @Test
    fun insertCurrentWeather()= runBlockingTest {
        launch {
            database.currentWeatherDao().getCurrentWeather().collect{
                assertNull(it)
                cancel()
            }
        }
        database.currentWeatherDao().insertCurrentWeather(weather)

        //When
        launch {
            database.currentWeatherDao().getCurrentWeather().collect{
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }

    }

    @Test
    fun getCurrentWeather_returnNotNull()= runBlockingTest {
        database.currentWeatherDao().insertCurrentWeather(weather)
        launch {
            database.currentWeatherDao().getCurrentWeather().collect {

                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteCurrentWeather_returnNull() = runBlockingTest {

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


    @Test
    fun deleteFavLocation_afterInsertOneItem_returnSizeZero()= runBlockingTest {
        database.currentWeatherDao().insertFavLocation(favWeather)
        var fav:FavWeather?=null

        launch {
            database.currentWeatherDao().getFavLocations().collect {
                fav=it[0]
                cancel()
            }
        }
        database.currentWeatherDao().deleteFavLocation(fav!!)

        launch {
            database.currentWeatherDao().getFavLocations().collect {
                assertThat(it.size,`is`(0) )
                cancel()
            }
        }
    }

    @Test
    fun insertFavLocation_sizeIncreasedByOne() = runBlockingTest {
        var size=0
        launch {
            database.currentWeatherDao().getFavLocations().collect{
                size=it.size
                cancel()
            }
        }

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
    fun getFavLocations_returnSizeIs4()= runBlockingTest{
        database.currentWeatherDao().insertFavLocation(favWeather)
        database.currentWeatherDao().insertFavLocation(favWeather)
        database.currentWeatherDao().insertFavLocation(favWeather)
        database.currentWeatherDao().insertFavLocation(favWeather)

        launch {
            database.currentWeatherDao().getFavLocations().collect{
                assertThat(it.size,`is`(4))
                cancel()
            }
        }
    }


    @Test
    fun insertAlert()= runBlockingTest {
        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }

        database.currentWeatherDao().insertAlert(alert)

        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(1))
                cancel()
            }
        }
    }
    @Test
    fun getAllAlerts()= runBlockingTest {
        database.currentWeatherDao().insertAlert(alert)
        database.currentWeatherDao().insertAlert(alert2)

        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(2))
                cancel()
            }
        }
    }
    @Test
    fun deleteAlert()= runBlockingTest{
        database.currentWeatherDao().insertAlert(alert)

        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(1))
                cancel()
            }
        }
        database.currentWeatherDao().deleteAlert(alert)

        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }
    }


}