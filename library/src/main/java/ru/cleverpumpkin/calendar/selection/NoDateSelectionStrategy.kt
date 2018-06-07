package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate

class NoDateSelectionStrategy : DateSelectionStrategy {

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