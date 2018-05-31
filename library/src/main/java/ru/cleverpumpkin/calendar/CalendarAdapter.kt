package ru.cleverpumpkin.calendar

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.cleverpumpkin.calendar.item.CalendarItem
import ru.cleverpumpkin.calendar.item.DayItem
import ru.cleverpumpkin.calendar.item.EmptyItem
import ru.cleverpumpkin.calendar.item.MonthItem
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MONTH_FORMAT = "LLLL yyyy"
        const val DAY_FORMAT = "d"

        const val DAY_VIEW_TYPE = 0
        const val MONTH_VIEW_TYPE = 1
        const val EMPTY_VIEW_TYPE = 2
    }

    private val items = mutableListOf<CalendarItem>()

    private val monthFormatter = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
    private val dayFormatter = SimpleDateFormat(DAY_FORMAT, Locale.getDefault())

    override fun getItemViewType(position: Int) = when (items[position]) {
        is DayItem -> DAY_VIEW_TYPE
        is MonthItem -> MONTH_VIEW_TYPE
        is EmptyItem -> EMPTY_VIEW_TYPE
        else -> throw IllegalStateException("Unknown item at position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        DAY_VIEW_TYPE -> {
            val dayView = CalendarDayView(parent.context)
            DayItemViewHolder(dayView)
        }

        MONTH_VIEW_TYPE -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
            MonthItemViewHolder(v as TextView)
        }

        EMPTY_VIEW_TYPE -> {
            val v = View(parent.context)
            EmptyItemViewHolder(v)
        }

        else -> throw IllegalStateException("Unknown view type")
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DAY_VIEW_TYPE -> {
                val dayItem = items[position] as DayItem
                val dayItemViewHolder = holder as DayItemViewHolder

                dayItemViewHolder.dayView.text = dayFormatter.format(dayItem.date)
            }

            MONTH_VIEW_TYPE -> {
                val monthItem = items[position] as MonthItem
                val monthItemViewHolder = holder as MonthItemViewHolder

                monthItemViewHolder.textView.text = monthFormatter.format(monthItem.date)
            }
        }
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun setItems(calendarItems: List<CalendarItem>) {
        items.clear()
        items.addAll(calendarItems)
        notifyDataSetChanged()
    }

    fun getCalendarItemAt(position: Int) = items[position]

    fun addNextCalendarItems(nextCalendarItems: List<CalendarItem>) {
        items.addAll(nextCalendarItems)
        notifyItemRangeInserted(items.size - nextCalendarItems.size, nextCalendarItems.size)
    }

    fun addPrevCalendarItems(prevCalendarItems: List<CalendarItem>) {
        items.addAll(0, prevCalendarItems)
        notifyItemRangeInserted(0, prevCalendarItems.size)
    }


    class DayItemViewHolder(val dayView: CalendarDayView) : RecyclerView.ViewHolder(dayView)

    class MonthItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    class EmptyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}