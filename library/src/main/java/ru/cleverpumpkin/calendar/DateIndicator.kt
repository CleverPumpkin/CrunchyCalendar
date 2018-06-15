package ru.cleverpumpkin.calendar

import android.support.annotation.ColorInt

/**
 * This class represents a colored indicator for specific date that will be displayed
 * on the calendar.
 */
class DateIndicator(
    val date: CalendarDate,
    @ColorInt val color: Int
)