package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_demo_selection.*
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import java.util.*

/**
 * This demo fragment demonstrate usage of the [CalendarView] with the
 * [CalendarView.SelectionMode.RANGE] selection mode.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class RangeSelectionModeDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_selection

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            setupCalendar()
        }

        calendarView.onDateClickListener = { updateSelectedDatesView() }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateSelectedDatesView()
    }

    private fun setupCalendar() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.JUNE, 1)
        val initialDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 13)
        val preselectedRangeStart = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 18)
        val preselectedRangeEnd = CalendarDate(calendar.time)

        calendarView.setupCalendar(
            initialDate = initialDate,
            selectionMode = CalendarView.SelectionMode.RANGE,
            selectedDates = listOf(preselectedRangeStart, preselectedRangeEnd)
        )
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${calendarView.selectedDates}"
        selectedDatesView.text = selectedDates
    }

}