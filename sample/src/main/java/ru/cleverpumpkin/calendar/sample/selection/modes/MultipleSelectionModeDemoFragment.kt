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
 * [CalendarView.SelectionMode.MULTIPLE] selection mode.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class MultipleSelectionModeDemoFragment : BaseFragment() {

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

        val preselectedDates = mutableListOf<CalendarDate>()

        calendar.set(2018, Calendar.JUNE, 13)
        preselectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 16)
        preselectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 19)
        preselectedDates += CalendarDate(calendar.time)

        calendarView.setupCalendar(
            initialDate = initialDate,
            selectionMode = CalendarView.SelectionMode.MULTIPLE,
            selectedDates = preselectedDates
        )
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${calendarView.selectedDates}"
        selectedDatesView.text = selectedDates
    }

}