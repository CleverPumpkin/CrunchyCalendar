package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.adapter.manager.AdapterDataManager

internal class SingleDateSelectionStrategy(
    private val adapterDataManager: AdapterDataManager,
    private val dateInfoProvider: CalendarView.DateInfoProvider

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

            if (previousSelectedDate != null) {
                val previousSelectedPosition = adapterDataManager.findDatePosition(previousSelectedDate)
                if (previousSelectedPosition != -1) {
                    adapterDataManager.notifyDateItemChanged(previousSelectedPosition)
                }
            }
        }

        val selectedDatePosition = adapterDataManager.findDatePosition(date)
        if (selectedDatePosition != -1) {
            adapterDataManager.notifyDateItemChanged(selectedDatePosition)
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