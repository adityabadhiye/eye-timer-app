package com.rgssdeveloper.eyetimer.di

import android.content.Context
import com.rgssdeveloper.eyetimer.repository.SettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideSettingsManager(
        @ApplicationContext context: Context
    ): SettingsManager = SettingsManager.getInstance(context)

//    @Provides
//    fun providesTotalTime():Long = 12000L
}