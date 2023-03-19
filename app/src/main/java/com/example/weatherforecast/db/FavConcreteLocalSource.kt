package com.example.weatherforecast.db
//
//import kotlinx.coroutines.flow.Flow
//import android.content.Context
//
//class FavConcreteLocalSource(context: Context) :FavLocalSource{
//    private val favLocationsDao : FavLocationsDao by lazy {
//        val db : FavDataBase = FavDataBase.getInstance(context) as FavDataBase
//        db.favLocationsDao()
//    }
//
//    override suspend fun insertLocation(address: String) {
//        favLocationsDao.insertLocation(address)
//    }
//
//    override suspend fun getAllFavLocations(): Flow<List<String>> {
//        return favLocationsDao.getFavLocations()
//    }
//
//    override suspend fun deleteLocation(address: String) {
//        favLocationsDao.deleteLocation(address)
//
//    }
//
//}