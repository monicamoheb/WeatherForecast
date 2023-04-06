package com.example.weatherforecast.alerts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherforecast.AlertState
import com.example.weatherforecast.MainRule
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.repo.FakeRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AlertsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule= MainRule()

    lateinit var viewModel: AlertsViewModel
    lateinit var repo: FakeRepo
    var alert=AlertModel("id1","start1","end1","hour1","type1")
    var alert2=AlertModel("id2","start2","end2","hour2","type2")


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        repo=FakeRepo()
        viewModel= AlertsViewModel(repo)
    }

    @Test
    fun deleteOneAlert_afterInsertTwoAlerts_returnSizeEqualOne() {
        //given insert two alerts
        viewModel.insertAlert(alert)
        viewModel.insertAlert(alert2)

        var result=viewModel.stateFlow.value
        result as AlertState.Success
        assertThat(result.alertData.getOrNull(1),`is`(notNullValue()))

        //when delete first alert
        viewModel.deleteAlert(alert)

        //then size =1
         result=viewModel.stateFlow.value
        result as AlertState.Success
        assertThat(result.alertData.size,`is`(1))
        assertNull(result.alertData.getOrNull(1))
        assertThat(result.alertData[0].alertType,`is`(alert2.alertType))
    }

    @Test
    fun deleteOneAlert_afterInsertOneAlert_returnSizeEqualZero() {
        //given inset one alert
        viewModel.insertAlert(alert)
        //when delete this one
        viewModel.deleteAlert(alert)
        //then return size =0
        var result=viewModel.stateFlow.value
        result as AlertState.Success
        assertThat(result.alertData.size,`is`(0))
    }

    @Test
    fun insertOneAlert_sizeIncreasedByOne() {
        var result=viewModel.stateFlow.value
        result as AlertState.Success
        val size=result.alertData.size
        //whe onsert one alert
        viewModel.insertAlert(alert)
        //then size increased by 1
        result=viewModel.stateFlow.value
        result as AlertState.Success
        assertThat(result.alertData.size,`is`(size+1))

    }
}