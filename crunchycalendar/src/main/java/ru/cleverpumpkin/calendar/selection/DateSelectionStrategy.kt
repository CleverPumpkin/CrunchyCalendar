package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate

/**
 * Basic internal interface for date selection strategy.
 */
internal interface DateSelectionStrategy {

    /**
     * This method will be invoked when date selected
     */
    fun onDateSelected(date: CalendarDate)

    /**
     * Returns `true` if the [date] is selected
     */
    fun isDateSelected(date: CalendarDate): Boolean

    /**
     * Returns list of selected dates
     */
    fun getSelectedDates(): List<CalendarDate>

    /**
     * This method will be invoked when CalendarView save its state
     */
    fun saveSelectedDates(bundle: Bundle)

    /**
     * This method will be invoked when CalendarView restore its state
     */
    fun restoreSelectedDates(bundle: Bundle)
}