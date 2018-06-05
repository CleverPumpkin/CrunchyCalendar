package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.SimpleLocalDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

class MultipleDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATES = "ru.cleverpumpkin.calendar.selected_items"
    }

    private val selectedDates = linkedSetOf<SimpleLocalDate>()

    override fun onDateSelected(date: SimpleLocalDate, datePosition: Int) {
        if (selectedDates.remove(date).not()) {
            selectedDates.add(date)
        }

        adapter.notifyItemChanged(datePosition)
    }

    override fun getSelectedDates(): List<SimpleLocalDate> {
        return selectedDates.toList()
    }

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        return selectedDates.contains(date)
    }

    override fun saveSelectedDates(bundle: Bundle) {
        val longArray = LongArray(selectedDates.size)

        selectedDates.forEachIndexed { i, selectedDate ->
            longArray[i] = selectedDate.toMillis()
        }

        bundle.putLongArray(BUNDLE_SELECTED_DATES, longArray)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        val selectedDatesAsLongArray = bundle.getLongArray(BUNDLE_SELECTED_DATES)
        selectedDatesAsLongArray.mapTo(selectedDates) { SimpleLocalDate(it) }
    }
}