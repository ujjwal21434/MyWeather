@file:Suppress("DEPRECATION")

package com.example.myweather

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myweather.ui.theme.MyWeatherTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeatherTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Weatherscreen()
                }
            }
        }
    }

}

@Composable
fun Weatherscreen(modifier: Modifier = Modifier) {
    var dateInput by remember { mutableStateOf("") }
    var maxTemp by remember { mutableStateOf<Double?>(null) }
    var minTemp by remember { mutableStateOf<Double?>(null) }
    var avgTemp by remember { mutableStateOf<Double?>(null) }
    var stateInput by remember { mutableStateOf("") }
    val isInputValid = dateInput.isNotBlank() && stateInput.isNotBlank()
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val context = LocalContext.current
    val isOnline = isOnline(context)
    val dbHandler = WeatherDBHandler(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Weather App", fontSize = 24.sp, modifier = Modifier.padding(16.dp))

        Button(onClick = {
            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                dateInput = "$year-${month + 1}-$dayOfMonth"
            }, year, month, day).show()
        }) {
            Text(if (dateInput.isNotBlank()) dateInput else "Enter Date *")
        }

        /*
        TextField(
            value = dateInput,
            onValueChange = { dateInput = it },
            label = { Text("Enter Date as yyyy-MM-dd *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )*/

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = stateInput,
            onValueChange = { stateInput = it },
            label = { Text("Enter State Name *") },
            singleLine = true,
        )


        Spacer(modifier = Modifier.height(16.dp))



        //First button for Fetch data and display it
        Button(
            onClick = {
                fetchWeatherData(context, dateInput, stateInput) { weatherData ->
                    weatherData?.let {
                        maxTemp = it.forecast.forecastday.firstOrNull()?.day?.maxtemp_c
                        minTemp = it.forecast.forecastday.firstOrNull()?.day?.mintemp_c
                        avgTemp = it.forecast.forecastday.firstOrNull()?.day?.avgtemp_c
                    }
                }
            },
            enabled = isInputValid && isOnline
        ) {
            Text(text = "Check Temperature data (Online)")
        }

        Spacer(modifier = Modifier.height(10.dp))

        //Second button for Fetch data, save it, and display it
        Button(
            onClick = {
                fetchWeatherData(context, dateInput, stateInput) { weatherData ->
                    weatherData?.let {
                        maxTemp = it.forecast.forecastday.firstOrNull()?.day?.maxtemp_c
                        minTemp = it.forecast.forecastday.firstOrNull()?.day?.mintemp_c
                        avgTemp = it.forecast.forecastday.firstOrNull()?.day?.avgtemp_c

                        // Save or update data in SQLite database
                        dbHandler.addOrUpdateWeatherData(context, dateInput, stateInput, maxTemp ?: 0.0, minTemp ?: 0.0, avgTemp ?: 0.0)
                    }
                }
            },
            enabled = isInputValid && isOnline
        ) {
            Text(text = "Fetch Temperature data for entered date & state and save it for offline use")
        }

        Spacer(modifier = Modifier.height(10.dp))

        //Third button: Fetch data from database and display it
        Button(
            onClick = {
                val weatherData = dbHandler.getWeatherData(dateInput, stateInput)
                if (weatherData != null) {
                    maxTemp = weatherData.maxTemp
                    minTemp = weatherData.minTemp
                    avgTemp = weatherData.avgTemp
                } else {
                    Toast.makeText(context, "Data doesn't exist locally", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = isInputValid
        ) {
            Text(text = "View Temperature data for entered date & state from saved data")
        }

        Button(
            onClick = {
                dateInput = ""
                stateInput = ""
                maxTemp = null
                minTemp = null
                avgTemp = null
            },
            enabled = dateInput!= "" || stateInput!= "" || maxTemp!= null || minTemp!= null || avgTemp!= null
        ) {
            Text(text = "Clear Screen")
        }

        Spacer(modifier = Modifier.height(10.dp))


        maxTemp?.let { maxTemp ->
            Text(
                text = "Max Temperature: $maxTemp°C",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        minTemp?.let { minTemp ->
            Text(
                text = "Min Temperature: $minTemp°C",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        avgTemp?.let { avgTemp ->
            Text(
                text = "Avg Temperature: $avgTemp°C",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}


fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

private val weatherApiCall = WeatherApiCall()

/*
private fun fetchWeatherData(context: Context, date: String, state: String, callback: (WeatherDataModel?) -> Unit) {
    val apiKey = "65fb005def1247e4887120259240404"

    weatherApiCall.getWeatherData(context = context, apiKey = apiKey, location = state, date = date, callback = callback)
}
*/

private fun fetchWeatherData(context: Context, dateInput: String, stateInput: String, callback: (WeatherDataModel?) -> Unit) {
    val apiKey = "65fb005def1247e4887120259240404"

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val enteredDate = Calendar.getInstance().apply { time = sdf.parse(dateInput) }
    val currentDate = Calendar.getInstance()

    //Checking if the entered date is more than three days in the future
    if (enteredDate.timeInMillis - currentDate.timeInMillis > TimeUnit.DAYS.toMillis(3)) {
        //Fetching weather data for the same date from the past two years
        val pastYear1 = (enteredDate.get(Calendar.YEAR) - 1).toString() + dateInput.substring(4)
        val pastYear2 = (enteredDate.get(Calendar.YEAR) - 2).toString() + dateInput.substring(4)

        weatherApiCall.getWeatherData(context, apiKey, location = stateInput, date = pastYear1) { weatherData1 ->
            weatherData1?.let {
                weatherApiCall.getWeatherData(context, apiKey, location = stateInput, date = pastYear2) { weatherData2 ->
                    weatherData2?.let {
                        //Calculating the average weather data
                        val maxTemp1 = weatherData1.forecast.forecastday.firstOrNull()?.day?.maxtemp_c ?: 0.0
                        val maxTemp2 = weatherData2.forecast.forecastday.firstOrNull()?.day?.maxtemp_c ?: 0.0
                        val maxTemp = (maxTemp1 + maxTemp2) / 2

                        val minTemp1 = weatherData1.forecast.forecastday.firstOrNull()?.day?.mintemp_c ?: 0.0
                        val minTemp2 = weatherData2.forecast.forecastday.firstOrNull()?.day?.mintemp_c ?: 0.0
                        val minTemp = (minTemp1 + minTemp2) / 2

                        val avgTemp1 = weatherData1.forecast.forecastday.firstOrNull()?.day?.avgtemp_c ?: 0.0
                        val avgTemp2 = weatherData2.forecast.forecastday.firstOrNull()?.day?.avgtemp_c ?: 0.0
                        val avgTemp = (avgTemp1 + avgTemp2) / 2


                        //Creating a new WeatherDataModel with the average weather data
                        val forecastDay = ForecastDay(Day(maxTemp, minTemp, avgTemp, Condition()), Astro(), listOf())
                        val forecast = Forecast(listOf(forecastDay))
                        val location = Location(stateInput, "", "", 0.0, 0.0, "", 0, "")
                        val predictedWeatherData = WeatherDataModel(location, forecast)

                        callback(predictedWeatherData)
                    }
                }
            }
        }
    } else {
        //Fetching weather data normally
        weatherApiCall.getWeatherData(context, apiKey, location = stateInput, date = dateInput, callback = callback)
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyWeatherTheme {
        Weatherscreen()
    }
}