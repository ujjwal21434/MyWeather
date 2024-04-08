package com.example.myweather


import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiCall {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: WeatherApiService = retrofit.create(WeatherApiService::class.java)

    fun getWeatherData(context: Context, apiKey: String, location: String, date: String, callback: (WeatherDataModel?) -> Unit) {
        val call = service.getWeatherData(apiKey, location, date)

        call.enqueue(object : Callback<WeatherDataModel> {
            override fun onResponse(call: Call<WeatherDataModel>, response: Response<WeatherDataModel>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    callback(weatherData)
                } else {

                    Toast.makeText(context, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onFailure(call: Call<WeatherDataModel>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch weather data: ${t.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}
/*
private val weatherApiCall = WeatherApiCall()

private fun fetchWeatherData(date: String, state: String, callback: (WeatherDataModel?) -> Unit) {
    val apiKey = "65fb005def1247e4887120259240404"
    weatherApiCall.getWeatherData(this, apiKey, state, date, callback)
}

*/