package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface HistoricalWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: HistoricalWeather)

    @Query("SELECT * FROM historical_weather")
    suspend fun getAllWeatherStored(): List<HistoricalWeather>

    @Query("DELETE FROM historical_weather")
    suspend fun deleteAllWeatherData()

    @Query("SELECT * FROM historical_weather WHERE id = :id")
    suspend fun getWeatherById(id: Long): HistoricalWeather?
}

