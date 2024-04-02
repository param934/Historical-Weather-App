package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historical_weather")
data class HistoricalWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
   val location: String,
    val date: String, // Date in yyyy-MM-dd format
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val precipitation_sum : List<Float>,
    val shortwave_radiation_sum:List<Float>
)
