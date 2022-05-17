package com.rgssdeveloper.eyetimer.model

sealed class TimerEvent{
    object START : TimerEvent()
    object END : TimerEvent()
}