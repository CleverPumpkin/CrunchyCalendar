package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.SelectionFragmentsAction
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoSelectionBinding
import java.util.*

/**
 * This demo fragment demonstrate usage of the [CalendarView] with the
 * [CalendarView.SelectionMode.MULTIPLE] selection mode.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class MultipleSelectionModeDemoFragment : BaseFragment(), SelectionFragmentsAction {

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
            }
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

        val preselectedDates = mutableListOf<CalendarDate>()

        calendar.set(2018, Calendar.JUNE, 13)
        preselectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 16)
        preselectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 19)
        preselectedDates += CalendarDate(calendar.time)

        viewBinding.calendarView.setupCalendar(
            initialDate = initialDate,
            selectionMode = CalendarView.SelectionMode.MULTIPLE,
            selectedDates = preselectedDates
        )
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${viewBinding.calendarView.selectedDates}"
        viewBinding.selectedDatesView.text = selectedDates
    }

    override fun moveToToday() {
        viewBinding.calendarView.moveToDate(CalendarDate.today)
    }

}