package com.example.calendarapplication

import WeatherFragment
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity()  {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        // Fragment tinkering
        val monthFragment = MonthFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.MainBody, monthFragment)
            .commit()

    }


    fun onCameraButtonClick(view: View) {
        val cameraFragment = CameraFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.MainBody, cameraFragment)
            .addToBackStack(null)
            .commit()
    }

    fun onCalendarButtonClick(view: View) {
        val CalendarFragment = MonthFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.MainBody, CalendarFragment)
            .addToBackStack(null)
            .commit()
    }

    fun onWeatherButtonClick(view: View) {
        val WeatherFragment = WeatherFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.MainBody, WeatherFragment)
            .addToBackStack(null)
            .commit()
    }





}



