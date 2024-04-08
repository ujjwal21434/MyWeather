package com.example.myweather

data class WeatherDBModel(
    val date: String,
    val state: String,
    val maxTemp: Double,
    val minTemp: Double,
    val avgTemp: Double
)
