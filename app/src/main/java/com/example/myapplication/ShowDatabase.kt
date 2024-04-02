package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatabase(navController: NavHostController, context: Context,weatherViewModel: WeatherViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Downloads") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle Menu click */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Menu", tint = Color.Black)
                    }
                }
            )
        }
    ) {
        val allWeather by weatherViewModel.allWeather.observeAsState(initial = emptyList())

        LazyColumn {
            items(allWeather) { weather ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "Location: ${weather.location}")
                        Text(text = "Date: ${weather.date}")
                        Text(text = "Max Temperature: ${weather.temperature_2m_max.maxOrNull()}")
                        Text(text = "Min Temperature: ${weather.temperature_2m_min.minOrNull()}")
                    }
                }
            }
        }

    }
}