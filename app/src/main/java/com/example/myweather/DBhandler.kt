package com.example.myweather

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class WeatherDBHandler(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE_COL + " TEXT,"
                + STATE_COL + " TEXT,"
                + MAX_TEMP_COL + " REAL,"
                + MIN_TEMP_COL + " REAL,"
                + AVG_TEMP_COL + " REAL)")
        db.execSQL(query)
    }

    fun addWeatherData(
        date: String,
        state: String,
        maxTemp: Double,
        minTemp: Double,
        avgTemp: Double
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DATE_COL, date)
        values.put(STATE_COL, state)
        values.put(MAX_TEMP_COL, maxTemp)
        values.put(MIN_TEMP_COL, minTemp)
        values.put(AVG_TEMP_COL, avgTemp)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    @SuppressLint("Range")
    fun getWeatherData(date: String, state: String): WeatherDBModel? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $DATE_COL = ? AND $STATE_COL = ?", arrayOf(date, state))

        return if (cursor.moveToFirst()) {
            val maxTemp = cursor.getDouble(cursor.getColumnIndex(MAX_TEMP_COL))
            val minTemp = cursor.getDouble(cursor.getColumnIndex(MIN_TEMP_COL))
            val avgTemp = cursor.getDouble(cursor.getColumnIndex(AVG_TEMP_COL))
            cursor.close()
            //Creating and return a WeatherDBModel object
            WeatherDBModel(date, state, maxTemp, minTemp, avgTemp)
        } else {
            cursor.close()
            null
        }
    }

    fun addOrUpdateWeatherData(context: Context, date: String, state: String, maxTemp: Double, minTemp: Double, avgTemp: Double) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DATE_COL, date)
        values.put(STATE_COL, state)
        values.put(MAX_TEMP_COL, maxTemp)
        values.put(MIN_TEMP_COL, minTemp)
        values.put(AVG_TEMP_COL, avgTemp)

        // First, we try to update the existing data
        val rowsAffected = db.update(TABLE_NAME, values, "$DATE_COL = ? AND $STATE_COL = ?", arrayOf(date, state))

        // If no rows were updated, that means the data doesn't exist yet, so we insert it
        if (rowsAffected == 0) {
            db.insert(TABLE_NAME, null, values)
            Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }



    companion object {
        private const val DB_NAME = "weatherdb"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "weather"
        private const val ID_COL = "id"
        private const val DATE_COL = "date"
        private const val STATE_COL = "state"
        private const val MAX_TEMP_COL = "max_temp"
        private const val MIN_TEMP_COL = "min_temp"
        private const val AVG_TEMP_COL = "avg_temp"
    }
}



