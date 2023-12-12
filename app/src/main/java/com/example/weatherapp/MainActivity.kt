package com.example.weatherapp

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//467dfb45f4c943caee956b1ee36cace3
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val location = findViewById<TextView>(R.id.cityName)
        location.text = "$location"
        fetchWeatherData("Kolhapur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response= retrofit.getWeatherData(cityName, "467dfb45f4c943caee956b1ee36cace3","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
               val responseBody= response.body()
                if (response.isSuccessful && responseBody!=null){
                    val temprature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunrise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val sealevel =responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp =responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    

                    binding.temp.text= "$temprature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp : $maxTemp °C"
                    binding.minTemp.text = "Min Temp : $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sea.text = "$sealevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayname(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text ="$cityName"


                // Log.d("TAG","onResponse: $temprature")

                changeImagesAccordingWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

        }

    private fun changeImagesAccordingWeather(conditions: String) {
        when(conditions){

            "HAZE" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Clear Sky" ,"Sunny" ,"Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds" ,"Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else-> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }
    private fun time(timestamp : Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayname(timestamp : Long) :String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}

