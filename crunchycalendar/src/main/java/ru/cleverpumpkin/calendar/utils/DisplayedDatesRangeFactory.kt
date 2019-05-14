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

        val displayDatesFrom: CalendarDate
        val displayDatesTo: CalendarDate

        when {
            minDate == null && maxDate == null -> {
                displayDatesFrom = initialDate.minusMonths(CalendarConst.MONTHS_PER_PAGE)
                displayDatesTo = initialDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate != null && maxDate == null -> {
                displayDatesFrom = minDate
                displayDatesTo = displayDatesFrom.plusMonths(CalendarConst.MONTHS_PER_PAGE)
            }

            minDate == null && maxDate != null -> {
                displayDatesFrom = maxDate.minusMonths(CalendarConst.MONTHS_PER_PAGE)
                displayDatesTo = maxDate
            }

            minDate != null && maxDate != null -> {
                if (initialDate.isBetween(minDate, maxDate)) {
                    var monthsBetween = minDate.monthsBetween(initialDate)
                    displayDatesFrom = if (monthsBetween > CalendarConst.MONTHS_PER_PAGE) {
                        initialDate.minusMonths(CalendarConst.MONTHS_PER_PAGE)
                    } else {
                        minDate
                    }

                    monthsBetween = initialDate.monthsBetween(maxDate)
                    displayDatesTo = if (monthsBetween > CalendarConst.MONTHS_PER_PAGE) {
                        initialDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
                    } else {
                        maxDate
                    }
                } else {
                    displayDatesFrom = minDate

                    val monthBetween = minDate.monthsBetween(maxDate)
                    displayDatesTo = if (monthBetween > CalendarConst.MONTHS_PER_PAGE) {
                        minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
                    } else {
                        maxDate
                    }
                }
            }

            else -> throw IllegalStateException() // unreachable branch
        }

        return DatesRange(dateFrom = displayDatesFrom, dateTo = displayDatesTo)
    }

}