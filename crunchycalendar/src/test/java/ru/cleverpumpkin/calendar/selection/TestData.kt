package ru.cleverpumpkin.calendar.selection

import ru.cleverpumpkin.calendar.CalendarDate
import java.util.*

object TestData {

    val date: CalendarDate
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(2018, Calendar.JANUARY, 1)
            return CalendarDate(calendar.time)
        }

    fun generateDatesBetween(dateFrom: CalendarDate, dateTo: CalendarDate): List<CalendarDate> {
        val dates = mutableListOf<CalendarDate>()
        val daysBetween = dateFrom.daysBetween(dateTo)
        val calendar = dateFrom.calendar

        repeat(daysBetween.inc()) {
            dates += CalendarDate(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }
}