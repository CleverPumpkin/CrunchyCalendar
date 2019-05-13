package ru.cleverpumpkin.calendar.selection

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import ru.cleverpumpkin.calendar.adapter.manager.AdapterDataManager
import ru.cleverpumpkin.calendar.anyNotNullObject
import ru.cleverpumpkin.calendar.utils.DateInfoProvider

/**
 * Set of unit tests for the [SingleDateSelectionStrategy] class.
 *
 * Each test contains:
 * 1. `Given` section, where we prepare test object and test data
 * 2. `When` section, where we run test method on the test object
 * 3. `Then` section, where we check results
 */
class SingleDateSelectionStrategyTest {

    @Mock private lateinit var adapterDataManager: AdapterDataManager
    @Mock private lateinit var dateInfoProvider: DateInfoProvider

    private lateinit var dateSelectionStrategy: DateSelectionStrategy

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dateSelectionStrategy = SingleDateSelectionStrategy(adapterDataManager, dateInfoProvider)
    }

    @Test
    fun `Select a date that is selectable, as a result - date is selected`() {
        // Given
        val date = TestData.date

        `when`(dateInfoProvider.isDateSelectable(date))
            .thenReturn(true)

        // When
        dateSelectionStrategy.onDateSelected(date)

        // Then
        assertEquals(true, dateSelectionStrategy.isDateSelected(date))
        assertEquals(1, dateSelectionStrategy.getSelectedDates().size)
        assertEquals(date, dateSelectionStrategy.getSelectedDates().firstOrNull())
    }

    @Test
    fun `Select a date that isn't selectable, as a result - date isn't selected`() {
        // Given
        val date = TestData.date

        `when`(dateInfoProvider.isDateSelectable(date))
            .thenReturn(false)

        // When
        dateSelectionStrategy.onDateSelected(date)

        // Then
        assertEquals(false, dateSelectionStrategy.isDateSelected(date))
        assertEquals(true, dateSelectionStrategy.getSelectedDates().isEmpty())
    }

    @Test
    fun `Select a new date when there is already selected the same date, as a result - a new date is not selected`() {
        // Given
        val date = TestData.date

        `when`(dateInfoProvider.isDateSelectable(date))
            .thenReturn(true)

        // When
        dateSelectionStrategy.onDateSelected(date)
        dateSelectionStrategy.onDateSelected(date)

        // Then
        assertEquals(false, dateSelectionStrategy.isDateSelected(date))
        assertEquals(true, dateSelectionStrategy.getSelectedDates().isEmpty())
    }

    @Test
    fun `Select a new date when there is already selected date, as a result - new date is selected and the old one - no`() {
        // Given
        val oldDate = TestData.date
        val newDate = oldDate.plusMonths(1)

        `when`(dateInfoProvider.isDateSelectable(anyNotNullObject()))
            .thenReturn(true)

        // When
        dateSelectionStrategy.onDateSelected(oldDate)
        dateSelectionStrategy.onDateSelected(newDate)

        // Then
        assertEquals(false, dateSelectionStrategy.isDateSelected(oldDate))
        assertEquals(true, dateSelectionStrategy.isDateSelected(newDate))
        assertEquals(1, dateSelectionStrategy.getSelectedDates().size)
        assertEquals(newDate, dateSelectionStrategy.getSelectedDates().firstOrNull())
    }
}