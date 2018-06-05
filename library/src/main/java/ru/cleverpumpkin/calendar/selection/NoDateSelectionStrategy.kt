package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.SimpleLocalDate

class NoDateSelectionStrategy : DateSelectionStrategy {

    override fun onDateSelected(date: SimpleLocalDate, datePosition: Int) {
        // do nothing
    }

    override fun getSelectedDates(): List<SimpleLocalDate> {
        return emptyList()
    }

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        return false
    }

    override fun saveSelectedDates(bundle: Bundle) {
        // do nothing
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        // do nothing
    }
}