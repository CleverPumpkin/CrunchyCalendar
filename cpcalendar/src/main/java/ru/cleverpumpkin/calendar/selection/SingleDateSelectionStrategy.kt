package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

class SingleDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATE = "ru.cleverpumpkin.calendar.selected_date"
    }

    private var selectedDate: CalendarDate? = null

    override fun onDateSelected(date: CalendarDate) {
        if (selectedDate == date) {
            selectedDate = null
        } else {
            val previousSelectedDate = selectedDate
            selectedDate = date

            if (previousSelectedDate != null) {
                val previousSelectedPosition = adapter.findDatePosition(previousSelectedDate)
                if (previousSelectedPosition != -1) {
                    adapter.notifyItemChanged(previousSelectedPosition)
                }
            }
        }

        val selectedDatePosition = adapter.findDatePosition(date)
        if (selectedDatePosition != -1) {
            adapter.notifyItemChanged(selectedDatePosition)
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

    override fun isDateSelected(date: CalendarDate): Boolean {
        return selectedDate == date
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_SELECTED_DATE, selectedDate)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        selectedDate = bundle.getParcelable(BUNDLE_SELECTED_DATE)
    }
}