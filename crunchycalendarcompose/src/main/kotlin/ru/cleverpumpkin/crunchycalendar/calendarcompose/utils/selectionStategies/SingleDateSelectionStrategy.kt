package ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.selectionStategies

import android.os.Bundle
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateCellSelectedState
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider

internal class SingleDateSelectionStrategy(
    private val dateInfoProvider: DateInfoProvider

) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATE = "ru.cleverpumpkin.calendar.selected_date"
    }

    private var selectedDate: CalendarDate? = null

    override fun onDateSelected(date: CalendarDate) {
        if (dateInfoProvider.isDateSelectable(date).not()) {
            return
        }

        if (selectedDate == date) {
            selectedDate = null
        } else {
            val previousSelectedDate = selectedDate
            selectedDate = date
        }


    }

    override fun getSelectedDates(): List<CalendarDate> {
        val selectedDate = selectedDate

        return if (selectedDate != null) {
            listOf(selectedDate)
        } else {
            emptyList()
        }
    }

    override fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState {
        return if(selectedDate == date) DateCellSelectedState.SINGLE else DateCellSelectedState.NOT_SELECTED
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_SELECTED_DATE, selectedDate)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        selectedDate = bundle.getParcelable(BUNDLE_SELECTED_DATE)
    }

    override fun clear() {
        val date = selectedDate
        if (date != null) {
            selectedDate = null
        }
    }

}