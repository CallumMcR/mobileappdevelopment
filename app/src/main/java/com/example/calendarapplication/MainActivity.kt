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

interface OnMonthClickListener {
    fun onMonthClick(monthNumber: Int)
}

interface ChangeYearButtonClickListener{
    fun onChangeYearClick(year:Int)
}



class MainActivity : AppCompatActivity(), MonthFragment.OnMonthClickListener  {

    private var currentYear by Delegates.notNull<Int>()
    private var changeYearClickListener: ChangeYearButtonClickListener? = null

    fun setChangeYearClickListener(listener: ChangeYearButtonClickListener){
        changeYearClickListener = listener
    }

    private fun changeYear(incrementType: String) {
        when (incrementType) {
            "negative" -> currentYear--
            "positive" -> currentYear++
        }
        val yearText = findViewById<TextView>(R.id.calendar_year)
        val text = currentYear.toString()
        yearText.text = text
        Log.d("yearText",text)
        val toInt = text.toInt()
        changeYearClickListener?.onChangeYearClick(toInt)
    }



    override fun onMonthClick(monthNumber: Int) {
        val daysFragment = DayFragment.newInstance(monthNumber)
        supportFragmentManager.popBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.MainBody, daysFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        //Setting current year
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearText = findViewById<TextView>(R.id.calendar_year)
        yearText.text = currentYear.toString()

        // Adjust Year
        val yearPrevButton: ImageButton = findViewById(R.id.previous_button)
        yearPrevButton.setOnClickListener { changeYear("negative") }

        val yearNextButton: ImageButton = findViewById(R.id.next_button)
        yearNextButton.setOnClickListener { changeYear("positive") }


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



