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
                rangeStart = calculateRangeStart(minDate = minDate, initialDate = initialDate)
                rangeEnd = rangeStart.plusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate == null && maxDate != null -> {
                rangeEnd = calculateRangeEnd(maxDate = maxDate, initialDate = initialDate)
                rangeStart = rangeEnd.minusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate != null && maxDate != null -> {
                if (initialDate.isBetween(minDate, maxDate)) {
                    rangeStart = calculateRangeStart(minDate = minDate, initialDate = initialDate)
                    rangeEnd = calculateRangeEnd(maxDate = maxDate, initialDate = initialDate)
                } else {
                    rangeStart = minDate
                    rangeEnd = calculateRangeEnd(maxDate = maxDate, initialDate = minDate)
                }
            }

            else -> throw IllegalStateException() // unreachable branch
        }

        return DatesRange(dateFrom = rangeStart, dateTo = rangeEnd)
    }

    private fun calculateRangeStart(
        minDate: CalendarDate,
        initialDate: CalendarDate
    ): CalendarDate {
        val monthsBetween = minDate.monthsBetween(initialDate)

        return if (monthsBetween > CalendarConst.MONTHS_PER_PAGE) {
            initialDate.minusMonths(CalendarConst.MONTHS_PER_PAGE)
        } else {
            minDate
        }
    }

    private fun calculateRangeEnd(
        maxDate: CalendarDate,
        initialDate: CalendarDate
    ): CalendarDate {
        val monthsBetween = initialDate.monthsBetween(maxDate)

        return if (monthsBetween > CalendarConst.MONTHS_PER_PAGE) {
            initialDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        } else {
            maxDate
        }
    }

}