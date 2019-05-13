package ru.cleverpumpkin.calendar.utils

import org.junit.Assert
import org.junit.Test
import ru.cleverpumpkin.calendar.CalendarConst
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.DatesRange

/**
 * Set of unit tests for the [DisplayedDatesRangeFactory] class.
 *
 * Each test contains:
 * 1. `Given` section, where we prepare test object and test data
 * 2. `When` section, where we run test method on the test object
 * 3. `Then` section, where we check results
 *
 * Created by Alexander Surinov on 29/04/2019.
 */
class DisplayedDatesRangeProviderTest {

    @Test
    fun `Get displayed dates range when min and max dates aren't defined, as a result correct dates range is returned`() {
        // Given
        val today = CalendarDate.today

        val expectedDatesRange = DatesRange(
            dateFrom = today.minusMonths(CalendarConst.MONTHS_PER_PAGE),
            dateTo = today.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = CalendarDate.today,
            minDate = null,
            maxDate = null
        )

        // Then
        Assert.assertTrue(
            "Unexpected actual dates range: $actualDatesRange, expected: $expectedDatesRange",
            expectedDatesRange == actualDatesRange
        )
    }

    @Test
    fun `Get displayed dates range when min date isn't defined and max date is defined, as a result correct dates range is returned`() {
        // Given
        val minDate = CalendarDate.today

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = CalendarDate.today,
            minDate = minDate,
            maxDate = null
        )

        // Then
        Assert.assertTrue(
            "Unexpected actual dates range: $actualDatesRange, expected: $expectedDatesRange",
            expectedDatesRange == actualDatesRange
        )
    }

    @Test
    fun `Get displayed dates range when min date is defined and max date isn't defined, as a result correct dates range is returned`() {
        // Given
        val maxDate = CalendarDate.today

        val expectedDatesRange = DatesRange(
            dateFrom = maxDate.minusMonths(CalendarConst.MONTHS_PER_PAGE),
            dateTo = maxDate
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = CalendarDate.today,
            minDate = null,
            maxDate = maxDate
        )

        // Then
        Assert.assertTrue(
            "Unexpected actual dates range: $actualDatesRange, expected: $expectedDatesRange",
            expectedDatesRange == actualDatesRange
        )
    }

    @Test
    fun `Get displayed dates range when min-max dates are defined, initial dates is out of min-max date boundaries and days between min-max dates more then 6 month, as a result correct dates range is returned`() {
        // Given
        val minDate = CalendarDate.today
        val maxDate = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE * 2)

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = CalendarDate.today,
            minDate = minDate,
            maxDate = maxDate
        )

        // Then
        Assert.assertTrue(
            "Unexpected actual dates range: $actualDatesRange, expected: $expectedDatesRange",
            expectedDatesRange == actualDatesRange
        )
    }

    @Test
    fun `Get displayed dates range when min-max dates are defined, initial dates is out of min-max date boundaries and days between min-max dates less then 6 month, as a result correct dates range is returned`() {
        // Given
        val minDate = CalendarDate.today
        val maxDate = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE / 2)

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = maxDate
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = CalendarDate.today,
            minDate = minDate,
            maxDate = maxDate
        )

        // Then
        Assert.assertTrue(
            "Unexpected actual dates range: $actualDatesRange, expected: $expectedDatesRange",
            expectedDatesRange == actualDatesRange
        )
    }

}