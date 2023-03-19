//package com.example.weatherforecast.db
//
//import kotlinx.coroutines.flow.Flow
//
//interface FavLocalSource {
//    suspend fun insertLocation(address: String)
//    suspend fun getAllFavLocations(): Flow<List<String>>
//    suspend fun deleteLocation(address: String)
//}