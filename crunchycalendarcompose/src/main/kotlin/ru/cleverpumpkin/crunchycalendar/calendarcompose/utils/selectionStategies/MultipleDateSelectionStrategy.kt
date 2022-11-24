package ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.selectionStategies

import android.os.Bundle
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateCellSelectedState
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider

internal class MultipleDateSelectionStrategy(
    private val dateInfoProvider: DateInfoProvider

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
    }

    override fun getSelectedDates(): List<CalendarDate> {
        return selectedDates.toList()
    }

    override fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState {
        return if(selectedDates.contains(date)) DateCellSelectedState.SINGLE else DateCellSelectedState.NOT_SELECTED
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelableArray(BUNDLE_SELECTED_DATES, selectedDates.toTypedArray())
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        val selectedDatesArray = bundle.getParcelableArray(BUNDLE_SELECTED_DATES)
        selectedDatesArray?.mapTo(selectedDates) { it as CalendarDate }
    }

    override fun clear() {
        selectedDates.clear()
    }

}