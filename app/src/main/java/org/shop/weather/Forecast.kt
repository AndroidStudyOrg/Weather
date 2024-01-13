package org.shop.weather

data class Forecast(
    val forecastDate: String,
    val forecastTime: String,

    var temperature: Double = 0.0,
    var sky: String = "",
    var percipitation: Int = 0,
    var percipitationType: String = ""
)
