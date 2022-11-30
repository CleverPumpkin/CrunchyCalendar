package ru.cleverpumpkin.calendar.sample.events

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
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

    private val viewBinding: FragmentCalendarBinding by viewBinding(FragmentCalendarBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbarView) {
            val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
            setBackgroundColor(colorSurface2)
            setTitle(R.string.demo_events)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        viewBinding.calendarView.datesIndicators = generateEventItems()

        viewBinding.calendarView.onDateClickListener = { date ->
            showDialogWithEventsForSpecificDate(date)
        }

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
        if (savedInstanceState == null) {
            with(viewBinding.calendarView) {
                setDaysBarBackgroundColor(colorSurface2)
                setYearSelectionBarBackgroundColor(colorSurface2)
                setupCalendar(selectionMode = CalendarView.SelectionMode.NONE)
            }
        }
    }

    private fun showDialogWithEventsForSpecificDate(date: CalendarDate) = with(viewBinding) {
        val eventItems = calendarView.getDateIndicators(date)
            .filterIsInstance<EventItem>()

        if (eventItems.isNotEmpty()) {
            val format = "d MMMM yyyy"
            val dayFormatter = SimpleDateFormat(format, Locale.getDefault())

            bottomSheet.selectedDate.text = dayFormatter.format(date.date)
            val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
            bottomSheet.bottomSheet.backgroundTintList = ColorStateList.valueOf(colorSurface2)

            bottomSheet.list.layoutManager = LinearLayoutManager(requireContext())
            bottomSheet.list.adapter = EventItemsAdapter(eventItems)

            val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet.bottomSheet)
            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) dimBg.visibility = GONE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })

            viewBinding.dimBg.setOnClickListener {
                it.visibility = GONE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            dimBg.visibility = VISIBLE
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

    private class EventItemsAdapter(private val items: List<EventItem>) :
        RecyclerView.Adapter<EventItemsAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val colorView: CardView = itemView.findViewById(R.id.colorView)
            val eventNameView: TextView = itemView.findViewById(R.id.eventNameView)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog_event, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.colorView.setCardBackgroundColor(items[position].color)
            holder.eventNameView.text = items[position].eventName
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

}