package com.example.weatherforecast.favlocations.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherforecast.FavApiState
import com.example.weatherforecast.MainRule
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.repo.FakeRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavLocationsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule = MainRule()

    lateinit var favViewModel: FavLocationsViewModel
    lateinit var viewModel: MapsViewModel
    lateinit var repo: FakeRepo

    private val favWeather = FavWeather(30.3030, 33.3333, "Cairo")
    private val favWeather2 = FavWeather(32.3030, 32.3333, "Alex")

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        repo = FakeRepo()
        favViewModel = FavLocationsViewModel(repo)
        viewModel = MapsViewModel((repo))
    }

    @Test
    fun getFavLocationsDB_affectInStateFlow() {
        viewModel.insertFavLocation(favWeather)
        val value = favViewModel.stateFlow.value
        value as FavApiState.Success
        assertThat(value.favData.size, `is`(1))
        assertThat(value.favData.get(0).timezone,`is`(favWeather.timezone))
    }

    @Test
    fun deleteOneFavLocation_afterTwoInsertion_returnSizeEqualOne() {
        //given insert two fav locations
        viewModel.insertFavLocation(favWeather)
        viewModel.insertFavLocation(favWeather2)
        //when delete one
        favViewModel.deleteFavLocation(favWeather)
        //then size = 1
        val value = favViewModel.stateFlow.value
        value as FavApiState.Success
        assertThat(value.favData.size, `is`(1))
        assertThat(value.favData[0].timezone,`is`(favWeather2.timezone))

    }

    @Test
    fun insertOneFavLocation_sizeInStateFlowEqualOne() {
        var value = favViewModel.stateFlow.value
        value as FavApiState.Success
        assertThat(value.favData.size, `is`(0))
        //when insert one
        viewModel.insertFavLocation(favWeather)
        //then the size = 1
        value = favViewModel.stateFlow.value
        value as FavApiState.Success
        assertThat(value.favData.size, `is`(1))
    }

}