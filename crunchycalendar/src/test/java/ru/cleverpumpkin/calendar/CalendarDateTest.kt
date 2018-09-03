package ru.cleverpumpkin.calendar

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class CalendarDateTest {

    @Test
    fun `Test months between two nearest months`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(1, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Test months between two months with one year difference`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.MAY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(12, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Test months between when endDate before startDate`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 10)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MARCH, 10)
        val endDate = CalendarDate(calendar.time)

        assertEquals(0, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Test months between when start date - 31 December of 2018 and end date - 1 January of 2019`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.DECEMBER, 31)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.JANUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(1, startDate.monthsBetween(endDate))
    }

    @Test
    fun `Test days between when endDate before startDate`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.MAY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MARCH, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(0, startDate.daysBetween(endDate))
    }

    @Test
    fun `Test days between two nearest months`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.FEBRUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MARCH, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(28, startDate.daysBetween(endDate))
    }

    @Test
    fun `Test days between with one year difference`() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.JANUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.JANUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(365, startDate.daysBetween(endDate))
    }

    @Test
    fun `Test days between with one year difference for a leap year`() {
        val calendar = Calendar.getInstance()

        calendar.set(2016, Calendar.JANUARY, 1)
        val startDate = CalendarDate(calendar.time)

        calendar.set(2017, Calendar.JANUARY, 1)
        val endDate = CalendarDate(calendar.time)

        assertEquals(366, startDate.daysBetween(endDate))
    }
}