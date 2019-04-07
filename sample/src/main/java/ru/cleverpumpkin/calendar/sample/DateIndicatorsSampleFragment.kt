package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.utils.getColorInt
import java.util.*

class DateIndicatorsSampleFragment : Fragment() {

    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendar_view)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.run {
            setTitle(R.string.date_indicators_sample)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }

            inflateMenu(R.menu.menu_today_action)
            setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
                calendarView.moveToDate(CalendarDate.today)
                return@OnMenuItemClickListener true
            })
        }

        calendarView.datesIndicators = generateCalendarDateIndicators()

        calendarView.onDateClickListener = { date ->
            val dateIndicators = calendarView.getDateIndicators(date)
                .filterIsInstance<CalendarDateIndicator>()
                .toTypedArray()

            if (dateIndicators.isNotEmpty()) {
                val builder = AlertDialog.Builder(context!!)
                    .setTitle("$date")
                    .setAdapter(DateIndicatorsDialogAdapter(context!!, dateIndicators), null)

                val dialog = builder.create()
                dialog.show()
            }
        }

        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.NONE)
        }
    }

    private fun generateCalendarDateIndicators(): List<CalendarView.DateIndicator> {
        val context = context!!
        val calendar = Calendar.getInstance()

        val indicators = mutableListOf<CalendarView.DateIndicator>()

        repeat(10) {
            indicators += CalendarDateIndicator(
                eventName = "Indicator #1",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_1_color)
            )

            indicators += CalendarDateIndicator(
                eventName = "Indicator #2",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_2_color)
            )

            indicators += CalendarDateIndicator(
                eventName = "Indicator #3",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_3_color)
            )

            indicators += CalendarDateIndicator(
                eventName = "Indicator #4",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_4_color)
            )

            indicators += CalendarDateIndicator(
                eventName = "Indicator #5",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_5_color)
            )

            calendar.add(Calendar.DAY_OF_MONTH, 5)
        }

        return indicators
    }

    class CalendarDateIndicator(
        override val date: CalendarDate,
        override val color: Int,
        val eventName: String

    ) : CalendarView.DateIndicator

    class DateIndicatorsDialogAdapter(
        context: Context,
        events: Array<CalendarDateIndicator>
    ) : ArrayAdapter<CalendarDateIndicator>(context, 0, events) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = if (convertView == null) {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_dialog_date_indicator, parent, false)
            } else {
                convertView
            }

            val event = getItem(position)

            if (event != null) {
                view.findViewById<View>(R.id.color_view).setBackgroundColor(event.color)
                view.findViewById<TextView>(R.id.event_name_view).text = event.eventName
            }

            return view
        }
    }
}