package com.example.weatherforecast.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.WeatherResponse
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Ignore


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalSourceTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule= InstantTaskExecutorRule()

    lateinit var database: AppDataBase
    lateinit var localDataSource: ConcreteLocalSource

    private val current= Current(1,1.2,1,2.1,1,1,1,1,
        2.2,2.3,1, listOf(),1,2.4,2.5)

    private val weather: WeatherResponse = WeatherResponse(current, listOf(), listOf(), listOf(),
        1.1,1.2,"",1)
    private val favWeather = FavWeather(5.0, 6.0, "fav1")
    private val alert=AlertModel("1","","","","")
    private val alert2=AlertModel("2","","","","")

    @Before
    fun setUp(){
        database=
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDataBase::class.java)
            .allowMainThreadQueries().build()

        localDataSource= ConcreteLocalSource(database.currentWeatherDao())
    }

    @After
    fun closeDataBase()= database.close()

    @Test
    fun insertCurrentWeather_returnValueNotNull()= runBlockingTest {
        launch {
            localDataSource.getCurrentWeather().collect{
                TestCase.assertNull(it)
                cancel()
            }
        }
        //when
        localDataSource.insertCurrentWeather(weather)

        //then
        launch {
            localDataSource.getCurrentWeather().collect{
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }

    @Test
    fun getCurrentWeather_returnNotNull()= runBlockingTest {
        localDataSource.insertCurrentWeather(weather)
        launch {
            localDataSource.getCurrentWeather().collect {

                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteCurrentWeather_returnNull() = runBlockingTest {
        //given
        localDataSource.insertCurrentWeather(weather)
        launch {
            localDataSource.getCurrentWeather().collect {
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
        //when
        localDataSource.deleteCurrentWeather()
        //then
        launch {
            localDataSource.getCurrentWeather().collect {
                TestCase.assertNull(it)
                cancel()
            }
        }
    }


    @Test
    fun deleteOneFavLocation_afterInsertOneItem_returnSizeZero()= runBlockingTest {
        localDataSource.insertFavLocation(favWeather)
        var fav:FavWeather?=null

        launch {
            localDataSource.getFavLocations().collect {
                fav=it[0]
                cancel()
            }
        }
        //when delete fav location
        localDataSource.deleteFavLocation(fav!!)

        //then size = 0 is returned
        launch {
            localDataSource.getFavLocations().collect {
                assertThat(it.size,`is`(0) )
                cancel()
            }
        }
    }

    @Test
    fun insertOneFavLocation_sizeIncreasedByOne() = runBlockingTest {
        var size=0
        launch {
            localDataSource.getFavLocations().collect{
                size=it.size
                cancel()
            }
        }
        //when insert new fav weather
        localDataSource.insertFavLocation(favWeather)

        //then size increased by one
        launch {
            localDataSource.getFavLocations().collect{
                assertThat(it.size, `is`(size+1))
                cancel()
            }
        }
    }

    @Test
    fun getFavLocations_afterFourInsertion_returnSizeIs4()= runBlockingTest{
        localDataSource.insertFavLocation(favWeather)
        localDataSource.insertFavLocation(favWeather)
        localDataSource.insertFavLocation(favWeather)
        localDataSource.insertFavLocation(favWeather)

        //when get fav location from db
        launch {
            localDataSource.getFavLocations().collect{
                //then return size = 4
                assertThat(it.size,`is`(4))
                cancel()
            }
        }
    }


    @Test
    fun insertOneAlert_returnSizeEqualOne()= runBlockingTest {
        launch {
            localDataSource.getAllAlerts().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }
        //when insert one alert
        localDataSource.insertAlert(alert)

        //then size =1
        launch {
            localDataSource.getAllAlerts().collect {
                assertThat(it.size,`is`(1))
                cancel()
            }
        }
    }

    @Test
    fun getAllAlerts_afterInsertTwo_returnSize2()= runBlockingTest {
        localDataSource.insertAlert(alert)
        localDataSource.insertAlert(alert2)

        launch {
            //when get alerts from db
            localDataSource.getAllAlerts().collect {
                //then size =2
                assertThat(it.size,`is`(2))
                cancel()
            }
        }
    }
    @Test
    fun deleteOneAlert_afterOneInsertion_returnSizeEqual0()= runBlockingTest{
        //given insert one alert
        localDataSource.insertAlert(alert)

        launch {
            localDataSource.getAllAlerts().collect {
                assertThat(it.size,`is`(1))
                cancel()
            }
        }
        //when delete one alert
        localDataSource.deleteAlert(alert)

        //then size =0
        launch {
            localDataSource.getAllAlerts().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }
    }

}