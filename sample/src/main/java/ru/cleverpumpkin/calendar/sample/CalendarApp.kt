package ru.cleverpumpkin.calendar.sample

import android.app.Application
import com.google.android.material.color.DynamicColors

class CalendarApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}