package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarDate.Companion.DAYS_IN_WEEK
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.utils.safeLet

internal class WeekDateSelectionStrategy(
    private val adapter: CalendarAdapter,
    private val dateInfoProvider: CalendarView.DateInfoProvider,
    private val firstDayOfWeek: Int?

) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_WEEK_DATE = "ru.cleverpumpkin.calendar.week_date"
    }

    private var selectedWeekDate: CalendarDate? = null

    override fun onDateSelected(date: CalendarDate) {
        if (dateInfoProvider.isDateSelectable(date).not()) {
            return
        }

        if (firstDayOfWeek != null) {
            if (selectedWeekDate?.isInWeek(date, firstDayOfWeek) == true) {
                selectedWeekDate = null
                adapter.notifyDataSetChanged()
            } else {
                selectedWeekDate = date
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun getSelectedDates(): List<CalendarDate> {
        return safeLet(selectedWeekDate, firstDayOfWeek) { date, firstDayOfWeek ->
            val selectedDates = mutableListOf<CalendarDate>()
            val diffStart = if (date.dayOfWeek >= firstDayOfWeek) {
                (date.dayOfWeek - firstDayOfWeek).takeIf { it >= 0 } ?: 0
            } else {
                (DAYS_IN_WEEK - date.dayOfWeek).takeIf { it >= 0 } ?: 0
            }
            val dateFirstDayOfWeek = selectedWeekDate?.plusDays(diffStart * -1)

            repeat(DAYS_IN_WEEK) {
                val tmpDate = dateFirstDayOfWeek?.plusDays(it)
                tmpDate?.let {
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
        return if (selectedWeekDate != null && firstDayOfWeek != null) {
            when {
                dateInfoProvider.isDateSelectable(date).not() -> {
                    false
                }
                selectedWeekDate?.isInWeek(date, firstDayOfWeek) ?: false -> {
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
        bundle.putParcelable(BUNDLE_WEEK_DATE, selectedWeekDate)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        selectedWeekDate = bundle.getParcelable(BUNDLE_WEEK_DATE)
    }
}