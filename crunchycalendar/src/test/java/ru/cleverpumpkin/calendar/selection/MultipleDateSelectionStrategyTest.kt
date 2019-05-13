package ru.cleverpumpkin.calendar.selection

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import ru.cleverpumpkin.calendar.adapter.manager.AdapterDataManager
import ru.cleverpumpkin.calendar.anyNotNullObject
import ru.cleverpumpkin.calendar.utils.DateInfoProvider

/**
 * Set of unit tests for the [MultipleDateSelectionStrategy] class.
 *
 * Each test contains:
 * 1. `Given` section, where we prepare test object and test data
 * 2. `When` section, where we run test method on the test object
 * 3. `Then` section, where we check results
 */
class MultipleDateSelectionStrategyTest {

    @Mock private lateinit var adapterDataManager: AdapterDataManager
    @Mock private lateinit var dateInfoProvider: DateInfoProvider

    private lateinit var dateSelectionStrategy: DateSelectionStrategy

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dateSelectionStrategy = MultipleDateSelectionStrategy(adapterDataManager, dateInfoProvider)
    }

    @Test
    fun `Select three dates that all are selectable, as a result - all dates are selected in order they were added`() {
        // Given
        val date1 = TestData.date
        val date2 = date1.plusMonths(1)
        val date3 = date1.plusMonths(2)

        Mockito.`when`(dateInfoProvider.isDateSelectable(anyNotNullObject()))
            .thenReturn(true)

        // When
        dateSelectionStrategy.onDateSelected(date1)
        dateSelectionStrategy.onDateSelected(date2)
        dateSelectionStrategy.onDateSelected(date3)

        // Then
        assertEquals(true, dateSelectionStrategy.isDateSelected(date1))
        assertEquals(true, dateSelectionStrategy.isDateSelected(date2))
        assertEquals(true, dateSelectionStrategy.isDateSelected(date3))

        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(3, selectedDates.size)
        assertEquals(date1, selectedDates[0])
        assertEquals(date2, selectedDates[1])
        assertEquals(date3, selectedDates[2])
    }

    @Test
    fun `Select three dates when two are selectable and one - no, as a result - two dates are selected in order they were added`() {
        // Given
        val date1 = TestData.date
        val date2 = date1.plusMonths(1)
        val date3 = date1.plusMonths(2)

        `when`(dateInfoProvider.isDateSelectable(date1))
            .thenReturn(true)

        `when`(dateInfoProvider.isDateSelectable(date2))
            .thenReturn(true)

        `when`(dateInfoProvider.isDateSelectable(date3))
            .thenReturn(false)

        // When
        dateSelectionStrategy.onDateSelected(date1)
        dateSelectionStrategy.onDateSelected(date2)
        dateSelectionStrategy.onDateSelected(date3)

        // Then
        assertEquals(true, dateSelectionStrategy.isDateSelected(date1))
        assertEquals(true, dateSelectionStrategy.isDateSelected(date2))
        assertEquals(false, dateSelectionStrategy.isDateSelected(date3))

        val selectedDates = dateSelectionStrategy.getSelectedDates()
        assertEquals(2, selectedDates.size)
        assertEquals(date1, selectedDates[0])
        assertEquals(date2, selectedDates[1])
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
}