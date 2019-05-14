package ru.cleverpumpkin.calendar.utils

import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView

/**
 * Internal interface that defines methods for providing required information
 * for the specific [CalendarDate].
 */
internal interface DateInfoProvider {

    fun isToday(date: CalendarDate): Boolean

    fun isDateSelected(date: CalendarDate): Boolean

    fun isDateOutOfRange(date: CalendarDate): Boolean

    fun isDateSelectable(date: CalendarDate): Boolean

    fun isWeekend(date: CalendarDate): Boolean

    fun getDateIndicators(date: CalendarDate): List<CalendarView.DateIndicator>

}