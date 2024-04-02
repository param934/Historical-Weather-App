@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePick(): MutableState<String> {
    var showDialog by remember { mutableStateOf(false) }
//    var location by remember { mutableStateOf("") }
    val dateState = rememberDatePickerState()
    val selectedDate = remember { mutableStateOf(Date()) } // Initialize selectedDate with the current date
    val dateToString = remember { mutableStateOf("Select Date") }

    FilledTonalButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape= RoundedCornerShape(4.dp),

    ) {
        Text(text = dateToString.value,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge)
    }
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateState.selectedDateMillis)
                        dateToString.value = formattedDate ?: "Choose Date"
                    }
                ) {
                    Text(text = "OK")
                }
            }
            ,
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                ) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = dateState,
                showModeToggle = true
            )
        }
    }
    return dateToString
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getGeolocation(context: Context): Address? {
    var location by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<Address?>(null)}
    val geocoder = Geocoder(context)
    val placeSuggestions = remember { mutableStateListOf<Address>() }
    var opendrop by remember{ mutableStateOf(false) }
    // Fetch location suggestions when the location text changes
    LaunchedEffect(location) {
        if (location.isNotBlank()) {
            try {
                val addresses = geocoder.getFromLocationName(location, 3)
                placeSuggestions.clear()
                addresses?.let { addressList ->
                    placeSuggestions.addAll(addressList)
                }
            } catch (e: IOException) {
                Log.e("Geolocation", "Error fetching location suggestions: ${e.message}", e)
            }
        } else {
            placeSuggestions.clear()
        }
    }

    Column {
        ExposedDropdownMenuBox(
            expanded =opendrop,
            modifier = Modifier.fillMaxWidth(),
            onExpandedChange = {
                !opendrop
            }
        ) {
            TextField(
                value = location,
                onValueChange = { newText ->
                    location = newText
                    if(placeSuggestions.isNotEmpty()){
                        opendrop = true }
                },
                label = { Text("Enter Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = opendrop,
                onDismissRequest = { opendrop=false},
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                placeSuggestions.forEach { suggestion ->
                    val uniqueParts = setOfNotNull(
                        suggestion.featureName,
                        suggestion.locality,
                        suggestion.countryName
                    )
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            val input = uniqueParts.joinToString(separator = " ")
                            Text(text = input) },
                        onClick = {
                            opendrop=false
                            selectedLocation =suggestion
                            location =
                                uniqueParts.joinToString(separator = " ") // Update the text field with the selected location
                        }
                    )
                }
            }
        }
    }
    return selectedLocation
}


@Composable
fun DataItem(label: String, value: String, modifier: Modifier) {
    Column(modifier.padding(12.dp)) {
        Text(
            text = label,
            style = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
        Text(
            text = value,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(fontSize = 16.sp, color = Color.Black),
            textAlign = TextAlign.End
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
    }
}

@Composable
fun ShowData(dataMap: Map<String, String>) {
    Card(
        modifier = Modifier.padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            dataMap.forEach { (key, value) ->
                DataItem(label = key, value = value, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
fun parseWeatherData(jsonData: String): Map<String, String> {
    val jsonObject = JSONObject(jsonData)
    val dailyData = jsonObject.getJSONObject("daily")
    val dataMap = mutableMapOf<String, String>()
    val unitsMap = mapOf(
        "Maximum Temperature" to "°C",
        "Minimum Temperature" to "°C",
        "Sunrise" to "iso8601",
        "Sunset" to "iso8601",
        "Precipitation" to "mm",
        "Shortwave Radiation" to "MJ/m²"
    )

    dataMap["Maximum Temperature"] = "${dailyData.getJSONArray("temperature_2m_max").getString(0)} ${unitsMap["Maximum Temperature"]}"
    dataMap["Minimum Temperature"] = "${dailyData.getJSONArray("temperature_2m_min").getString(0)} ${unitsMap["Minimum Temperature"]}"
    dataMap["Sunrise"] = "${dailyData.getJSONArray("sunrise").getString(0)} ${unitsMap["Sunrise"]}"
    dataMap["Sunset"] = "${dailyData.getJSONArray("sunset").getString(0)} ${unitsMap["Sunset"]}"
    dataMap["Precipitation"] = "${dailyData.getJSONArray("precipitation_sum").getString(0)} ${unitsMap["Precipitation"]}"
    dataMap["Shortwave Radiation"] = "${dailyData.getJSONArray("shortwave_radiation_sum").getString(0)} ${unitsMap["Shortwave Radiation"]}"

    return dataMap
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController, context: Context, viewModel: WeatherViewModel,weatherRepository: WeatherRepository) {
    val weatherData by viewModel.weatherData.observeAsState()
    val screenBreadth = LocalConfiguration.current.screenWidthDp.dp
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Fetch Weather Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.Black)
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = {
                                    Text(text = "Downloads") },
                                onClick = {
                                     navController.navigate("Data")
                                }
                            )
                        }
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            var insertdata by remember() {
                mutableStateOf(false)
            }
            if(insertdata){
                Crossfade(targetState = weatherData, label = "") { state ->
                    when (state) {
                        is Resource.Loading -> {
                            Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Success -> {
                            viewModel.parseJsonToHistoricalWeatherAndInsert(state.data)
                            insertdata=false
                        }
                        is Resource.Error -> {
                            // Weather data loading failed
                            Toast.makeText(context, "Select Date and Location", Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
            }
            }

            // Floating action button content
            FloatingActionButton(
                onClick = {
                    insertdata=true

                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(screenBreadth / 7) // Adjust the size of the FAB
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_download),
                    contentDescription = "Download Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(screenBreadth / 12)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        // Content of the screen
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(66.dp))
            val selectedLocation = remember { mutableStateOf<Address?>(null)}
            val selectedDate: MutableState<String> = datePick()
            Spacer(modifier = Modifier.height(16.dp))
            selectedLocation.value = getGeolocation(context)
            Spacer(modifier = Modifier.height(16.dp))
            val apiurlbase = "https://archive-api.open-meteo.com/v1/archive?"
            val parsedDate = LocalDate.parse(selectedDate.value)
            val apiParameters = if (parsedDate.isAfter(LocalDate.now())) {
                // If the selected date is in the future, calculate the average of the last 10 available days' temperatures
                val endDate = parsedDate.minusDays(1) // Exclude the selected future date
                val startDate = endDate.minusDays(9) // Calculate the start date for the last 10 days
                val startDateFormatted = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val endDateFormatted = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

                mapOf(
                    "latitude" to selectedLocation.value?.latitude.toString(),
                    "longitude" to selectedLocation.value?.longitude.toString(),
                    "start_date" to startDateFormatted,
                    "end_date" to endDateFormatted,
                    "daily" to "temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum,shortwave_radiation_sum",
                    "timezone" to "auto"
                )
            } else {
                // If the selected date is in the past or today, use the selected date as start and end date
                mapOf(
                    "latitude" to selectedLocation.value?.latitude.toString(),
                    "longitude" to selectedLocation.value?.longitude.toString(),
                    "start_date" to selectedDate,
                    "end_date" to selectedDate,
                    "daily" to "temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum,shortwave_radiation_sum",
                    "timezone" to "auto"
                )
            }
            val scope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                scope.launch {
                    while (true) {
                        if (!isNetworkAvailable(context)) {
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                        }
                        delay(5000)  // Check every 5 seconds
                    }
                }
            }
            if (selectedLocation.value != null && selectedDate.value.isNotEmpty()) {
                viewModel.setDataFetched()
                LaunchedEffect(Unit) {
                    viewModel.fetchWeatherData(apiurlbase, apiParameters)
                }
                Crossfade(targetState = weatherData, label = "") { state ->
                    when (state) {
                        is Resource.Loading -> {
                            Text(text = state.message ?: "Loading...")
                        }
                        is Resource.Success -> {
                            ShowData(dataMap = parseWeatherData(state.data.toString()))
                        }
                        is Resource.Error -> {
                            // Weather data loading failed
                            Text(text = "Error: ${state.message}")
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}







