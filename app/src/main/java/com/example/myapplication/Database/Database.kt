package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.assi2.Database.Converters

@Database(entities = [HistoricalWeather::class], version = 1, exportSchema = false)
@TypeConverters(
    Converters.ListStringConverter::class,Converters.ListDoubleConverter::class,
    Converters.ListFloatConverter::class, Converters.ListIntConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): HistoricalWeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
