package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.CalendarView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val calendarView = findViewById<CalendarView>(R.id.calendar_view)

            val calendar = Calendar.getInstance()
            calendar.set(2018, 5, 1)
            val minDate = calendar.time

            calendar.set(2018, 7, 1)
            val maxDate = calendar.time

            calendar.set(2018, 6, 1)
            val initialDate = calendar.time

            calendarView.init(initialDate = initialDate, minDate = null, maxDate = maxDate)
        }
    }
}
