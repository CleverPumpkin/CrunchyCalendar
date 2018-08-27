package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

class MultipleDateSelectionStrategy(
    private val adapter: CalendarAdapter,
    private val dateInfoProvider: CalendarView.DateInfoProvider
) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATES = "ru.cleverpumpkin.calendar.selected_items"
    }

    private val selectedDates = linkedSetOf<CalendarDate>()

    override fun onDateSelected(date: CalendarDate) {
        if (dateInfoProvider.isDateSelectable(date).not()) {
            return
        }

        val dateWasRemoved = selectedDates.remove(date)
        if (dateWasRemoved.not()) {
            selectedDates.add(date)
        }

        val datePosition = adapter.findDatePosition(date)
        if (datePosition != -1) {
            adapter.notifyItemChanged(datePosition)
        }
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