package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.DateCellSelectedState
import ru.cleverpumpkin.calendar.adapter.manager.AdapterDataManager
import ru.cleverpumpkin.calendar.utils.DateInfoProvider

internal class SingleDateSelectionStrategy(
    private val adapterDataManager: AdapterDataManager,
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

            if (previousSelectedDate != null) {
                val previousSelectedPosition =
                    adapterDataManager.findDatePosition(previousSelectedDate)
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
            val datePosition = adapterDataManager.findDatePosition(date)
            adapterDataManager.notifyDateItemChanged(datePosition)
        }
    }

}