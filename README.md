MyWeather App

Introduction

Welcome to MyWeather, an Android application that allows users to check temperature data for specific dates and locations. This README file provides an overview of the implementation details of the app's main activity.

Implementation Overview

The MainActivity.kt file contains the primary logic and user interface components of the MyWeather app. Here's a breakdown of the key features and components:

User Interface

The user interface is built using Jetpack Compose, a modern toolkit for building native Android UIs.
It includes text fields for entering the date and state, along with buttons for fetching, saving, and displaying temperature data.

Data Retrieval

The app fetches temperature data from an online weather API based on the user's input (date and state).
It also includes functionality to fetch historical weather data for dates more than three days in the past.

Database Interaction

The app stores fetched temperature data locally using SQLite database to support offline use.
Users can retrieve and view saved temperature data from the database.

Connectivity Check

Before making API calls, the app checks for internet connectivity to ensure proper functioning.

Dependencies

The app relies on the WeatherApiCall class for making API requests and handling responses.
It also uses the WeatherDBHandler class for interacting with the SQLite database.

Usage

To use the MyWeather app:

Enter the desired date (in yyyy-MM-dd format) and state name.
Click the respective buttons to fetch and display temperature data online, save it for offline use, or retrieve saved data from the database.
Optionally, clear the screen to enter new data.
Dependencies
Android SDK: Minimum SDK version 21 (Android 5.0 Lollipop)
Kotlin: Version 1.5.21
Jetpack Compose: Version 1.0.0-beta09
