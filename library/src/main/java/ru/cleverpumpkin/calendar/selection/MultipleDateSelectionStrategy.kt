package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarAdapter
import ru.cleverpumpkin.calendar.SimpleLocalDate

class MultipleDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATES = "ru.cleverpumpkin.calendar.selected_items"
    }

    private val selectedDates = linkedSetOf<SimpleLocalDate>()

    override fun onDateSelected(date: SimpleLocalDate, position: Int) {
        if (selectedDates.remove(date)) {
            adapter.notifyItemChanged(position)
        } else {
            selectedDates.add(date)
            adapter.notifyItemChanged(position)
        }
    }

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        return selectedDates.contains(date)
    }

    override fun saveSelectionState(bundle: Bundle) {
        val longArray = LongArray(selectedDates.size)
        selectedDates.forEachIndexed { i, localDate ->
            longArray[i] = localDate.toDate().time
        }

        bundle.putLongArray(BUNDLE_SELECTED_DATES, longArray)
    }

    override fun restoreSelectionState(bundle: Bundle) {
        selectedDates.clear()
        val selectedDatesAsLongArray = bundle.getLongArray(BUNDLE_SELECTED_DATES)
        selectedDatesAsLongArray.mapTo(selectedDates) { SimpleLocalDate(it) }
    }
}