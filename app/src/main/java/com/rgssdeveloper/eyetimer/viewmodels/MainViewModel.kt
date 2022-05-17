package com.rgssdeveloper.eyetimer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rgssdeveloper.eyetimer.repository.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager
):ViewModel() {
    var currentTheme = settingsManager.themeFlow    //add logic to get from data preferences

    fun getCurrentTheme():Int{
        var res:Int=0
        viewModelScope.launch{
            res=settingsManager.getTheme()
        }
        return res
    }

    fun changeTheme(themeIndex: Int) {
        viewModelScope.launch{
            settingsManager.saveTheme(themeIndex)
        }
//        Timber.d(themeIndex.toString())    //change theme here
    }
}