package ru.cleverpumpkin.calendar.sample.customstyle

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoStylingBinding

/**
 * This demo fragment demonstrate usage of the [CalendarView] with custom styles defined in XML.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class XmlStylingDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_demo_styling

    private val viewBinding: FragmentDemoStylingBinding by viewBinding(FragmentDemoStylingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbarView) {
            setTitle(R.string.demo_styling)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        if (savedInstanceState == null) {
            viewBinding.calendarView.setupCalendar(selectionMode = SelectionMode.MULTIPLE)
        }
    }

}