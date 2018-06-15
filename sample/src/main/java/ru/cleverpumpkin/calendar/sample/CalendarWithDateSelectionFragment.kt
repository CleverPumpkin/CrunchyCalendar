package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.DemoListFragment.DemoMode
import java.lang.IllegalStateException
import java.util.*

class CalendarWithDateSelectionFragment : Fragment() {

    companion object {
        private const val ARG_DEMO_MODE = "ru.cleverpumpkin.calendar.sample.demo_mode"

        fun newInstance(demoMode: DemoMode): Fragment {
            return CalendarWithDateSelectionFragment().apply {
                arguments = Bundle().apply { putString(ARG_DEMO_MODE, demoMode.name) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_demo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoModeName = arguments?.getString(ARG_DEMO_MODE)
                ?: throw IllegalStateException()

        val demoMode = DemoListFragment.DemoMode.valueOf(demoModeName)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.run {
            setTitle(demoMode.descriptionRes)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)

        if (savedInstanceState == null) {
            val selectedDates = when (demoMode.selectionMode) {
                CalendarView.SelectionMode.NON -> emptyList()
                CalendarView.SelectionMode.SINGLE -> singleSelectedDate()
                CalendarView.SelectionMode.MULTIPLE -> multipleSelectedDate()
                CalendarView.SelectionMode.RANGE -> rangeSelectedDate()
            }

            calendarView.setupCalendar(
                selectionMode = demoMode.selectionMode,
                selectedDates = selectedDates
            )
        }
    }

    private fun singleSelectedDate(): List<CalendarDate> {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 18)
        return listOf(CalendarDate(calendar.time))
    }

    private fun multipleSelectedDate(): List<CalendarDate> {
        val calendar = Calendar.getInstance()
        val selectedDates = mutableListOf<CalendarDate>()

        calendar.set(2018, Calendar.JUNE, 13)
        selectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 16)
        selectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 19)
        selectedDates += CalendarDate(calendar.time)

        return selectedDates
    }

    private fun rangeSelectedDate(): List<CalendarDate> {
        val calendar = Calendar.getInstance()
        val selectedDates = mutableListOf<CalendarDate>()

        calendar.set(2018, Calendar.JUNE, 13)
        selectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 18)
        selectedDates += CalendarDate(calendar.time)

        return selectedDates
    }
}