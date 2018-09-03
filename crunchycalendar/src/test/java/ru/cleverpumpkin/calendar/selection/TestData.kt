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
}