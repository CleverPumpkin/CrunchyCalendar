package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate

/**
 * Empty date selection strategy implementation that do nothing.
 */
internal class NoDateSelectionStrategy : DateSelectionStrategy {

    override fun onDateSelected(date: CalendarDate) {
        // do nothing
    }

    override fun getSelectedDates(): List<CalendarDate> {
        return emptyList()
    }

    override fun isDateSelected(date: CalendarDate): Boolean {
        return false
    }

    override fun saveSelectedDates(bundle: Bundle) {
        // do nothing
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        // do nothing
    }
}