package com.example.myapplication
import org.json.JSONArray
import org.json.JSONObject

class WeatherRepository(private val weatherDao: HistoricalWeatherDao) {
    suspend fun insertWeather(historicalWeather: HistoricalWeather) {
        weatherDao.insertWeather(historicalWeather)
    }

   suspend fun getAllWeather(): List<HistoricalWeather> {
        return weatherDao.getAllWeatherStored()
    }

    suspend fun deleteAllWeatherData() {
        weatherDao.deleteAllWeatherData()
    }

    suspend fun getWeatherById(id: Long): HistoricalWeather? {
        return weatherDao.getWeatherById(id)
    }
}
