package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.DateCellSelectedState

/**
 * Empty date selection strategy implementation that do nothing.
 */
internal class NoDateSelectionStrategy : DateSelectionStrategy {

    override fun onDateSelected(date: CalendarDate) {
        // Do nothing.
    }

    override fun getSelectedDates(): List<CalendarDate> {
        return emptyList()
    }

    override fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState {
        return DateCellSelectedState.NOT_SELECTED
    }

    override fun saveSelectedDates(bundle: Bundle) {
        // Do nothing.
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        // Do nothing.
    }

    override fun clear() {
        // Do nothing.
    }

}