package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoSelectionBinding

/**
 * This demo fragment demonstrate usage of the [CalendarView] with the
 * [CalendarView.SelectionMode.NONE] selection mode.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class NoneSelectionModeDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_selection

    private val viewBinding: FragmentDemoSelectionBinding by viewBinding(FragmentDemoSelectionBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewBinding.calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.NONE)
        }

        viewBinding.selectedDatesView.visibility = View.GONE
    }

}