package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

class MultipleDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATES = "ru.cleverpumpkin.calendar.selected_items"
    }

    private val selectedDates = linkedSetOf<CalendarDate>()

    override fun onDateSelected(date: CalendarDate) {
        if (selectedDates.remove(date).not()) {
            selectedDates.add(date)
        }

        val datePosition = adapter.findDateItemPosition(date)
        adapter.notifyItemChanged(datePosition)
    }

    override fun getSelectedDates(): List<CalendarDate> {
        return selectedDates.toList()
    }

    override fun isDateSelected(date: CalendarDate): Boolean {
        return selectedDates.contains(date)
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelableArray(BUNDLE_SELECTED_DATES, selectedDates.toTypedArray())
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        val selectedDatesArray = bundle.getParcelableArray(BUNDLE_SELECTED_DATES)
        selectedDatesArray.mapTo(selectedDates) { it as CalendarDate }
    }
}