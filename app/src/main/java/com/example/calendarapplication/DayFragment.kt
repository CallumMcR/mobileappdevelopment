package com.example.calendarapplication


import DayIndividualAdapter
import DayIndividualPicturesAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import java.sql.Blob


private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
private const val ARG_MONTH_NUMERIC = "ARG_MONTH_NUMERIC"

class DayFragment : Fragment(),ChangeYearButtonClickListener {

    private var monthNumber = 0
    private var spinnerValue = "Events"
    private lateinit var dbHandler: DBHandler
    private var baseYear : Int = 2023
    private lateinit var dayAdapter: DayAdapter


    override fun onChangeYearClick(year: Int) {
        baseYear = year
        Log.d("baseyear",baseYear.toString())
        updateData()
    }

    private fun setGetYear(): Int {

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.activity_main, null)
        val currentYearView = view.findViewById<TextView>(R.id.calendar_year)
        val currentYear = currentYearView.text
        val convertedToString = currentYear.toString()
        return convertedToString.toInt()
    }

    private fun showAddEventDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Event")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_event_pop_up, null)
        builder.setView(view)

        val hourSpinner = view.findViewById<Spinner>(R.id.hour_spinner)
        val minuteSpinner = view.findViewById<Spinner>(R.id.minute_spinner)
        val hours = arrayOf("00","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23")
        val minutes = arrayOf("00", "15", "30", "45")
        val hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, hours)
        val minuteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, minutes)
        hourSpinner.adapter = hourAdapter
        minuteSpinner.adapter = minuteAdapter


        val daySpinner = view.findViewById<Spinner>(R.id.day_spinner)
        val days = ArrayList<String>()
        val myCalendar = Calendar.getInstance()
        val monthNumberChanged = monthNumber - 1
        myCalendar.set(Calendar.MONTH, monthNumberChanged)
        myCalendar.set(Calendar.YEAR, baseYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            days.add(i.toString())
        }
        val dayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, days)
        daySpinner.adapter = dayAdapter

        builder.setPositiveButton("Add") { dialog, _ ->
            dbHandler = DBHandler(requireContext())
            val desc = view.findViewById<EditText>(R.id.note_edittext)
            val selectedDay = daySpinner.selectedItem.toString()
            val hourString = hourSpinner.selectedItem.toString()
            val minuteString = minuteSpinner.selectedItem.toString()
            val year = baseYear.toString()
            val dateAsString = "$selectedDay/$monthNumber/$year $hourString:$minuteString:00"

            val inputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault())
            val inputDate = inputDateFormat.parse(dateAsString)
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
            val outputDateString = outputDateFormat.format(inputDate)


            dbHandler.addNewEvent(desc.text.toString(), outputDateString)
            updateData()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun updateData() {
        val dates = addDatesToList()
        dayAdapter.updateDates(dates)
        dayAdapter.notifyDataSetChanged()
    }

    private fun addDaysToList(): ArrayList<String> {
        val days = ArrayList<String>()
        val myCalendar = Calendar.getInstance()
        val monthNumberChanged = monthNumber-1
        myCalendar.set(Calendar.MONTH, monthNumberChanged)
        myCalendar.set(Calendar.YEAR, baseYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            myCalendar.set(Calendar.DAY_OF_MONTH, i)
            val dayOfWeek: Int = myCalendar.get(Calendar.DAY_OF_WEEK)
            val fullWeekday: String = DateFormatSymbols().weekdays[dayOfWeek]
            days.add(fullWeekday)
        }
        Log.d("DaysList", days.toString())
        return days
    }

    private fun addDatesToList(): ArrayList<String>{
        val dates = ArrayList<String>()
        val myCalendar= Calendar.getInstance()
        val monthNumberChanged = monthNumber-1
        myCalendar.set(Calendar.MONTH, monthNumberChanged)
        myCalendar.set(Calendar.YEAR, baseYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val year = baseYear.toString()
        val daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            myCalendar.set(Calendar.DAY_OF_MONTH, i)
            val dayOfMonth = myCalendar.get(Calendar.DAY_OF_MONTH)
            dates.add("$dayOfMonth/$monthNumber/$year")
        }

        return dates
    }

    private val listOfDays: ArrayList<String> by lazy {
        addDaysToList()
    }

    private val listOfDates:ArrayList<String> by lazy {
        addDatesToList()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            monthNumber = it.getInt(ARG_MONTH_NUMERIC)
        }

        val view = inflater.inflate(R.layout.fragment_day, container, false)
        dbHandler = DBHandler(requireContext())

        // Initialize dayAdapter before setting it as the adapter for the RecyclerView
        dayAdapter = DayAdapter(listOfDays, listOfDates, dbHandler,requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.days_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dayAdapter

        val addButton = view.findViewById<ImageButton>(R.id.add_button)
        addButton.setOnClickListener {
            showAddEventDialog()
            Log.d("click add button", "clicked")
        }
        spinnerValue = "Events"
        val filterSpinner = view.findViewById<Spinner>(R.id.filter_dropdown_button)
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = parent?.getItemAtPosition(position).toString()
                spinnerValue = selectedOption // Update the spinner value
                dayAdapter.spinnerValue = spinnerValue // Pass the updated spinner value to the adapter
                updateData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerValue = "Events"
                dayAdapter.spinnerValue = spinnerValue // Pass the default spinner value to the adapter
                updateData()
            }
        }
        Log.d("Spinner opt", spinnerValue)

        return view
    }

    companion object {
        fun newInstance(monthNumber: Int) = DayFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MONTH_NUMERIC, monthNumber)
            }
        }
    }


}

class DayAdapter(
    private val days: List<String>,
    private var dates: ArrayList<String>,
    private val dbHandler: DBHandler,
    private val context: Context
) : RecyclerView.Adapter<DayAdapter.ViewHolder>() {
    var spinnerValue = "Events"

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText :TextView = view.findViewById(R.id.date_textview)
        val dayText : TextView = view.findViewById(R.id.day_textview)
        val eventsList: RecyclerView = view.findViewById(R.id.events_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.day_button, parent, false)
        return ViewHolder(view)
    }

    data class Event(val date: String, val description: String, val time: String, val ID: String)
    data class Photo(val pictureData: ByteArray, val date: String, val ID: String)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        val date = dates[position]
        holder.dayText.text = day
        holder.dateText.text = date


        if (spinnerValue == "Events") { // Check the spinner value
            // Retrieve the events for the current date
            val events = eventOnSpecificDay(date)

            // Set up the adapter for the eventsList RecyclerView with the retrieved events data
            val eventAdapter = DayIndividualAdapter(context, events)
            holder.eventsList.layoutManager = LinearLayoutManager(context)
            holder.eventsList.adapter = eventAdapter
        } else if(spinnerValue == "Pictures") {
            Log.d("Spinner","Pictures ");
            val photos = photosOnSpecificDay(date)
            val photoAdapter = DayIndividualPicturesAdapter(context, photos)
            holder.eventsList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
            holder.eventsList.adapter = photoAdapter
        }

    }

    fun updateDates(updatedDates: ArrayList<String>) {
        dates.clear() // Clear the current dates list
        dates.addAll(updatedDates) // Add the new dates to the list
        notifyDataSetChanged() // Notify the adapter that the dataset has changed
    }


    private fun formatDate(date: String, inputType: String): String {
        val inputDateFormat = when (inputType) {
            "days" -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            else -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        }
        val inputDate = inputDateFormat.parse(date)

        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return outputDateFormat.format(inputDate)
    }

    private fun formatDateToTime(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val inputDate = inputDateFormat.parse(date)
        val outputDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return outputDateFormat.format(inputDate)
    }


    private fun photosOnSpecificDay(date: String): ArrayList<Photo> {
        val convertedDate = formatDate(date, "days")
        val photosData = dbHandler.getPhotos(convertedDate)
        Log.d("list oh photodats",photosData.toString())
        val photos = ArrayList<Photo>()
        if (photosData != null) {
            while (photosData.moveToNext()) {
                val id = photosData.getString(photosData.getColumnIndexOrThrow("pictureID"))
                val pictureData = photosData.getBlob(photosData.getColumnIndexOrThrow("pictureData"))
                val pictureDate = photosData.getString(photosData.getColumnIndexOrThrow("pictureDate"))
                val photo = Photo(pictureData, pictureDate, id)
                Log.d("photo data",pictureData.toString())
                photos.add(photo)
            }
        }
        Log.d("Date convo photo", convertedDate.toString())
        Log.d("List of photos",photos.toString())
        return photos
    }


    private fun eventOnSpecificDay(date: String): ArrayList<Event> {
        val convertedDate = formatDate(date, "days")
        val eventsData = dbHandler.getEvents(convertedDate)
        val events = ArrayList<Event>()
        if (eventsData != null) {
            while (eventsData.moveToNext()) {
                val desc = eventsData.getString(eventsData.getColumnIndexOrThrow("eventDescription"))
                val dateString = eventsData.getString(eventsData.getColumnIndexOrThrow("dateTimeJava"))
                val time = formatDateToTime(dateString)
                val id = eventsData.getString(eventsData.getColumnIndexOrThrow("eventID"))
                val event = Event(date, desc, time, id)
                events.add(event)
            }
        }
        return events
    }

    override fun getItemCount() = days.size


}



