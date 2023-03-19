package com.example.weatherforecast.repo

//import com.example.weatherforecast.db.FavLocalSource
import com.example.weatherforecast.network.LocationClient
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

//class Repo private constructor(var remoteSource:LocationClient ,var localSource: FavLocalSource) :RepoInterface{
//
//    companion object{
//        private var instance:Repo?=null
//        fun getInstance(remoteSource: LocationClient,localSource: FavLocalSource):Repo{
//            return instance?: synchronized(this){
//                val temp=Repo(remoteSource,localSource)
//                instance=temp
//                temp
//            }
//        }
//    }
//
//    override suspend fun getLocation(lat: String, lon: String): Response<WeatherResponse> {
//        return remoteSource.getLocationOnline(lat, lon)
//    }
//
//    override suspend fun insertFavLocation(weatherResponse: WeatherResponse) {
//        localSource.insertLocation(weatherResponse)
//    }
//
//    override suspend fun deleteFavLocation(weatherResponse: WeatherResponse) {
//        localSource.deleteLocation(weatherResponse)
//    }
//
//    override suspend fun getAllFavLocation(): Flow<List<WeatherResponse>> {
//        return localSource.getAllFavLocations()
//    }
//
//
//}