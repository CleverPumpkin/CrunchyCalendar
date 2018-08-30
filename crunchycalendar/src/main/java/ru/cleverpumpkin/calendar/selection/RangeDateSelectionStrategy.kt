package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.NullableDatesRange
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import java.util.*

internal class RangeDateSelectionStrategy(
    private val adapter: CalendarAdapter,
    private val dateInfoProvider: CalendarView.DateInfoProvider

) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_DATES_RANGE = "ru.cleverpumpkin.calendar.dates_range"
    }

    private var datesRange = NullableDatesRange()

    override fun onDateSelected(date: CalendarDate) {
        val (dateFrom, dateTo) = datesRange

        when {
            dateFrom == null && dateTo == null -> {
                datesRange = datesRange.copy(dateFrom = date)

                val position = adapter.findDatePosition(date)
                if (position != -1) {
                    adapter.notifyItemChanged(position)
                }
            }

            dateFrom != null && dateTo == null -> {
                if (dateFrom == date) {
                    datesRange = datesRange.copy(dateTo = date)
                    return
                }

                datesRange = if (date < dateFrom) {
                    datesRange.copy(dateFrom = date, dateTo = dateFrom)
                } else {
                    datesRange.copy(dateTo = date)
                }

                adapter.notifyDataSetChanged()
            }

            dateFrom != null && dateTo != null -> {
                datesRange = datesRange.copy(dateFrom = date, dateTo = null)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun getSelectedDates(): List<CalendarDate> {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        return if (dateFrom != null && dateTo != null) {

            if (adapter.findDatePosition(dateFrom) != -1 &&
                adapter.findDatePosition(dateTo) != -1) {

                return adapter.getDateRange(dateFrom = dateFrom, dateTo = dateTo)
                    .filter(dateInfoProvider::isDateSelectable)
            }

            val selectedDates = mutableListOf<CalendarDate>()
            val daysBetween = dateFrom.daysBetween(dateTo)
            val calendar = dateFrom.calendar

            repeat(daysBetween.inc()) {
                val date = CalendarDate(calendar.time)
                if (dateInfoProvider.isDateSelectable(date)) {
                    selectedDates += date
                }

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            selectedDates

        } else if (dateFrom != null) {
            listOf(dateFrom)
        } else {
            emptyList()
        }
    }

    override fun isDateSelected(date: CalendarDate): Boolean {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        return when {
            dateInfoProvider.isDateSelectable(date).not() -> {
                false
            }

            (dateFrom != null && dateTo != null) -> {
                date.isBetween(dateFrom, dateTo)
            }

            else -> {
                dateFrom == date || dateTo == date
            }
        }
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_DATES_RANGE, datesRange)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        datesRange = bundle.getParcelable(BUNDLE_DATES_RANGE)
    }
}