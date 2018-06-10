package ru.cleverpumpkin.calendar.adapter

import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.item.CalendarItem
import ru.cleverpumpkin.calendar.adapter.item.DateItem
import ru.cleverpumpkin.calendar.adapter.item.EmptyItem
import ru.cleverpumpkin.calendar.adapter.item.MonthItem
import java.util.*

/**
 * TODO Describe class
 */
class CalendarItemsGenerator {

    companion object {
        private const val DAYS_IN_WEEK = 7
    }

    /**
     * List of week days that positioned according with Locale.
     *
     * For example, for US Locale:
     * [Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday]
     *
     * For RU Locale:
     * [Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday]
     */
    private val positionedDaysOfWeek: List<Int> = mutableListOf<Int>().apply {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        repeat(DAYS_IN_WEEK) {
            this += calendar.get(Calendar.DAY_OF_WEEK)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    fun generateCalendarItems(dateFrom: CalendarDate, dateTo: CalendarDate): List<CalendarItem> {
        val calendar = Calendar.getInstance()
        calendar.time = dateFrom.date

        val calendarItems = mutableListOf<CalendarItem>()
        val monthsBetween = dateFrom.monthsDiff(dateTo)

        repeat(monthsBetween.inc()) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            val monthItem = MonthItem(CalendarDate(calendar.time))
            val itemsForMonth = generateCalendarItemsForMonth(year, month)

            calendarItems += monthItem
            calendarItems += itemsForMonth

            calendar.add(Calendar.MONTH, 1)
        }

        return calendarItems
    }

    private fun generateCalendarItemsForMonth(year: Int, month: Int): List<CalendarItem> {
        val itemsForMonth = mutableListOf<CalendarItem>()
        val calendar = Calendar.getInstance()

        // Add empty items for start offset
        calendar.set(year, month, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val startOffset = positionedDaysOfWeek.indexOf(firstDayOfMonth)

        repeat(startOffset) { itemsForMonth += EmptyItem }

        // Add date items
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(daysInMonth) {
            val date = CalendarDate(calendar.time)
            itemsForMonth += DateItem(date)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Add empty items for end offset
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)
        val lastDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val positionOfLastDayOfMonth = positionedDaysOfWeek.indexOf(lastDayOfMonth)
        val endOffset = (DAYS_IN_WEEK - positionOfLastDayOfMonth) - 1

        repeat(endOffset) { itemsForMonth += EmptyItem }

        return itemsForMonth
    }
}