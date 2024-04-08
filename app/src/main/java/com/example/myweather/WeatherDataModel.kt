package com.example.myweather

//data class DataModel()


data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val condition: Condition
)

data class Condition(
    val placeholder: String = ""
)

data class ForecastDay(
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

data class Astro(
    val placeholder: String = ""
)

data class Hour(
    val condition: Condition
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class WeatherDataModel(
    val location: Location,
    val forecast: Forecast
)
