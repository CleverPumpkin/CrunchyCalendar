package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import java.util.*

class CalendarMoveToDateFragment : Fragment() {

    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_demo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.run {
            inflateMenu(R.menu.menu_today_action)
            setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
                calendarView.moveToDate(CalendarDate.today)
                return@OnMenuItemClickListener true
            })

            setTitle(R.string.demo_mode_move_to_date)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        calendarView = view.findViewById(R.id.calendar_view)

        if (savedInstanceState == null) {
            val calendar = Calendar.getInstance()
            calendar.set(2015, Calendar.JUNE, 1)
            val initialDate = CalendarDate(calendar.time)

            calendar.set(2018, Calendar.APRIL, 28)
            val minDate = CalendarDate(calendar.time)

            calendar.set(2018, Calendar.MAY, 1)
            val maxDate = CalendarDate(calendar.time)

            calendarView.setupCalendar(
                initialDate = initialDate,
                selectionMode = SelectionMode.MULTIPLE
            )
        }
    }

}