package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import java.util.*

class SelectionSampleFragment : Fragment() {

    companion object {
        private const val BUNDLE_SELECTED_MODE = "selected_mode"
    }

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDatesView: TextView
    private lateinit var selectionModeGroupView: RadioGroup

    private var selectedMode = R.id.single_mode

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
        selectedDatesView = view.findViewById(R.id.selected_dates_view)
        selectionModeGroupView = view.findViewById(R.id.selection_modes_group)

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

        calendarView.onDateClickListener = {
            updateSelectedDatesView()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        selectionModeGroupView.setOnCheckedChangeListener { _, selectedMode ->
            this.selectedMode = selectedMode

            when (selectedMode) {
                R.id.single_mode -> setupCalendarWithSelectionMode(SelectionMode.SINGLE)
                R.id.multiple_mode -> setupCalendarWithSelectionMode(SelectionMode.MULTIPLE)
                R.id.range_mode -> setupCalendarWithSelectionMode(SelectionMode.RANGE)
                R.id.boundaries_mode -> setupCalendarWithBoundaries()
                R.id.selection_filter_mode -> setupCalendarWithDateSelectionFilter()
                R.id.week_mode -> setupCalendarWithSelectionMode(SelectionMode.WEEK)
            }

            updateSelectedDatesView()
        }

        if (savedInstanceState == null) {
            selectionModeGroupView.check(selectedMode)
        } else {
            selectedMode = savedInstanceState.getInt(BUNDLE_SELECTED_MODE)
            if (selectedMode == R.id.selection_filter_mode) {
                setDateSelectionFilter()
            }
        }

        updateSelectedDatesView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_SELECTED_MODE, selectedMode)
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
            SelectionMode.WEEK -> emptyList()
        }

        calendarView.dateSelectionFilter = null

        calendarView.setupCalendar(
            initialDate = initialDate,
            selectionMode = selectionMode,
            firstDayOfWeek = Calendar.WEDNESDAY,
            firstDayWeekSelectionMode = Calendar.WEDNESDAY,
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

        calendarView.dateSelectionFilter = null

        calendarView.setupCalendar(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            selectionMode = SelectionMode.MULTIPLE,
            selectedDates = preselectedDates
        )
    }

    private fun setupCalendarWithDateSelectionFilter() {
        calendarView.setupCalendar(selectionMode = SelectionMode.RANGE)
        setDateSelectionFilter()
    }

    private fun setDateSelectionFilter() {
        calendarView.dateSelectionFilter = { date ->
            date.dayOfWeek != Calendar.SATURDAY && date.dayOfWeek != Calendar.SUNDAY
        }
    }

    private fun updateSelectedDatesView() {
        val selectedDates = "Selected dates = ${calendarView.selectedDates}"
        selectedDatesView.text = selectedDates
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