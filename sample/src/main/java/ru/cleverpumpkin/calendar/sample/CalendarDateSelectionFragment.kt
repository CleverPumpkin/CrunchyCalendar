package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.DemoModeListFragment.DemoMode
import java.lang.IllegalStateException

class CalendarDateSelectionFragment : Fragment() {

    companion object {
        private const val ARG_DEMO_MODE = "ru.cleverpumpkin.calendar.sample.demo_mode"

        fun newInstance(demoMode: DemoMode): Fragment {
            return CalendarDateSelectionFragment().apply {
                arguments = Bundle().apply { putString(ARG_DEMO_MODE, demoMode.name) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_demo_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoModeName = arguments?.getString(ARG_DEMO_MODE)
                ?: throw IllegalStateException()

        val demoMode = DemoMode.valueOf(demoModeName)

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