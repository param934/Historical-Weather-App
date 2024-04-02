package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
       val context=applicationContext
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {

                val weatherDao =WeatherDatabase.getDatabase(applicationContext)
                val dao= weatherDao.weatherDao()// Initialize your HistoricalWeatherDao here
                val weatherRepository = WeatherRepository(dao)
                val data=WeatherViewModel(weatherRepository)
                val navController = rememberNavController()
                NavHost(navController=navController, startDestination = "Splash")
                {
                    composable("Splash") {
                        SplashScreen(navController)
                    }
                    composable("Land") {
                        LandingPage(navController,context)
                    }
                    composable("Home"){
                        HomePage(navController,context,data,weatherRepository)
                    }
                    composable("Data"){
                        ShowDatabase(navController,context, weatherViewModel = data)
                    }

                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    var logoAlpha by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.navigate("Land") {
            launchSingleTop = true
            popUpTo("Splash") { inclusive = true }
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F8FF)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(400.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "App by Param",
                style = TextStyle(fontSize = 24.sp),
                color =Color(0xFF2F4F4F)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

