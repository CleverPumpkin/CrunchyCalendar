package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.ColorInt
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
import ru.cleverpumpkin.calendar.decorations.AbsDateItemDecoration
import ru.cleverpumpkin.calendar.utils.dpToPix
import java.lang.IllegalStateException
import java.util.*

class CalendarWithEventsFragment : Fragment() {

    companion object {
        private const val ARG_DEMO_MODE = "ru.cleverpumpkin.calendar.sample.demo_mode"

        fun newInstance(demoMode: DemoModeListFragment.DemoMode): Fragment {
            return CalendarWithEventsFragment().apply {
                arguments = Bundle().apply { putString(ARG_DEMO_MODE, demoMode.name) }
            }
        }
    }

    private val groupedEvents = generateCalendarEvents()
        .groupBy { it.calendarDate }

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

        val demoMode = DemoModeListFragment.DemoMode.valueOf(demoModeName)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.run {
            setTitle(demoMode.descriptionRes)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)
        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.NON)
        }

        val eventsDateItemDecoration = EventsDateItemDecoration(
            context = context!!,
            groupedEvents = groupedEvents
        )

        calendarView.addCustomItemDecoration(eventsDateItemDecoration)

        calendarView.onDateClickListener = object : CalendarView.OnDateClickListener {
            override fun onDateClick(date: CalendarDate) {
                showEventsForDate(date)
            }
        }
    }

    private fun showEventsForDate(date: CalendarDate) {
        val eventsForDate = groupedEvents[date]?.toTypedArray() ?: return

        val builder = AlertDialog.Builder(context!!)
            .setTitle("Events")
            .setAdapter(EventsAdapter(context!!, eventsForDate), null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun generateCalendarEvents(): List<CalendarEvent> {
        val calendar = Calendar.getInstance()
        calendar.set(2018, Calendar.JUNE, 15)

        val event1 = CalendarEvent(
            eventName = "Event #1",
            calendarDate = CalendarDate(calendar.time),
            color = Color.RED
        )

        val event2 = CalendarEvent(
            eventName = "Event #2",
            calendarDate = CalendarDate(calendar.time),
            color = Color.GREEN
        )

        val event3 = CalendarEvent(
            eventName = "Event #3",
            calendarDate = CalendarDate(calendar.time),
            color = Color.CYAN
        )

        calendar.set(2018, Calendar.JUNE, 18)
        val event4 = CalendarEvent(
            eventName = "Event #4",
            calendarDate = CalendarDate(calendar.time),
            color = Color.RED
        )

        return listOf(event1, event2, event3, event4)
    }

    class EventsDateItemDecoration(
        context: Context,
        private val groupedEvents: Map<CalendarDate, List<CalendarEvent>>
    ) : AbsDateItemDecoration() {

        companion object {
            private const val INDICATOR_RADIUS = 4.0f
            private const val SPACE_BETWEEN_INDICATORS = 4.0f
        }

        private val radiusPx = context.dpToPix(INDICATOR_RADIUS)
        private val spacePx = context.dpToPix(SPACE_BETWEEN_INDICATORS)

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        override fun decorateDateView(canvas: Canvas, date: CalendarDate, dateViewRect: Rect) {
            val eventsForDate = groupedEvents[date]
            if (eventsForDate == null || eventsForDate.isEmpty()) {
                return
            }

            // Calculate initial positions
            val eventsCount = eventsForDate.size
            val drawableAreaWidth = (radiusPx * 2 * eventsCount) + (spacePx * (eventsCount - 1))

            val dateViewWidth = dateViewRect.width()
            var xPosition = dateViewRect.left + ((dateViewWidth - drawableAreaWidth) / 2) + radiusPx
            val yPosition = dateViewRect.bottom - radiusPx * 2

            // Draw indicators
            eventsForDate.forEach { event ->
                paint.color = event.color
                canvas.drawCircle(xPosition, yPosition, radiusPx, paint)

                xPosition += radiusPx * 2 + spacePx
            }
        }
    }

    class CalendarEvent(
        val eventName: String,
        val calendarDate: CalendarDate,
        @ColorInt val color: Int
    ) {
        override fun toString() = eventName
    }

    class EventsAdapter(
        context: Context,
        events: Array<CalendarEvent>
    ) : ArrayAdapter<CalendarEvent>(context, android.R.layout.simple_list_item_1, events) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val textView = super.getView(position, convertView, parent) as TextView
            val event = getItem(position)
            textView.setTextColor(event.color)
            return textView
        }
    }
}