package ru.cleverpumpkin.crunchycalendar.calendarcompose.item

import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import java.util.*

class MonthItem(val id: UUID, val monthTitle: MonthTitleItem, val dates: List<CalendarItem>)

fun List<MonthItem>.findMonthPosition(date: CalendarDate): Int {
    val year = date.year
    val month = date.month

    return this.indexOfFirst { item ->
        if (item.monthTitle.date.year == year && item.monthTitle.date.month == month) {
            return@indexOfFirst true
        }

        return@indexOfFirst false
    }
}