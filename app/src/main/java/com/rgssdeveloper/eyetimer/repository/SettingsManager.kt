package com.rgssdeveloper.eyetimer.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

enum class Themes{
    DEFAULT,LIGHT,DARK
}
class SettingsManager constructor(context: Context){
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private val mDataStore = context.dataStore

    val themeFlow: Flow<Int> = mDataStore.data.map {
//        Timber.d(it[THEME_KEY].toString())
       it[THEME_KEY]?:0
    }

    suspend fun getTheme() = mDataStore.data.map { it[THEME_KEY]?:0 }.last()

    suspend fun saveTheme(theme: Int){
        mDataStore.edit {
            it[THEME_KEY] = theme
        }
    }

    companion object {
        val THEME_KEY = intPreferencesKey("theme")
        @Volatile
        private var INSTANCE: SettingsManager? = null

        @Synchronized
        fun getInstance(param: Context): SettingsManager = INSTANCE ?: SettingsManager(param).also { INSTANCE = it }    }
}