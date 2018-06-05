package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.SimpleLocalDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

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

    override fun getSelectedDates(): List<SimpleLocalDate> {
        val selectedDate = selectedDate

        return if (selectedDate != null) {
            listOf(selectedDate)
        } else {
            emptyList()
        }
    }

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        return selectedDate == date
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putLong(BUNDLE_SELECTED_DATE, selectedDate?.toMillis() ?: UNDEFINED_DATE)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        val dateInMillis = bundle.getLong(BUNDLE_SELECTED_DATE, UNDEFINED_DATE)
        if (dateInMillis != UNDEFINED_DATE) {
            selectedDate = SimpleLocalDate(dateInMillis)
        }
    }
}