package ru.cleverpumpkin.calendar.sample.customstyle

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_calendar.*
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R

/**
 * This demo fragment demonstrate usage of the [CalendarView] with custom styles defined
 * programmatically.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class CodeStylingDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(toolbarView) {
            setTitle(R.string.demo_styling)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        with(calendarView) {
            setDrawGridOnSelectedDates(drawGrid = true)
            setGridColorRes(R.color.custom_calendar_grid_color)

            setYearSelectionBarBackgroundColorRes(R.color.custom_calendar_year_selection_background)
            setYearSelectionBarArrowsColorRes(R.color.custom_calendar_year_selection_arrows_color)
            setYearSelectionBarTextColorRes(R.color.custom_calendar_year_selection_text_color)

            setDaysBarBackgroundColorRes(R.color.custom_calendar_days_bar_background)
            setDaysBarTextColorRes(R.color.custom_calendar_days_bar_text_color)

            setMonthTextColorRes(R.color.custom_calendar_month_text_color)

            setDateCellBackgroundRes(R.drawable.custom_date_bg_selector)
            setDateCellTextColorRes(R.color.custom_date_text_selector)
        }

        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = SelectionMode.MULTIPLE)
        }
    }

}