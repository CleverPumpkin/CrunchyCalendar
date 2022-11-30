package ru.cleverpumpkin.calendar.sample.customstyle

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentCalendarBinding

/**
 * This demo fragment demonstrate usage of the [CalendarView] with custom styles defined
 * programmatically.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class CodeStylingDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_calendar

    private val viewBinding: FragmentCalendarBinding by viewBinding(FragmentCalendarBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbarView) {
            val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
            setBackgroundColor(colorSurface2)
            setTitle(R.string.demo_styling)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        with(viewBinding.calendarView) {
            setDrawGridOnSelectedDates(drawGrid = false)
            setGridColorRes(R.color.custom_calendar_grid_color)

            setYearSelectionBarBackgroundColorRes(R.color.custom_calendar_year_selection_background)
            setYearSelectionBarArrowsColorRes(R.color.custom_calendar_year_selection_arrows_color)
            setYearSelectionBarTextColorRes(R.color.custom_calendar_year_selection_text_color)

            setDaysBarBackgroundColorRes(R.color.custom_calendar_days_bar_background)
            setDaysBarTextColorRes(R.color.custom_calendar_days_bar_text_color)
            setDaysBarWeekendTextColorRes(R.color.custom_calendar_weekend_days_bar_text_color)

            setMonthTextColorRes(R.color.custom_calendar_month_text_color)

            setDateCellBackgroundRes(R.drawable.sample_custom_calendar_drawable)
            setDateCellBackgroundTintRes(R.color.custom_date_cell_background_color)
            setDateCellTextColorRes(R.color.custom_date_text_selector)
        }

        if (savedInstanceState == null) {
            viewBinding.calendarView.setupCalendar(selectionMode = SelectionMode.RANGE)
        }
    }

}