package ru.cleverpumpkin.calendar.utils

import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.DatesRange

/**
 * Created by Alexander Surinov on 29/04/2019.
 */
internal object DisplayedDatesRangeFactory {

    private const val MONTHS_PER_PAGE = 6

    fun getDisplayedDatesRange(
        initialDate: CalendarDate,
        minDate: CalendarDate? = null,
        maxDate: CalendarDate? = null

    ): DatesRange {

        val displayDatesFrom: CalendarDate
        val displayDatesTo: CalendarDate

        when {
            minDate == null && maxDate == null -> {
                displayDatesFrom = initialDate.minusMonths(MONTHS_PER_PAGE)
                displayDatesTo = initialDate.plusMonths(MONTHS_PER_PAGE)
            }

            minDate != null && maxDate == null -> {
                displayDatesFrom = minDate
                displayDatesTo = displayDatesFrom.plusMonths(MONTHS_PER_PAGE)
            }

            minDate == null && maxDate != null -> {
                displayDatesFrom = maxDate.minusMonths(MONTHS_PER_PAGE)
                displayDatesTo = maxDate
            }

            minDate != null && maxDate != null -> {
                if (initialDate.isBetween(minDate, maxDate)) {
                    var monthsBetween = minDate.monthsBetween(initialDate)
                    displayDatesFrom = if (monthsBetween > MONTHS_PER_PAGE) {
                        initialDate.minusMonths(MONTHS_PER_PAGE)
                    } else {
                        minDate
                    }

                    monthsBetween = initialDate.monthsBetween(maxDate)
                    displayDatesTo = if (monthsBetween > MONTHS_PER_PAGE) {
                        initialDate.plusMonths(MONTHS_PER_PAGE)
                    } else {
                        maxDate
                    }
                } else {
                    displayDatesFrom = minDate

                    val monthBetween = minDate.monthsBetween(maxDate)
                    displayDatesTo = if (monthBetween > MONTHS_PER_PAGE) {
                        minDate.plusMonths(MONTHS_PER_PAGE)
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