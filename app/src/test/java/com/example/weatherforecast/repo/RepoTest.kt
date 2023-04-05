package com.example.weatherforecast.repo

import android.util.Log
import androidx.test.core.app.ActivityScenario.launch
import com.example.weatherforecast.MainRule
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.math.log

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RepoTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainRule = MainRule()


    private val current = Current(
        1, 1.2, 1, 2.1, 1, 1, 1, 1,
        2.2, 2.3, 1, listOf(), 1, 2.4, 2.5
    )

    private val weather: WeatherResponse = WeatherResponse(
        current, listOf(), listOf(), listOf(),
        1.1, 1.2, "", 1
    )


    val fav1 = FavWeather(1.1, 1.2, "fav1")
    val fav2 = FavWeather(1.1, 1.2, "fav2")
    val fav3 = FavWeather(1.1, 1.2, "fav3")
    val fav4 = FavWeather(1.1, 1.2, "fav4")

    val alert1 = AlertModel("1", "start1", "end1", "hour1", "type1")
    val alert2 = AlertModel("2", "start2", "end2", "hour2", "type2")
    val alert3 = AlertModel("3", "start3", "end3", "hour3", "type3")
    val alert4 = AlertModel("4", "start4", "end4", "hour4", "type4")


    lateinit var fakeLocalDataSource: FakeLocalSource
    lateinit var fakeRemoteDataSource: FakeRemoteSource
    lateinit var repository: Repo
    var favList = mutableListOf(fav1, fav2)
    var alertList = mutableListOf(alert1, alert2)


    @Before
    fun setUp() {

        fakeLocalDataSource = FakeLocalSource(weather, favList, alertList)
        fakeRemoteDataSource = FakeRemoteSource(weather)

        repository = Repo.getInstance(
            fakeRemoteDataSource,
            fakeLocalDataSource,
            Dispatchers.Main
        )
    }

    @After
    fun tearDown() {
        favList = mutableListOf()
        alertList = mutableListOf()
    }

    @Test
    fun getCurrentWeatherOnline_returnTheSameObject() = mainRule.runBlockingTest {
        //when get online
        val result = repository.getCurrentWeatherOnline(
            weather.lat.toString(),
            weather.lon.toString(),
            "en",
            "standard"
        )
        //then the same object is retrived
        assertThat(result.toList().get(0).lat, `is`(weather.lat))
        assertThat(result.toList().get(0).lon, `is`(weather.lon))
    }

    @Test
    fun insertCurrentWeather_returnTheSameObject() = mainRule.runBlockingTest {
        repository.insertCurrentWeather(weather)
        launch {
            repository.getCurrentWeatherDB().collect {
                assertThat(it, `is`(weather))
            }
        }
    }

    @Test
    fun getCurrentWeatherDB_afterInsertOne_returnTheSameObject() = mainRule.runBlockingTest {
        repository.insertCurrentWeather(weather)
        launch {
            repository.getCurrentWeatherDB().collect {
                assertThat(it, `is`(weather))
            }
        }
    }

    @Test
    fun insertTwoFavLocation_sizeIncreasedByTwo() = mainRule.runBlockingTest {
        var size = 0
        repository.getFavLocations().collect {
            size = it.size

        }
        //when isert two fav location
        repository.insertFavLocation(fav1)
        repository.insertFavLocation(fav1)

        //when size increased by two
        repository.getFavLocations().collect {
            assertThat(it.size, `is`(size + 2))

        }
    }

    @Test
    fun getFavLocations_returnTheSameSize() = mainRule.runBlockingTest {
        repository.getFavLocations().collect {
            assertThat(it.size, `is`((favList.size)))
        }
    }

    @Test
    fun deleteOneFavLocation_afterInsertAndGetTheSize_sizeDecreasedByOne() = mainRule.runBlockingTest {
        repository.insertFavLocation(fav3)
        var size = 0
        repository.getFavLocations().collect {
            size = it.size
        }//when delete one fav location
        repository.deleteFavLocation(fav1)
        //then size decreased by one
        repository.getFavLocations().collect {
            assertThat(it.size, `is`(size - 1))
        }
    }

    @Test
    fun deleteOneAlert_afterInsertOneAndGetTheSize_sizeDecreasedByOne() = mainRule.runBlockingTest {
        repository.insertAlert(alert4)
        var size = 0
        repository.getAllAlerts().collect {
            size = it.size
        }
        //when delete alert
        repository.deleteAlert(alert4)
        //then size decreased
        repository.getAllAlerts().collect {
            assertThat(it.size, `is`(size - 1))
        }
    }

    @Test
    fun insertOneAlert_sizeIncreasedByOne() = mainRule.runBlockingTest {
        var size = 0
        repository.getAllAlerts().collect {
            size = it.size
        }
        repository.insertAlert(alert3)

        repository.getAllAlerts().collect {
            assertThat(it.size, `is`(size + 1))
        }
    }

    @Test
    fun getAllAlerts_returnTheSameSizeOfList() = mainRule.runBlockingTest {
        repository.getAllAlerts().collect {
            assertThat(it.size, `is`(alertList.size))
        }
    }

}