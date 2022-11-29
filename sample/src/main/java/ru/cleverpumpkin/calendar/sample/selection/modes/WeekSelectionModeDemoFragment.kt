package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.SelectionsBaseFragment
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoSelectionBinding
import java.util.*

/**
 * This demo fragment demonstrate usage of the [CalendarView] with a custom week selection
 * implementation.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class WeekSelectionModeDemoFragment : SelectionsBaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_selection

    private val viewBinding: FragmentDemoSelectionBinding by viewBinding(
        FragmentDemoSelectionBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
        viewBinding.horizontalView.setBackgroundColor(colorSurface2)

        if (savedInstanceState == null) {
            with(viewBinding.calendarView) {
                setDaysBarBackgroundColor(colorSurface2)
                setYearSelectionBarBackgroundColor(colorSurface2)
                setupCalendar(selectionMode = CalendarView.SelectionMode.RANGE)
            }
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

        selectedDates.add(CalendarDate(calendar.timeInMillis))
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        selectedDates.add(CalendarDate(calendar.timeInMillis))

        return selectedDates
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${viewBinding.calendarView.selectedDates}"
        viewBinding.selectedDatesView.text = selectedDates
    }

    override fun moveToToday() {
        viewBinding.calendarView.moveToDate(CalendarDate.today)
    }

}