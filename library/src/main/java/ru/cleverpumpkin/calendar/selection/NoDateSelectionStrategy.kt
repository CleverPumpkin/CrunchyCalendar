package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.SimpleLocalDate

class NoDateSelectionStrategy : DateSelectionStrategy {

    override fun onDateSelected(date: SimpleLocalDate, position: Int) {
        // do nothing
    }

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        return false
    }

    override fun saveSelectionState(bundle: Bundle) {
        // do nothing
    }

    override fun restoreSelectionState(bundle: Bundle) {
        // do nothing
    }
}