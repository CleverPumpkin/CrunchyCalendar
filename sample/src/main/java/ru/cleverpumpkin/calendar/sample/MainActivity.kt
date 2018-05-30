package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.CalendarView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = findViewById<CalendarView>(R.id.calendar_view)
        calendarView.init()
    }
}
