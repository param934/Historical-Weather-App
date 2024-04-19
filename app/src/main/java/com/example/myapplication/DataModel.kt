package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Database.HistoricalWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

sealed class Resource<out T> {
    data class Loading(val message: String ) : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}
class WeatherViewModel( val weatherRepository: WeatherRepository) : ViewModel() {

    val allWeather: LiveData<List<HistoricalWeather>> = liveData {
        val data = weatherRepository.getAllWeather()
        emit(data)
    }
    private val _weatherData = MutableLiveData<Resource<JSONObject>>()

    val weatherData: LiveData<Resource<JSONObject>> = _weatherData
    val _dataFetched = mutableStateOf(false)

    // Function to set dataFetched to true
    fun setDataFetched() {
        _dataFetched.value = true
    }
    private fun jsonArrayToList(jsonArray: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }
         // Parse JSONObject to HistoricalWeather object
    fun parseJsonToHistoricalWeatherAndInsert(location: String,date: String, jsonWeather: JSONObject) {
//        val location = jsonWeather.getString("location")
//        val date = jsonWeather.getString("date")
        println(jsonWeather)
        val dailyData = jsonWeather.getJSONObject("daily")
        val temperature_2m_max = jsonArrayToList(dailyData.getJSONArray("temperature_2m_max")).map { it.toDouble() }
        val temperature_2m_min = jsonArrayToList(dailyData.getJSONArray("temperature_2m_min")).map { it.toDouble() }
        val sunrise = jsonArrayToList(dailyData.getJSONArray("sunrise"))
        val sunset = jsonArrayToList(dailyData.getJSONArray("sunset"))
        val precipitation_sum = jsonArrayToList(dailyData.getJSONArray("precipitation_sum")).map { it.toFloat() }
        val shortwave_radiation_sum = jsonArrayToList(dailyData.getJSONArray("shortwave_radiation_sum")).map { it.toFloat() }

             viewModelScope.launch{
            weatherRepository.insertWeather(
                HistoricalWeather(
                    location = location,
                    date = date,
                    temperature_2m_max = temperature_2m_max,
                    temperature_2m_min = temperature_2m_min,
                    sunrise = sunrise,
                    sunset = sunset,
                    precipitation_sum = precipitation_sum,
                    shortwave_radiation_sum = shortwave_radiation_sum
                )
            )
        }
    }
    fun fetchWeatherData(apiUrl: String, apiParameters: Map<String, Any?>) {
        _weatherData.value = Resource.Loading("Loading Started")
        viewModelScope.launch {
            try {
                // Ensure all values in apiParameters are Strings
                val stringParameters = apiParameters.mapValues { it.value.toString() }
                val response = fetchWeatherFromApi(apiUrl, stringParameters)
                val isFuture=false
                val jsonObject = JSONObject(response)
                println(jsonObject)
                if (response.isNotEmpty() && isFuture) {
                    // Calculate average of max and min temperatures over the last 10 years
                    val dailyData = jsonObject.getJSONArray("daily")
                    var totalMaxTemp = 0.0
                    var totalMinTemp = 0.0
                    for (i in 0 until dailyData.length()) {
                        val dayData = dailyData.getJSONObject(i)
                        totalMaxTemp += dayData.getDouble("temperature_2m_max")
                        totalMinTemp += dayData.getDouble("temperature_2m_min")
                    }
                    val avgMaxTemp = totalMaxTemp / dailyData.length()
                    val avgMinTemp = totalMinTemp / dailyData.length()

                    // Store averages in weather data
                    jsonObject.put("avg_max_temp_last_10_years", avgMaxTemp)
                    jsonObject.put("avg_min_temp_last_10_years", avgMinTemp)

                    _weatherData.postValue(Resource.Success(jsonObject))
                }
                if (response.isNotEmpty()) {
                    _weatherData.postValue(Resource.Success(jsonObject))
                } else {
                    _weatherData.postValue(Resource.Error("Failed to fetch"))
                }
            } catch (e: IOException) {
                // Handle network error
                _weatherData.postValue(Resource.Error("Error fetching weather data: $e"))
            } catch (e: Exception) {
                // Handle other errors
                _weatherData.postValue(Resource.Error("Error: $e"))
            }
        }
    }

    private suspend fun fetchWeatherFromApi(apiUrl: String, apiParameters: Map<String, String>): String {
        return withContext(Dispatchers.IO) {
            val urlBuilder = StringBuilder(apiUrl)
            if (apiParameters.isNotEmpty()) {
                urlBuilder.append(apiParameters.map { "${it.key}=${it.value}" }.joinToString("&"))
            }
            val url = URL(urlBuilder.toString())
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                return@withContext connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                throw IOException("HTTP error code: ${connection.responseCode}")
            }
        }
    }

}
