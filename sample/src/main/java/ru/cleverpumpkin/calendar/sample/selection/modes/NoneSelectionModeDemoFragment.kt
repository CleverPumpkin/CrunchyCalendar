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

/**
 * This demo fragment demonstrate usage of the [CalendarView] with the
 * [CalendarView.SelectionMode.NONE] selection mode.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class NoneSelectionModeDemoFragment : BaseFragment(), SelectionFragmentsAction {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_selection

    private val viewBinding: FragmentDemoSelectionBinding by viewBinding(
        FragmentDemoSelectionBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())

        if (savedInstanceState == null) {
            with(viewBinding.calendarView){
                setDaysBarBackgroundColor(colorSurface2)
                setYearSelectionBarBackgroundColor(colorSurface2)
                setupCalendar()
            }
        }

        viewBinding.selectedDatesView.visibility = View.GONE
    }

    override fun moveToToday() {
        viewBinding.calendarView.moveToDate(CalendarDate.today)
    }

}