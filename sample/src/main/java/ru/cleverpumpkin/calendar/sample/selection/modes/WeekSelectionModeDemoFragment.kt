package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoSelectionBinding
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

    private val viewBinding: FragmentDemoSelectionBinding by viewBinding(FragmentDemoSelectionBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewBinding.calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.MULTIPLE)
        }

        viewBinding.calendarView.onDateClickListener = { date ->
            val selectedDates = prepareWeekDates(date)
            viewBinding.calendarView.updateSelectedDates(selectedDates)
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
        val selectedDates = "Selected dates = ${viewBinding.calendarView.selectedDates}"
        viewBinding.selectedDatesView.text = selectedDates
    }

}