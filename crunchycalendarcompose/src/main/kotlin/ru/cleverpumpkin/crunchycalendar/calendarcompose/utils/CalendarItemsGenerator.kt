package ru.cleverpumpkin.crunchycalendar.calendarcompose.utils

import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarConst
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.*

import java.util.*

class CalendarItemsGenerator(private val firstDayOfWeek: Int) {

    /**
     * List of days of week according to [firstDayOfWeek].
     *
     * For example, when [firstDayOfWeek] is [Calendar.MONDAY] list looks like:
     * [MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY]
     */
    private val daysOfWeek = mutableListOf<Int>().apply {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek)

        repeat(CalendarConst.DAYS_IN_WEEK) {
            this += calendar.get(Calendar.DAY_OF_WEEK)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    /**
     * Generate calendar items for months between [dateFrom] and [dateTo]
     */
    fun generateCalendarItems(dateFrom: CalendarDate, dateTo: CalendarDate): List<MonthItem> {
        val calendar = Calendar.getInstance()
        calendar.time = dateFrom.date

        val calendarItems = mutableListOf<MonthItem>()
        val monthsBetween = dateFrom.monthsBetween(dateTo)

        repeat(monthsBetween.inc()) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            val monthTitleItem = MonthTitleItem(CalendarDate(calendar.time))
            val itemsForMonth = generateCalendarItemsForMonth(year, month)

            calendarItems.add(MonthItem(UUID.randomUUID(), monthTitleItem, itemsForMonth))

            calendar.add(Calendar.MONTH, 1)
        }

        return calendarItems
    }

    private fun generateCalendarItemsForMonth(year: Int, month: Int): List<CalendarItem> {
        val itemsForMonth = mutableListOf<CalendarItem>()

        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = firstDayOfWeek

        calendar.set(year, month, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val startOffset = daysOfWeek.indexOf(firstDayOfMonth)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)
        val lastDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val endOffset = CalendarConst.DAYS_IN_WEEK.dec() - daysOfWeek.indexOf(lastDayOfMonth)

        // Add empty items for start offset
        repeat(startOffset) { itemsForMonth += EmptyItem() }

        calendar.set(year, month, 1)

        // Add date items
        repeat(daysInMonth) {
            val date = CalendarDate(calendar.time)
            itemsForMonth += DateItem(date)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Add empty items for end offset
        repeat(endOffset) { itemsForMonth += EmptyItem() }

        return itemsForMonth
    }

}