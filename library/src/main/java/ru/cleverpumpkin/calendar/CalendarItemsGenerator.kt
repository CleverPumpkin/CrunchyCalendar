package ru.cleverpumpkin.calendar

import ru.cleverpumpkin.calendar.item.CalendarItem
import ru.cleverpumpkin.calendar.item.DateItem
import ru.cleverpumpkin.calendar.item.EmptyItem
import ru.cleverpumpkin.calendar.item.MonthItem
import ru.cleverpumpkin.calendar.utils.monthsBetweenTwoDates
import java.util.*

class CalendarItemsGenerator {

    companion object {
        private const val DAYS_IN_WEEK = 7
    }

    /**
     * List of week days that positioned according with Locale.
     *
     *  For example, for US Locale:
     * [Calendar.SUNDAY], [Calendar.MONDAY], [Calendar.WEDNESDAY],
     * [Calendar.THURSDAY], [Calendar.FRIDAY], [Calendar.SATURDAY]
     *
     * For RU Locale:
     * [Calendar.MONDAY], [Calendar.WEDNESDAY], [Calendar.THURSDAY],
     * [Calendar.FRIDAY], [Calendar.SATURDAY], [Calendar.SUNDAY]
     */
    val positionedDaysOfWeek: List<Int> = mutableListOf<Int>().apply {
        var dayValue = Calendar.getInstance().firstDayOfWeek

        repeat(DAYS_IN_WEEK) {
            if (dayValue > DAYS_IN_WEEK) {
                dayValue = 1
            }

            this += dayValue
            dayValue++
        }
    }

    fun generateCalendarItems(dateFrom: Date, dateTo: Date): List<CalendarItem> {
        val calendar = Calendar.getInstance()
        calendar.time = dateFrom

        val calendarItems = mutableListOf<CalendarItem>()
        val monthsBetween = monthsBetweenTwoDates(startDate = dateFrom, endDate = dateTo)

        repeat(monthsBetween.inc()) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            val monthItem = MonthItem(SimpleLocalDate(calendar.time))
            val itemsForMonth = generateCalendarItemsForMonth(year, month)

            calendarItems += monthItem
            calendarItems += itemsForMonth

            calendar.add(Calendar.MONTH, 1)
        }

        return calendarItems
    }

    private fun generateCalendarItemsForMonth(year: Int, month: Int): List<CalendarItem> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)

        // First day of month - MONDAY, TUESDAY, e.t.c
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val positionOfFirstDayOfMonth = positionedDaysOfWeek.indexOf(firstDayOfMonth)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)

        // Last day of month - MONDAY, TUESDAY, e.t.c
        val lastDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val positionOfLastDayOfMonth = positionedDaysOfWeek.indexOf(lastDayOfMonth)

        val startOffset = positionOfFirstDayOfMonth
        val endOffset = (DAYS_IN_WEEK - positionOfLastDayOfMonth) - 1

        val itemsForMonth = mutableListOf<CalendarItem>()

        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Add empty items for start offset
        repeat(startOffset) { itemsForMonth += EmptyItem }

        // Add day items
        repeat(daysInMonth) {
            val date = SimpleLocalDate(calendar.time)
            itemsForMonth += DateItem(date)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Add empty items for end offset
        repeat(endOffset) { itemsForMonth += EmptyItem }

        return itemsForMonth
    }
}