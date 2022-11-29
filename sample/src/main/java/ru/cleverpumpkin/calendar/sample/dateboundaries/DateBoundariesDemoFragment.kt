package ru.cleverpumpkin.calendar.sample.dateboundaries

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentCalendarBinding
import java.util.*

/**
 * This demo fragment demonstrate usage of the [CalendarView] with min-max date boundaries.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class DateBoundariesDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_calendar

    private val viewBinding: FragmentCalendarBinding by viewBinding(FragmentCalendarBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbarView) {
            val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
            setBackgroundColor(colorSurface2)
            setTitle(R.string.demo_date_boundaries)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }


        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
        if (savedInstanceState == null) {
            with(viewBinding.calendarView) {
                setDaysBarBackgroundColor(colorSurface2)
                setYearSelectionBarBackgroundColor(colorSurface2)
            }
            setupCalendar()
        }
    }

    private fun setupCalendar() {
        val calendar = Calendar.getInstance()

        calendar.set(2018, Calendar.JUNE, 1)
        val initialDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MAY, 28)
        val minDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JULY, 2)
        val maxDate = CalendarDate(calendar.time)

        viewBinding.calendarView.setupCalendar(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            selectionMode = CalendarView.SelectionMode.MULTIPLE
        )

    }

}