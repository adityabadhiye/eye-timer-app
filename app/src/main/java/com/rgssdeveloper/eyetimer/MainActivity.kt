package com.rgssdeveloper.eyetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rgssdeveloper.eyetimer.repository.SettingsManager
import com.rgssdeveloper.eyetimer.screens.HomeScreen
import com.rgssdeveloper.eyetimer.screens.SettingScreen
import com.rgssdeveloper.eyetimer.ui.theme.EyeTimerTheme
import com.rgssdeveloper.eyetimer.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel by lazy { ViewModelProvider(this@MainActivity)
        .get(MainViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager.getInstance(applicationContext)
        setContent {
            val theme = settingsManager.themeFlow.collectAsState(0)
            EyeTimerTheme(when(theme.value){
                0 -> isSystemInDarkTheme()
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
                ) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home_screen"
                ){
                    composable(route = "home_screen"){
                        HomeScreen(navController)
                    }
                    composable(route = "setting_screen"){
                        SettingScreen(navController,mainViewModel)
                    }
                }
            }
        }
    }
}

