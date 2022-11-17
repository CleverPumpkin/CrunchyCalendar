package ru.cleverpumpkin.crunchycalendar.calendarcompose.utils

import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate

/**
 * Internal interface that defines methods for providing required information
 * for the specific [CalendarDate].
 */
interface DateInfoProvider {

    fun isToday(date: CalendarDate): Boolean

//    fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState

    fun isDateOutOfRange(date: CalendarDate): Boolean

    fun isDateSelectable(date: CalendarDate): Boolean

    fun isWeekend(date: CalendarDate): Boolean

//    fun getDateIndicators(date: CalendarDate): List<CalendarView.DateIndicator>

}