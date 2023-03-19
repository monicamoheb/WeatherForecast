//package com.example.weatherforecast.db
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room.databaseBuilder
//import androidx.room.RoomDatabase
//import com.example.weatherforecast.model.WeatherResponse
//
//
//@Database(entities = [WeatherResponse::class], version = 1)
//abstract class FavDataBase : RoomDatabase() {
//
//    abstract fun favLocationsDao(): FavLocationsDao
//
//    companion object {
//        private var instance: FavDataBase? = null
//        @Synchronized
//        fun getInstance(context: Context): FavDataBase?{
//            if (instance == null) {
//                instance = databaseBuilder(
//                    context.applicationContext,
//                    FavDataBase::class.java, "products"
//                ).build()
//            }
//            return instance
//        }
//    }
//}