package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.cleverpumpkin.calendar.CalendarView
import java.lang.IllegalStateException

class CalendarCustomStyleFragment : Fragment() {

    companion object {
        private const val ARG_DEMO_MODE = "ru.cleverpumpkin.calendar.sample.demo_mode"

        fun newInstance(demoMode: DemoModeListFragment.DemoMode): Fragment {
            return CalendarCustomStyleFragment().apply {
                arguments = Bundle().apply { putString(ARG_DEMO_MODE, demoMode.name) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_style_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoModeName = arguments?.getString(ARG_DEMO_MODE)
                ?: throw IllegalStateException()

        val demoMode = DemoModeListFragment.DemoMode.valueOf(demoModeName)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.run {
            setTitle(demoMode.descriptionRes)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)
        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = demoMode.selectionMode)
        }
    }
}