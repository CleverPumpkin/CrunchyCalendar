package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import java.util.*

class SelectionSampleFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectionModeGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selection_sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendar_view)
        selectionModeGroup = view.findViewById(R.id.selection_modes_group)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.run {
            setTitle(R.string.selection_sample)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }

            inflateMenu(R.menu.menu_today_action)
            setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
                calendarView.moveToDate(CalendarDate.today)
                return@OnMenuItemClickListener true
            })
        }

        calendarView.onDateLongClickListener = { date ->
            Toast.makeText(view.context, "Long click on date: $date", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        selectionModeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.single_mode_button -> setupCalendarWithSelectionMode(SelectionMode.SINGLE)
                R.id.multi_mode_button -> setupCalendarWithSelectionMode(SelectionMode.MULTIPLE)
                R.id.range_mode_button -> setupCalendarWithSelectionMode(SelectionMode.RANGE)
                R.id.boundaries_button -> setupCalendarWithBoundaries()
            }
        }

        if (savedInstanceState == null) {
            selectionModeGroup.check(R.id.single_mode_button)
        }
    }

    private fun setupCalendarWithSelectionMode(selectionMode: CalendarView.SelectionMode) {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 1)
        val initialDate = CalendarDate(calendar.time)

        val preselectedDates = when (selectionMode) {
            SelectionMode.NON -> emptyList()
            SelectionMode.SINGLE -> preselectedSingleDate()
            SelectionMode.MULTIPLE -> preselectedMultipleDates()
            SelectionMode.RANGE -> preselectedDatesRange()
        }

        calendarView.setupCalendar(
            initialDate = initialDate,
            selectionMode = selectionMode,
            selectedDates = preselectedDates
        )
    }

    private fun setupCalendarWithBoundaries() {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 1)
        val initialDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.MAY, 28)
        val minDate = CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JULY, 2)
        val maxDate = CalendarDate(calendar.time)

        val preselectedDates = preselectedMultipleDates()

        calendarView.setupCalendar(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            selectionMode = SelectionMode.MULTIPLE,
            selectedDates = preselectedDates
        )
    }

    private fun preselectedSingleDate(): List<CalendarDate> {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 18)
        return listOf(CalendarDate(calendar.time))
    }

    private fun preselectedMultipleDates(): List<CalendarDate> {
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

    private fun preselectedDatesRange(): List<CalendarDate> {
        val calendar = Calendar.getInstance()
        val selectedDates = mutableListOf<CalendarDate>()

        calendar.set(2018, Calendar.JUNE, 13)
        selectedDates += CalendarDate(calendar.time)

        calendar.set(2018, Calendar.JUNE, 18)
        selectedDates += CalendarDate(calendar.time)

        return selectedDates
    }
}