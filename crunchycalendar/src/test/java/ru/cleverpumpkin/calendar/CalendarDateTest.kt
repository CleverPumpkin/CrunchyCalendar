package ru.cleverpumpkin.calendar

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Set of unit tests for a [CalendarDate] class
 */
class CalendarDateTest {

    @Test
    fun `Difference between two neighbor months should be 1 month`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.JANUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.FEBRUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(1, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Difference between two months with one year difference should be 12 months`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.MAY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(12, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Difference between two months when startDate is greater than endDate should be 0 months`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 10)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MARCH, 10)
        val endDate = CalendarDate(calendar.time)

        assertEquals(0, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Difference between two months when startDate is the last date of the year and endDate is the first date of the next year should be 1 month`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.DECEMBER, 31)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.JANUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(1, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Difference between two dates when startDate is greater than endDate should be 0 days`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MARCH, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(0, startDate.daysBetween(endDate))
    }

    @Test
    fun `Difference between February and March should be 28 days`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.FEBRUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MARCH, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(28, startDate.daysBetween(endDate))
    }

    @Test
    fun `Difference between two dates with one year difference should be 365 days`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.JANUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.JANUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(365, startDate.daysBetween(endDate))
    }

    @Test
    fun `Difference between two dates with one leap year difference should be 366 days`() {
        val calendar = Calendar.getInstance()

        calendar.set(2016, Calendar.JANUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2017, Calendar.JANUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(366, startDate.daysBetween(endDate))
    }
}