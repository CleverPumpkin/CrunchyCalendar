package ru.cleverpumpkin.calendar.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = findViewById<CalendarView>(R.id.calendar_view)

        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.MAY, 2)
        val minDate = calendar.time

        calendar.set(2018, Calendar.JUNE, 28)
        val maxDate = calendar.time

        calendar.set(2018, Calendar.JUNE, 1)
        val initialDate = calendar.time

        if (savedInstanceState == null) {
            calendarView.setup(
                initialDate = CalendarDate(initialDate),
                minDate = null,
                maxDate = null,
                selectionMode = CalendarView.SelectionMode.MULTIPLE
            )
        }

        val events = generateCalendarEvents()
        val groupedEvents = events.groupBy { it.calendarDate }

        val eventsDateItemDecoration = EventsDateItemDecoration(
            context = this,
            groupedEvents = groupedEvents
        )

        calendarView.addCustomItemDecoration(eventsDateItemDecoration)
    }

    private fun generateCalendarEvents(): List<CalendarEvent> {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 15)

        val event1 = CalendarEvent(
            calendarDate = CalendarDate(calendar.time),
            color = Color.RED
        )

        val event2 = CalendarEvent(
            calendarDate = CalendarDate(calendar.time),
            color = Color.GREEN
        )

        calendar.set(2018, Calendar.JUNE, 18)
        val event3 = CalendarEvent(
            calendarDate = CalendarDate(calendar.time),
            color = Color.RED
        )

        return listOf(event1, event2, event3)
    }
}