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
    fun insertCurrentWeather_returnValueNotNull()= runBlockingTest {
        launch {
            database.currentWeatherDao().getCurrentWeather().collect{
                assertNull(it)
                cancel()
            }
        }
        //when
        database.currentWeatherDao().insertCurrentWeather(weather)

        //then
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
        //given
        database.currentWeatherDao().insertCurrentWeather(weather)
        launch {
            database.currentWeatherDao().getCurrentWeather().collect {
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
        //when
        database.currentWeatherDao().deleteCurrentWeather()
        //then
        launch {
            database.currentWeatherDao().getCurrentWeather().collect {
                assertNull(it)
                cancel()
            }
        }
    }


    @Test
    fun deleteOneFavLocation_afterInsertOneItem_returnSizeZero()= runBlockingTest {
        database.currentWeatherDao().insertFavLocation(favWeather)
        var fav:FavWeather?=null

        launch {
            database.currentWeatherDao().getFavLocations().collect {
                fav=it[0]
                cancel()
            }
        }
        //when delete fav location
        database.currentWeatherDao().deleteFavLocation(fav!!)

        //then size = 0 is returned
        launch {
            database.currentWeatherDao().getFavLocations().collect {
                assertThat(it.size,`is`(0) )
                cancel()
            }
        }
    }

    @Test
    fun insertOneFavLocation_sizeIncreasedByOne() = runBlockingTest {
        var size=0
        launch {
            database.currentWeatherDao().getFavLocations().collect{
                size=it.size
                cancel()
            }
        }
        //when insert new fav weather
        database.currentWeatherDao().insertFavLocation(favWeather)

        //then size increased by one
        launch {
            database.currentWeatherDao().getFavLocations().collect{
                assertThat(it.size, `is`(size+1))
                cancel()
            }
        }
    }

    @Test
    fun getFavLocations_afterFourInsertion_returnSizeIs4()= runBlockingTest{
        database.currentWeatherDao().insertFavLocation(favWeather)
        database.currentWeatherDao().insertFavLocation(favWeather)
        database.currentWeatherDao().insertFavLocation(favWeather)
        database.currentWeatherDao().insertFavLocation(favWeather)

        //when get fav location from db
        launch {
            database.currentWeatherDao().getFavLocations().collect{
                //then return size = 4
                assertThat(it.size,`is`(4))
                cancel()
            }
        }
    }


    @Test
    fun insertOneAlert_returnSizeEqualOne()= runBlockingTest {
        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }
        //when insert one alert
        database.currentWeatherDao().insertAlert(alert)

        //then size =1
        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(1))
                cancel()
            }
        }
    }

    @Test
    fun getAllAlerts_afterInsertTwo_returnSize2()= runBlockingTest {
        database.currentWeatherDao().insertAlert(alert)
        database.currentWeatherDao().insertAlert(alert2)

        launch {
            //when get alerts from db
            database.currentWeatherDao().getAllAlerts().collect {
                //then size =2
                assertThat(it.size,`is`(2))
                cancel()
            }
        }
    }
    @Test
    fun deleteOneAlert_afterOneInsertion_returnSizeEqual0()= runBlockingTest{
        //given insert one alert
        database.currentWeatherDao().insertAlert(alert)

        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(1))
                cancel()
            }
        }
        //when delete one alert
        database.currentWeatherDao().deleteAlert(alert)

        //then size =0
        launch {
            database.currentWeatherDao().getAllAlerts().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }
    }


}