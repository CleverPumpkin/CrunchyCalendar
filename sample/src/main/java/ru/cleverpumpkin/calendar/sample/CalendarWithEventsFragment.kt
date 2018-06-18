package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode
import ru.cleverpumpkin.calendar.utils.getColorInt
import java.util.*

class CalendarWithEventsFragment : Fragment() {

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
            setTitle(R.string.demo_mode_events)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)
        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = SelectionMode.NON)
        }

        val events = generateCalendarEvents()
        calendarView.datesIndicators = events

        calendarView.onDateClickListener = { date ->
            val eventsForDate = calendarView.getDateIndicators(date)
                .filterIsInstance<CalendarEvent>()
                .toTypedArray()

            showDialogWithEvents(eventsForDate)
        }
    }

    private fun showDialogWithEvents(eventsForDate: Array<CalendarEvent>) {
        val builder = AlertDialog.Builder(context!!)
            .setTitle("Events")
            .setAdapter(EventsDialogAdapter(context!!, eventsForDate), null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun generateCalendarEvents(): List<CalendarEvent> {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 15)

        val context = context!!

        val event1 = CalendarEvent(
            eventName = "Event #1",
            date = CalendarDate(calendar.time),
            color = context.getColorInt(R.color.event_1_color)
        )

        val event2 = CalendarEvent(
            eventName = "Event #2",
            date = CalendarDate(calendar.time),
            color = context.getColorInt(R.color.event_2_color)
        )

        val event3 = CalendarEvent(
            eventName = "Event #3",
            date = CalendarDate(calendar.time),
            color = context.getColorInt(R.color.event_3_color)
        )

        val event4 = CalendarEvent(
            eventName = "Event #4",
            date = CalendarDate(calendar.time),
            color = context.getColorInt(R.color.event_4_color)
        )

        val event5 = CalendarEvent(
            eventName = "Event #5",
            date = CalendarDate(calendar.time),
            color = context.getColorInt(R.color.event_5_color)
        )

        calendar.set(2018, Calendar.JUNE, 18)
        val event6 = CalendarEvent(
            eventName = "Event #6",
            date = CalendarDate(calendar.time),
            color = context.getColorInt(R.color.event_6_color)
        )

        return listOf(event1, event2, event3, event4, event5, event6)
    }

    class CalendarEvent(
        override val date: CalendarDate,
        override val color: Int,
        val eventName: String

    ) : CalendarView.DateIndicator

    class EventsDialogAdapter(
        context: Context,
        events: Array<CalendarEvent>
    ) : ArrayAdapter<CalendarEvent>(context, 0, events) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = if (convertView == null) {
                LayoutInflater.from(parent.context).inflate(R.layout.item_event, null)
            } else {
                convertView
            }

            val event = getItem(position)
            view.findViewById<View>(R.id.color_view).setBackgroundColor(event.color)
            view.findViewById<TextView>(R.id.event_name_view).text = event.eventName

            return view
        }
    }
}