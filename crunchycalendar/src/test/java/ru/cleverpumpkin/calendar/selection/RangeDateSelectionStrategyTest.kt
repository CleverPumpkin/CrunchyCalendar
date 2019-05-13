package ru.cleverpumpkin.calendar.selection

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.manager.AdapterDataManager
import ru.cleverpumpkin.calendar.anyNotNullObject
import ru.cleverpumpkin.calendar.utils.DateInfoProvider
import java.util.*

/**
 * Set of unit tests for the [RangeDateSelectionStrategy] class.
 *
 * Each test contains:
 * 1. `Given` section, where we prepare test object and test data
 * 2. `When` section, where we run test method on the test object
 * 3. `Then` section, where we check results
 */
class RangeDateSelectionStrategyTest {

    @Mock private lateinit var adapterDataManager: AdapterDataManager
    @Mock private lateinit var dateInfoProvider: DateInfoProvider

    private lateinit var dateSelectionStrategy: DateSelectionStrategy

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dateSelectionStrategy = RangeDateSelectionStrategy(adapterDataManager, dateInfoProvider)
    }

    @Test
    fun `Select a range when dateFrom is less than dateTo and all dates in the range are selectable, as a result - all dates in the range are selected`() {
        // Given
        val dateFrom = TestData.date
        val dateTo = dateFrom.plusMonths(1)

        `when`(dateInfoProvider.isDateSelectable(anyNotNullObject()))
            .thenReturn(true)

        `when`(adapterDataManager.findDatePosition(dateFrom))
            .thenReturn(-1)

        `when`(adapterDataManager.findDatePosition(dateTo))
            .thenReturn(-1)

        // When
        dateSelectionStrategy.onDateSelected(dateFrom)
        dateSelectionStrategy.onDateSelected(dateTo)

        // Then
        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(dateFrom, selectedDates.firstOrNull())
        assertEquals(dateTo, selectedDates.lastOrNull())

        selectedDates.forEach { dateInRange ->
            assertEquals(true, dateSelectionStrategy.isDateSelected(dateInRange))
        }
    }

    @Test
    fun `Select a range when dateTo is less than dateFrom and all dates in the range are selectable, as a result - all dates in the range are selected`() {
        // Given
        val dateFrom = TestData.date
        val dateTo = dateFrom.minusMonths(1)

        `when`(dateInfoProvider.isDateSelectable(anyNotNullObject()))
            .thenReturn(true)

        `when`(adapterDataManager.findDatePosition(dateFrom))
            .thenReturn(-1)

        `when`(adapterDataManager.findDatePosition(dateTo))
            .thenReturn(-1)

        // When
        dateSelectionStrategy.onDateSelected(dateFrom)
        dateSelectionStrategy.onDateSelected(dateTo)

        // Then
        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(dateTo, selectedDates.firstOrNull())
        assertEquals(dateFrom, selectedDates.lastOrNull())

        selectedDates.forEach { dateInRange ->
            assertEquals(true, dateSelectionStrategy.isDateSelected(dateInRange))
        }
    }

    @Test
    fun `Select a range when dateFrom is selectable and equals to dateTo, as a result - there is only one selected date`() {
        // Given
        val date = TestData.date

        `when`(dateInfoProvider.isDateSelectable(date))
            .thenReturn(true)

        `when`(adapterDataManager.findDatePosition(date))
            .thenReturn(-1)

        // When
        dateSelectionStrategy.onDateSelected(date)
        dateSelectionStrategy.onDateSelected(date)

        // Then
        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(1, selectedDates.size)
        assertEquals(date, selectedDates.firstOrNull())
    }

    @Test
    fun `Select a range when dateFrom is selectable and dateTo isn't selectable, as a result - there is only one selected dateFrom`() {
        // Given
        val dateFrom = TestData.date
        val dateTo = dateFrom.plusMonths(1)

        `when`(dateInfoProvider.isDateSelectable(dateFrom))
            .thenReturn(true)

        `when`(dateInfoProvider.isDateSelectable(dateTo))
            .thenReturn(false)

        `when`(adapterDataManager.findDatePosition(dateFrom))
            .thenReturn(-1)

        `when`(adapterDataManager.findDatePosition(dateTo))
            .thenReturn(-1)

        // When
        dateSelectionStrategy.onDateSelected(dateFrom)
        dateSelectionStrategy.onDateSelected(dateTo)

        // Then
        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(1, selectedDates.size)
        assertEquals(dateFrom, selectedDates.firstOrNull())
    }

    @Test
    fun `Select a range when dateFrom is less than dateTo and some dates in the range aren't selectable, as a result - there are unselected dates in the range`() {
        // Given
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.JANUARY, 1)
        val dateFrom = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JANUARY, 15)
        val dateTo = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JANUARY, 4)
        val unselectableDate1 = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JANUARY, 12)
        val unselectableDate2 = CalendarDate(calendar.time)

        val generatedTestDates = TestData.generateDatesBetween(dateFrom = dateFrom, dateTo = dateTo)
        generatedTestDates.forEach { date ->
            val selectable = date != unselectableDate1 && date != unselectableDate2

            `when`(dateInfoProvider.isDateSelectable(date))
                .thenReturn(selectable)
        }

        `when`(adapterDataManager.findDatePosition(dateFrom))
            .thenReturn(-1)

        `when`(adapterDataManager.findDatePosition(dateTo))
            .thenReturn(-1)

        // When
        dateSelectionStrategy.onDateSelected(dateFrom)
        dateSelectionStrategy.onDateSelected(dateTo)

        // Then
        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(13, selectedDates.size)
        assertEquals(dateFrom, selectedDates.firstOrNull())
        assertEquals(dateTo, selectedDates.lastOrNull())

        assertEquals(false, dateSelectionStrategy.isDateSelected(unselectableDate1))
        assertEquals(false, dateSelectionStrategy.isDateSelected(unselectableDate2))
    }
}