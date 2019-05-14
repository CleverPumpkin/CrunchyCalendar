package ru.cleverpumpkin.calendar.sample.events

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_calendar.*
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import java.util.*

/**
 * This demo fragment demonstrate usage of the [CalendarView] to display custom events as
 * colored indicators.
 *
 * Created by Alexander Surinov on 2019-05-13.
 */
class EventListDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(toolbarView) {
            setTitle(R.string.demo_events)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        calendarView.datesIndicators = generateEventItems()

        calendarView.onDateClickListener = { date ->
            showDialogWithEventsForSpecificDate(date)
        }

        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.NONE)
        }
    }

    private fun showDialogWithEventsForSpecificDate(date: CalendarDate) {
        val eventItems = calendarView.getDateIndicators(date)
            .filterIsInstance<EventItem>()
            .toTypedArray()

        if (eventItems.isNotEmpty()) {
            val adapter = EventDialogAdapter(requireContext(), eventItems)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("$date")
                .setAdapter(adapter, null)

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun generateEventItems(): List<EventItem> {
        val context = requireContext()
        val calendar = Calendar.getInstance()

        val eventItems = mutableListOf<EventItem>()

        repeat(10) {
            eventItems += EventItem(
                eventName = "Event #1",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_1_color)
            )

            eventItems += EventItem(
                eventName = "Event #2",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_2_color)
            )

            eventItems += EventItem(
                eventName = "Event #3",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_3_color)
            )

            eventItems += EventItem(
                eventName = "Event #4",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_4_color)
            )

            eventItems += EventItem(
                eventName = "Event #5",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_5_color)
            )

            calendar.add(Calendar.DAY_OF_MONTH, 5)
        }

        return eventItems
    }

}