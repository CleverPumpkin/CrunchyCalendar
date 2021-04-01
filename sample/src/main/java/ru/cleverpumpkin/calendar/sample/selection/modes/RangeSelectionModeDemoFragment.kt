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
 * This demo fragment demonstrate usage of the [CalendarView] with the
 * [CalendarView.SelectionMode.RANGE] selection mode.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class RangeSelectionModeDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_selection

    private val viewBinding: FragmentDemoSelectionBinding by viewBinding(FragmentDemoSelectionBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            setupCalendar()
        }

        viewBinding.calendarView.onDateClickListener = { updateSelectedDatesView() }
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

        viewBinding.calendarView.setupCalendar(
            initialDate = initialDate,
            selectionMode = CalendarView.SelectionMode.RANGE,
            selectedDates = listOf(preselectedRangeStart, preselectedRangeEnd)
        )
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${viewBinding.calendarView.selectedDates}"
        viewBinding.selectedDatesView.text = selectedDates
    }

}