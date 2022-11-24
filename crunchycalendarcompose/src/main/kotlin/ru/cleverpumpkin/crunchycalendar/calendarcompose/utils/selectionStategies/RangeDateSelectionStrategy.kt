package ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.selectionStategies

import android.os.Bundle
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateCellSelectedState
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.NullableDatesRange
import java.util.*

internal class RangeDateSelectionStrategy(
    private val dateInfoProvider: DateInfoProvider

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
            }

            dateFrom != null && dateTo != null -> {
                datesRange = datesRange.copy(dateFrom = date, dateTo = null)
            }
        }
    }

    override fun getSelectedDates(): List<CalendarDate> {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        return if (dateFrom != null && dateTo != null) {

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

    override fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState {
        val dateFrom = datesRange.dateFrom
        val dateTo = datesRange.dateTo

        return when {
            dateInfoProvider.isDateSelectable(date).not() -> {
                DateCellSelectedState.NOT_SELECTED
            }

            (dateFrom != null && dateTo != null) -> {
                when {
                    date == dateFrom && date == dateTo -> {
                        DateCellSelectedState.SINGLE
                    }
                    date == dateFrom -> {
                        DateCellSelectedState.SELECTION_START
                    }
                    date == dateTo -> {
                        DateCellSelectedState.SELECTION_END
                    }
                    date.isBetween(dateFrom, dateTo) -> {
                        DateCellSelectedState.SELECTED
                    }
                    else -> {
                        DateCellSelectedState.NOT_SELECTED
                    }
                }
            }

            else -> {
                when {
                    dateFrom == date || dateTo == date -> {
                        DateCellSelectedState.SINGLE
                    }
                    else -> {
                        DateCellSelectedState.NOT_SELECTED
                    }
                }
            }
        }
    }

    override fun saveSelectedDates(bundle: Bundle) {
        bundle.putParcelable(BUNDLE_DATES_RANGE, datesRange)
    }

    override fun restoreSelectedDates(bundle: Bundle) {
        datesRange = bundle.getParcelable(BUNDLE_DATES_RANGE) ?: datesRange
    }

    override fun clear() {
        datesRange = NullableDatesRange()
    }

}