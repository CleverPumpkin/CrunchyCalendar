package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.NullableDatesRange
import ru.cleverpumpkin.calendar.SimpleLocalDate

class RangeDateSelectionStrategy(private val adapter: CalendarAdapter) : DateSelectionStrategy {

    companion object {
        private const val BUNDLE_DATES_RANGE = "ru.cleverpumpkin.calendar.dates_range"
    }

    private var datesRange = NullableDatesRange()

    override fun onDateSelected(date: SimpleLocalDate, datePosition: Int) {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        when {
            dateFrom == null && dateTo == null -> {
                datesRange = datesRange.copy(dateFrom = date)

                val position = adapter.findDateItemPosition(date)
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

    override fun isDateSelected(date: SimpleLocalDate): Boolean {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        return if (dateFrom != null && dateTo != null) {
            date >= dateFrom && date <= dateTo
        } else {
            dateFrom == date || dateTo == date
        }
    }

    override fun saveSelectionState(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_DATES_RANGE, datesRange)
    }

    override fun restoreSelectionState(bundle: Bundle) {
        datesRange = bundle.getParcelable(BUNDLE_DATES_RANGE)
    }
}