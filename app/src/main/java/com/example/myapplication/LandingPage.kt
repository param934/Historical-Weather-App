package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun LandingPage(navController: NavHostController, context: Context) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val screenLength = LocalConfiguration.current.screenHeightDp.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Top, // Align content at the top
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.weather),
            contentDescription = "Weather Vector"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp), // Apply horizontal padding here
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Plan Your Travels with Ease",
                style = typography.titleLarge.copy(fontSize = 50.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Add some vertical space between text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp), // Apply horizontal padding here
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Easily track historical weather with our app. Access it anytime, anywhere. Never miss important weather details again!",
                style = typography.bodyMedium.copy(fontSize = 32.sp),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 36.sp // Adjust line spacing
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.Bottom, // Align content at the bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("Home") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp) // Add padding from the top
                .padding(horizontal = 16.dp)
                .height(60.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(text = "Get Started", style = typography.bodyMedium.copy(fontSize = 32.sp))
        }
    }
}


@Preview(heightDp = 1600, widthDp = 800)
@Composable
fun LandingPagePreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    LandingPage(navController = navController, context = context)
}
