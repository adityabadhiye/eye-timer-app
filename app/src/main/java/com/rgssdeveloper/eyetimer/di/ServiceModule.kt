package com.rgssdeveloper.eyetimer.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.getColor
import com.rgssdeveloper.eyetimer.MainActivity
import com.rgssdeveloper.eyetimer.R
import com.rgssdeveloper.eyetimer.service.TimerService
import com.rgssdeveloper.eyetimer.ui.theme.customColors
import com.rgssdeveloper.eyetimer.util.Constants
import com.rgssdeveloper.eyetimer.util.Constants.END_NOTIFICATION_CHANNEL_ID
import com.rgssdeveloper.eyetimer.util.Constants.MAIN_ACTIVITY_PENDING_INTENT
import com.rgssdeveloper.eyetimer.util.Constants.NOTIFICATION_CHANNEL_ID
import com.rgssdeveloper.eyetimer.util.Constants.START_TIMER_PENDING_INTENT
import com.rgssdeveloper.eyetimer.util.Constants.STOP_TIMER_PENDING_INTENT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Named

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    @Named(MAIN_ACTIVITY_PENDING_INTENT)
    fun provideMainActivityPendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            420,
            Intent(context, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    @ServiceScoped
    @Provides
    @Named(START_TIMER_PENDING_INTENT)
    fun provideStartTimerPendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent =
        PendingIntent.getService(
            context,
            421,
            Intent(context, TimerService::class.java).apply {
                this.action = Constants.ACTION_START_SERVICE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    @RequiresApi(Build.VERSION_CODES.M)
    @ServiceScoped
    @Provides
    @Named(STOP_TIMER_PENDING_INTENT)
    fun provideStopTimerPendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent =
        PendingIntent.getService(
            context,
            421,
            Intent(context, TimerService::class.java).apply {
                this.action = Constants.ACTION_STOP_SERVICE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


    @ServiceScoped
    @Provides
    @Named(NOTIFICATION_CHANNEL_ID)
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        @Named(MAIN_ACTIVITY_PENDING_INTENT) pendingIntent: PendingIntent,
        @Named(STOP_TIMER_PENDING_INTENT) pendingIntent1: PendingIntent
    ) = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.eye)
        .setContentTitle("Time Left")
        .setContentText("00:00:00")
        .addAction(android.R.drawable.ic_media_pause,"STOP",pendingIntent1)
        .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    @Named(END_NOTIFICATION_CHANNEL_ID)
    fun provideEndNotificationBuilder(
        @ApplicationContext context: Context,
        @Named(START_TIMER_PENDING_INTENT) pendingIntent: PendingIntent,
        @Named(MAIN_ACTIVITY_PENDING_INTENT) pendingIntent1: PendingIntent,
        notificationSoundUri:Uri
    ) = NotificationCompat.Builder(context, END_NOTIFICATION_CHANNEL_ID)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setOngoing(false)
        .setSmallIcon(R.drawable.eye)
        .setContentTitle("Relax your eyes")
        .setContentText("Look at something 20 ft away for 20 sec")
        .addAction(android.R.drawable.ic_media_play,"START AGAIN",pendingIntent)
        .setVibrate(longArrayOf(0,300,500,300))
        .setSound(notificationSoundUri)
        .setContentIntent(pendingIntent1)

    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @ServiceScoped
    @Provides
    fun providesNotificationSoundUri(
        @ApplicationContext context: Context
    ): Uri {
        return Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://" + context.packageName + "/" + R.raw.notification)
    }
}