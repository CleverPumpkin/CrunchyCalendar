package ru.cleverpumpkin.calendar.utils

import ru.cleverpumpkin.calendar.CalendarConst
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.DatesRange

/**
 * This class responsible for providing [DatesRange] according to incoming params.
 */
internal object DisplayedDatesRangeFactory {

    fun getDisplayedDatesRange(
        initialDate: CalendarDate,
        minDate: CalendarDate? = null,
        maxDate: CalendarDate? = null

    ): DatesRange {

        val rangeStart: CalendarDate
        val rangeEnd: CalendarDate

        when {
            minDate == null && maxDate == null -> {
                rangeStart = initialDate.minusMonths(CalendarConst.MONTHS_PER_PAGE)
                rangeEnd = initialDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate != null && maxDate == null -> {
                rangeStart = calculateRangeStart(dateFrom = minDate, dateTo = initialDate)
                rangeEnd = rangeStart.plusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate == null && maxDate != null -> {
                rangeEnd = calculateRangeEnd(dateFrom = initialDate, dateTo = maxDate)
                rangeStart = rangeEnd.minusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate != null && maxDate != null -> {
                if (initialDate.isBetween(minDate, maxDate)) {
                    rangeStart = calculateRangeStart(dateFrom = minDate, dateTo = initialDate)
                    rangeEnd = calculateRangeEnd(dateFrom = initialDate, dateTo = maxDate)
                } else {
                    rangeStart = minDate
                    rangeEnd = calculateRangeEnd(dateFrom = minDate, dateTo = maxDate)
                }
            }

            else -> throw IllegalStateException() // unreachable branch
        }

        return DatesRange(dateFrom = rangeStart, dateTo = rangeEnd)
    }

    private fun calculateRangeStart(
        dateFrom: CalendarDate,
        dateTo: CalendarDate
    ): CalendarDate {
        return if (dateFrom.monthsBetween(dateTo) > CalendarConst.MONTHS_PER_PAGE) {
            dateTo.minusMonths(CalendarConst.MONTHS_PER_PAGE)
        } else {
            dateFrom
        }
    }

    private fun calculateRangeEnd(
        dateFrom: CalendarDate,
        dateTo: CalendarDate
    ): CalendarDate {
        return if (dateFrom.monthsBetween(dateTo) > CalendarConst.MONTHS_PER_PAGE) {
            dateFrom.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        } else {
            dateTo
        }
    }

}