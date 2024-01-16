package org.shop.weather

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class UpdateWeatherService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /**
         *  이것은 ForegroundService가 아님. 그렇기에 ForegroundService로 만들어줄 필요가 있음.
         *  만들어주기 위해 onStartCommand가 처음 시작할 때
         *  notification channel을 만들어주고, foregroundService로 전환시켜줘야한다.
         */

        val appwidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO 위젯을 권한없음 상태로 표시하고 클릭했을 때 권한 팝업을 얻을 수 있도록 추가

            return super.onStartCommand(intent, flags, startId)
        }

        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            WeatherRepository.getVillageForecast(
                serviceKey = resources.getString(R.string.service_key),
                longitude = it.longitude,
                latitude = it.latitude,
                successCallback = { forecastList ->
                    val pendingServiceIntent: PendingIntent =
                        Intent(this, UpdateWeatherService::class.java).let { intent ->
                            PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
                        }

                    val currentForecast = forecastList.first()

                    RemoteViews(packageName, R.layout.widget_weather).apply {
                        setTextViewText(
                            R.id.temperatureTextView,
                            resources.getString(
                                R.string.temperature_text,
                                currentForecast.temperature
                            )
                        )
                        setTextViewText(
                            R.id.weatherTextView,
                            currentForecast.weather
                        )
                        setOnClickPendingIntent(R.id.temperatureTextView, pendingServiceIntent)
                    }.also { remoteViews ->
                        val appWidgetName =
                            ComponentName(this, WeatherAppWidgetProvider::class.java)
                        appwidgetManager.updateAppWidget(appWidgetName, remoteViews)
                    }

                    stopSelf()
                },
                failureCallback = {
                    // TODO 위젯을 에러상태로 표시

                    stopSelf()
                }
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }
}