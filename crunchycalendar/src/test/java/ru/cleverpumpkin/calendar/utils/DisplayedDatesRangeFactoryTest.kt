package ru.cleverpumpkin.calendar.utils

import org.junit.Assert
import org.junit.Test
import ru.cleverpumpkin.calendar.CalendarConst
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.DatesRange
import java.util.*

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
class DisplayedDatesRangeFactoryTest {

    @Test
    fun `Get displayed dates range when min and max dates aren't defined, as a result correct dates range is returned`() {
        // Given
        val initialDate = CalendarDate.today

        val expectedDatesRange = DatesRange(
            dateFrom = initialDate.minusMonths(CalendarConst.MONTHS_PER_PAGE),
            dateTo = initialDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min date is defined and max date isn't defined, as a result correct dates range is returned`() {
        // Given
        val minDate = CalendarDate.today
        val initialDate = CalendarDate.today

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min date isn't defined and max date is defined, as a result correct dates range is returned`() {
        // Given
        val maxDate = CalendarDate.today
        val initialDate = CalendarDate.today

        val expectedDatesRange = DatesRange(
            dateFrom = maxDate.minusMonths(CalendarConst.MONTHS_PER_PAGE),
            dateTo = maxDate
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min date is defined, max date isn't defined and initial date is far from min date, as a result correct dates range is returned`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2019, Calendar.JANUARY, 1)
        val minDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.AUGUST, 1)
        val initialDate = CalendarDate(calendar.time)

        val expectedDatesRange = DatesRange(
            dateFrom = initialDate.minusMonths(CalendarConst.MONTHS_PER_PAGE),
            dateTo = initialDate
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min date isn't defined, max date is defined and initial date is far from max date, as a result correct dates range is returned`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2019, Calendar.AUGUST, 1)
        val maxDate = CalendarDate(calendar.time)

        calendar.set(2019, Calendar.JANUARY, 1)
        val initialDate = CalendarDate(calendar.time)

        val expectedDatesRange = DatesRange(
            dateFrom = initialDate,
            dateTo = initialDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min date isn't defined, max date is defined and initial date is more then max date, as a result correct dates range is returned`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2019, Calendar.AUGUST, 1)
        val maxDate = CalendarDate(calendar.time)

        val initialDate = maxDate.plusMonths(1)

        val expectedDatesRange = DatesRange(
            dateFrom = maxDate.minusMonths(CalendarConst.MONTHS_PER_PAGE),
            dateTo = maxDate
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min date is defined, max date isn't defined and initial date is less then min date, as a result correct dates range is returned`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2019, Calendar.JANUARY, 1)
        val minDate = CalendarDate(calendar.time)

        val initialDate = minDate.minusMonths(1)

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
    fun `Get displayed dates range when min-max dates are defined, initial dates is out of min-max date boundaries and days between min-max dates more then 6 month, as a result correct dates range is returned`() {
        // Given
        val minDate = CalendarDate.today
        val maxDate = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE * 2)
        val initialDate = maxDate.plusMonths(1)

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = minDate.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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
        val initialDate = maxDate.plusMonths(1)

        val expectedDatesRange = DatesRange(
            dateFrom = minDate,
            dateTo = maxDate
        )

        // When
        val actualDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
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