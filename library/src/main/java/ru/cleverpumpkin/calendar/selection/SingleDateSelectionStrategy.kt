package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.SimpleLocalDate

class SingleDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_SELECTED_DATE = "ru.cleverpumpkin.calendar.selected_date"
        private const val UNDEFINED_DATE = -1L
    }

    private var selectedDate: SimpleLocalDate? = null

    override fun onDateSelected(date: SimpleLocalDate, datePosition: Int) {
        val previousSelectedDate = selectedDate

        if (previousSelectedDate != null) {
            val previousSelectedPosition = adapter.findDateItemPosition(previousSelectedDate)
            if (previousSelectedPosition != -1) {
                adapter.notifyItemChanged(previousSelectedPosition)
            }
        }

        selectedDate = date
        adapter.notifyItemChanged(datePosition)
    }

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        return selectedDate == date
    }

    override fun saveSelectionState(bundle: Bundle) {
        bundle.putLong(BUNDLE_SELECTED_DATE, selectedDate?.toDate()?.time ?: UNDEFINED_DATE)
    }

    override fun restoreSelectionState(bundle: Bundle) {
        val dateInMillis = bundle.getLong(BUNDLE_SELECTED_DATE, UNDEFINED_DATE)
        if (dateInMillis != UNDEFINED_DATE) {
            selectedDate = SimpleLocalDate(dateInMillis)
        }
    }
}