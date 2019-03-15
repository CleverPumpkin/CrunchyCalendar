package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import android.util.Log
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarDate.Companion.DAYS_IN_WEEK
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.utils.safeLet

internal class WeekDateSelectionStrategy(
    private val adapter: CalendarAdapter,
    private val dateInfoProvider: CalendarView.DateInfoProvider,
    private val firstDayWeekSelectionMode: Int?

) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_WEEK_DATE = "ru.cleverpumpkin.calendar.week_date"
    }

    private var selectedDate: CalendarDate? = null

    override fun onDateSelected(date: CalendarDate) {
        if (dateInfoProvider.isDateSelectable(date).not()) {
            return
        }

        if (firstDayWeekSelectionMode != null) {
            if (selectedDate?.isInWeek(date, firstDayWeekSelectionMode) == true) {
                selectedDate = null
                adapter.notifyDataSetChanged()
            } else {
                selectedDate = date
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun getSelectedDates(): List<CalendarDate> {
        return safeLet(
            selectedDate,
            firstDayWeekSelectionMode
        ) { selectedDate, firstDayWeekSelectionMode ->
            val selectedDates = mutableListOf<CalendarDate>()
            val dateFirstDayOfWeek = selectedDate.getFirstDayOfWeek(firstDayWeekSelectionMode)

            repeat(DAYS_IN_WEEK) {
                val tmpDate = dateFirstDayOfWeek.plusDays(it)
                tmpDate.let {
                    if (dateInfoProvider.isDateSelectable(tmpDate)) {
                        selectedDates += tmpDate
                    }
                }
            }
            selectedDates
        } ?: run {
            return emptyList()
        }
    }

    override fun isDateSelected(date: CalendarDate): Boolean {
        return if (selectedDate != null && firstDayWeekSelectionMode != null) {
            when {
                dateInfoProvider.isDateSelectable(date).not() -> {
                    false
                }
                selectedDate?.isInWeek(date, firstDayWeekSelectionMode) ?: false -> {
                    true
                }
                else -> {
                    false
                }
            }
        } else {
            false
        }
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_WEEK_DATE, selectedDate)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        selectedDate = bundle.getParcelable(BUNDLE_WEEK_DATE)
    }
}