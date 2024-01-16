package org.shop.weather

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
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

        // notification channel
        createChannel()
        // foregroundService
        startForeground(1, createNotification())

        val appwidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val pendingIntent: PendingIntent =
                Intent(this, SettingActivity::class.java).let { intent ->
                    PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_IMMUTABLE)
                }

            RemoteViews(packageName, R.layout.widget_weather).apply {
                setTextViewText(R.id.temperatureTextView, "권한 없음")
                setTextViewText(R.id.weatherTextView, "")
                setOnClickPendingIntent(R.id.temperatureTextView, pendingIntent)
            }.also { remoteViews ->
                val appWidgetName = ComponentName(this, WeatherAppWidgetProvider::class.java)
                appwidgetManager.updateAppWidget(appWidgetName, remoteViews)
            }

            stopSelf()

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
                    val pendingServiceIntent: PendingIntent =
                        Intent(this, UpdateWeatherService::class.java).let { intent ->
                            PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
                        }

                    RemoteViews(packageName, R.layout.widget_weather).apply {
                        setTextViewText(
                            R.id.temperatureTextView,
                            "에러"
                        )
                        setTextViewText(R.id.weatherTextView, "")
                        setOnClickPendingIntent(R.id.temperatureTextView, pendingServiceIntent)
                    }.also { remoteViews ->
                        val appWidgetName =
                            ComponentName(this, WeatherAppWidgetProvider::class.java)
                        appwidgetManager.updateAppWidget(appWidgetName, remoteViews)
                    }

                    stopSelf()
                }
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createChannel() {
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL, "날씨앱", NotificationManager.IMPORTANCE_LOW)
        channel.description = "위젯을 업데이트하는 채널"

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("날씨앱")
            .setContentText("날씨 업데이트")
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "widget_refresh_channel"
    }
}