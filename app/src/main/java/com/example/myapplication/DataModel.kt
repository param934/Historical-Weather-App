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
    fun parseJsonToHistoricalWeatherAndInsert(jsonWeather: JSONObject) {
        val location = jsonWeather.getString("location")
        val date = jsonWeather.getString("date")
        val temperature_2m_max = jsonArrayToList(jsonWeather.getJSONArray("temperature_2m_max")).map { it.toDouble() }
        val temperature_2m_min = jsonArrayToList(jsonWeather.getJSONArray("temperature_2m_min")).map { it.toDouble() }
        val sunrise = jsonArrayToList(jsonWeather.getJSONArray("sunrise"))
        val sunset = jsonArrayToList(jsonWeather.getJSONArray("sunset"))
        val precipitation_sum = jsonArrayToList(jsonWeather.getJSONArray("precipitation_sum")).map { it.toFloat() }
        val shortwave_radiation_sum = jsonArrayToList(jsonWeather.getJSONArray("shortwave_radiation_sum")).map { it.toFloat() }


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
    fun fetchWeatherData(apiUrl: String, apiParameters: Map<String, Any>) {
       _weatherData.value= Resource.Loading("Loading Started")
        viewModelScope.launch {
            try {
                val response = fetchWeatherFromApi(apiUrl, apiParameters)
                    val jsonObject = JSONObject(response)
                    println(jsonObject)
                    if(response.isNotEmpty()){
                        _weatherData.postValue(Resource.Success(jsonObject))
                    }else{
                        _weatherData.postValue(Resource.Error("Failed to fetch"))
                    }
            } catch (e: IOException) {
                // Handle network error
                _weatherData.postValue( Resource.Error("Error fetching weather data: $e"))
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
