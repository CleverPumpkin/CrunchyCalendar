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
 * This demo fragment demonstrate usage of the [CalendarView] with a custom week selection
 * implementation.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class WeekSelectionModeDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_selection

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.MULTIPLE)
        }

        calendarView.onDateClickListener = { date ->
            val selectedDates = prepareWeekDates(date)
            calendarView.updateSelectedDates(selectedDates)
            updateSelectedDatesView()
        }
    }

    private fun prepareWeekDates(date: CalendarDate): List<CalendarDate> {
        val selectedDates = mutableListOf<CalendarDate>()
        val calendar = date.calendar

        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        repeat(7) {
            selectedDates += CalendarDate(calendar.timeInMillis)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        return selectedDates
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${calendarView.selectedDates}"
        selectedDatesView.text = selectedDates
    }

}