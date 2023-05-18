import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendarapplication.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


class WeatherFragment : Fragment() {

    private val apiKey = "d5b1a4f2a5786e835d6aa402a43a8461"
    private val apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=London&appid=$apiKey"

    private lateinit var weatherRecyclerView: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        weatherRecyclerView = view.findViewById(R.id.weatherRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherAdapter = WeatherAdapter()
        weatherRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        weatherRecyclerView.adapter = weatherAdapter
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(apiUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WeatherApp", "Failed to fetch weather data: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                activity?.runOnUiThread {
                    if (response.isSuccessful && responseData != null) {
                        val weatherData = parseWeatherData(responseData)
                        displayWeatherData(weatherData)
                    } else {
                        Log.e("WeatherApp", "Failed to fetch weather data: ${response.message}")
                    }
                }
            }
        })
    }

    private fun parseWeatherData(jsonString: String): List<WeatherData> {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("list")

        val weatherData = mutableListOf<WeatherData>()

        val calendar = Calendar.getInstance()
        val uniqueDays = mutableSetOf<Int>()

        for (i in 0 until jsonArray.length()) {
            val weatherObject = jsonArray.getJSONObject(i)
            val mainJsonObject = weatherObject.getJSONObject("main")
            val temperatureKelvin = mainJsonObject.getDouble("temp")
            val temperatureCelsius = temperatureKelvin - 273.15 // Conversion from Kelvin to Celsius
            val humidity = mainJsonObject.getInt("humidity")

            val timestamp = weatherObject.getLong("dt") * 1000 // Convert timestamp to milliseconds
            calendar.timeInMillis = timestamp

            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            if (uniqueDays.size >= 5) {
                break // Fetch weather data for only 5 unique days
            }

            if (dayOfMonth !in uniqueDays) {
                uniqueDays.add(dayOfMonth)

                val dayOfWeek = SimpleDateFormat("EEEE, dd/MM/yy", Locale.getDefault()).format(calendar.time)

                val weatherArray = weatherObject.getJSONArray("weather")
                val weatherIconCode = weatherArray.getJSONObject(0).getString("icon")

                val weather = WeatherData(dayOfWeek, temperatureCelsius.toInt(), humidity, weatherIconCode)
                weatherData.add(weather)
            }
        }

        return weatherData
    }

    private fun displayWeatherData(weatherData: List<WeatherData>) {
        weatherAdapter.setData(weatherData)
    }

    data class WeatherData(val day: String, val temperature: Int, val humidity: Int, val weatherIcon: String)


    private inner class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
        private var weatherData: List<WeatherData> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
            return WeatherViewHolder(view)
        }

        override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
            val weather = weatherData[position]
            holder.bind(weather)
        }

        override fun getItemCount(): Int = weatherData.size

        fun setData(data: List<WeatherData>) {
            weatherData = data
            notifyDataSetChanged()
        }

        inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
            private val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
            private val humidityTextView: TextView = itemView.findViewById(R.id.humidityTextView)
            private val weatherIconImageView: ImageView = itemView.findViewById(R.id.weatherIconImageView)

            fun bind(weather: WeatherData) {
                dayTextView.text = weather.day
                temperatureTextView.text = "${weather.temperature} °C"
                humidityTextView.text = "Humidity: ${weather.humidity}%"

                val weatherIconUrl = "https://openweathermap.org/img/wn/${weather.weatherIcon}.png"
                Glide.with(itemView)
                    .load(weatherIconUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(weatherIconImageView)
            }

        }
    }
}
