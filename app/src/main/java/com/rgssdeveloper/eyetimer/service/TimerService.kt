package com.rgssdeveloper.eyetimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.rgssdeveloper.eyetimer.model.TimerEvent
import com.rgssdeveloper.eyetimer.util.Constants.ACTION_START_SERVICE
import com.rgssdeveloper.eyetimer.util.Constants.ACTION_STOP_SERVICE
import com.rgssdeveloper.eyetimer.util.Constants.END_NOTIFICATION_CHANNEL_ID
import com.rgssdeveloper.eyetimer.util.Constants.END_NOTIFICATION_CHANNEL_NAME
import com.rgssdeveloper.eyetimer.util.Constants.END_NOTIFICATION_ID
import com.rgssdeveloper.eyetimer.util.Constants.NOTIFICATION_CHANNEL_ID
import com.rgssdeveloper.eyetimer.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.rgssdeveloper.eyetimer.util.Constants.NOTIFICATION_ID
import com.rgssdeveloper.eyetimer.util.Constants.TOTAL_TIME
import com.rgssdeveloper.eyetimer.util.TimerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class TimerService : LifecycleService() {
    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
    }
    @Inject lateinit var notificationSoundUri:Uri

    private var isServiceStopped = false

    @Inject
    @Named(NOTIFICATION_CHANNEL_ID)
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    @Named(END_NOTIFICATION_CHANNEL_ID)
    lateinit var endNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    private val totalTime = TOTAL_TIME

    //Timer properties
    private var lapTime = 0L
    private var timeStarted = 0L

    override fun onCreate() {
        super.onCreate()
        initValues()
    }

    private fun initValues(){
        timerEvent.postValue(TimerEvent.END)
        timerInMillis.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    notificationManager.cancel(END_NOTIFICATION_ID)
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        timerEvent.postValue(TimerEvent.START)
        startTimer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        timerInMillis.observe(this, {
            if (!isServiceStopped) {
                notificationBuilder.setContentText(
                    TimerUtil.getFormattedTime(totalTime-it)
                )
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                if(it>=totalTime)
                    finishService()
            }
        })
    }

    private fun showEndNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createEndNotificationChannel()
        }
        notificationManager.notify(END_NOTIFICATION_ID,endNotificationBuilder.build())
    }

    private fun stopService() {
        isServiceStopped = true
        initValues()
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    private fun finishService(){
        stopService()
        showEndNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEndNotificationChannel() {
        val channel =
            NotificationChannel(
                END_NOTIFICATION_CHANNEL_ID,
                END_NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_HIGH
            )
        channel.setSound(
            notificationSoundUri,
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
        )
        channel.vibrationPattern= longArrayOf(0,300,500,300)
        notificationManager.createNotificationChannel(channel)
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (timerEvent.value!! == TimerEvent.START && !isServiceStopped) {
                lapTime = System.currentTimeMillis() - timeStarted
                timerInMillis.postValue(lapTime)
                delay(500L)
            }
        }
    }
}