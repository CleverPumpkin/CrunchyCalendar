package ru.cleverpumpkin.calendar.sample.customstyle

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_demo_styling.*
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R

/**
 * This demo fragment demonstrate usage of the [CalendarView] with custom styles defined in XML.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class XmlStylingDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_styling

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(toolbarView) {
            setTitle(R.string.demo_styling)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = SelectionMode.MULTIPLE)
        }
    }

}