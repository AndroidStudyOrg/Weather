package org.shop.weather

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.shop.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)

        service.getVillageForecast(
            serviceKey = getString(R.string.service_key),
            baseDate = "20240113",
            baseTime = "1700",
            nx = 55,
            ny = 127
        ).enqueue(object : Callback<WeatherEntity> {
            override fun onResponse(call: Call<WeatherEntity>, response: Response<WeatherEntity>) {
                val forecaseDataTimeMap = mutableMapOf<String, Forecast>()
                val forecastList =
                    response.body()?.response?.body?.items?.forecastEntities.orEmpty()
                for (forecast in forecastList) {
                    if (forecaseDataTimeMap["${forecast.forecastDate}/${forecast.forecastTime}"] == null) {
                        forecaseDataTimeMap["${forecast.forecastDate}/${forecast.forecastTime}"] =
                            Forecast(
                                forecastDate = forecast.forecastDate,
                                forecastTime = forecast.forecastTime
                            )
                    }

                    forecaseDataTimeMap["${forecast.forecastDate}/${forecast.forecastTime}"]?.apply {
                        when (forecast.category) {
                            Category.POP -> {
                                percipitation = forecast.forecastValue.toInt()
                            }

                            Category.PTY -> {
                                percipitationType = transformRainType(forecast)
                            }

                            Category.SKY -> {
                                sky = transformSky(forecast)
                            }

                            Category.TMP -> {
                                temperature = forecast.forecastValue.toDouble()
                            }

                            else -> {}
                        }
                    }
                }
                Log.d("MainActivity Forecast", forecaseDataTimeMap.toString())
            }

            override fun onFailure(call: Call<WeatherEntity>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun transformRainType(forecast: ForecastEntity): String {
        return when (forecast.forecastValue.toInt()) {
            0 -> "없음"
            1 -> "비"
            2 -> "비/눈"
            3 -> "눈"
            4 -> "소나기"
            else -> ""
        }
    }

    private fun transformSky(forecast: ForecastEntity): String {
        return when (forecast.forecastValue.toInt()) {
            1 -> "맑음"
            3 -> "구름많음"
            4 -> "흐림"
            else -> ""
        }
    }
}