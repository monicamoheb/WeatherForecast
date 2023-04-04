package com.example.weatherforecast.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalSourceTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule= InstantTaskExecutorRule()

    lateinit var database: AppDataBase
    lateinit var localDataSource: ConcreteLocalSource
    lateinit var instrumentationContext: Context

    @Before
    fun setUp(){
        database=
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDataBase::class.java)
            .allowMainThreadQueries().build()

        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
        localDataSource= ConcreteLocalSource(instrumentationContext)
    }

    @After
    fun closeDataBase()= database.close()
}