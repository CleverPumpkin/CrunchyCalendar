package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.NullableDatesRange
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

class RangeDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_DATES_RANGE = "ru.cleverpumpkin.calendar.dates_range"
    }

    private var datesRange = NullableDatesRange()

    override fun onDateSelected(date: CalendarDate) {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        when {
            dateFrom == null && dateTo == null -> {
                datesRange = datesRange.copy(dateFrom = date)

                val position = adapter.findDatePosition(date)
                adapter.notifyItemChanged(position)
            }

            dateFrom != null && dateTo == null -> {
                if (dateFrom == date) {
                    return
                }

                datesRange = if (date < dateFrom) {
                    datesRange.copy(dateFrom = date)
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
            adapter.getDateRange(dateFrom = dateFrom, dateTo = dateTo)
        } else {
            emptyList()
        }
    }

    override fun isDateSelected(date: CalendarDate): Boolean {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        return if (dateFrom != null && dateTo != null) {
            date >= dateFrom && date <= dateTo
        } else {
            dateFrom == date || dateTo == date
        }
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_DATES_RANGE, datesRange)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        datesRange = bundle.getParcelable(BUNDLE_DATES_RANGE)
    }
}