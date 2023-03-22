package com.example.weatherforecast.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface FavLocationsDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertLocation(address: String)
//
//    @Query("select * from WeatherTable")
//    fun getFavLocations(): Flow<List<String>>
//
//    @Delete
//    suspend fun deleteLocation(address: String)

    ///////////
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun saveAlert(alertTable: AlertTable):Long
//
//    @Query("SELECT * FROM AlertTable")
//    fun getAllAlerts():Flow<List<AlertTable>>
//
//    @Query("DELETE FROM AlertTable WHERE id Like:id")
//    fun deleteAlert(id: Long)

}