package ru.cleverpumpkin.calendar

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.joda.time.LocalDate
import ru.cleverpumpkin.calendar.item.CalendarItem
import ru.cleverpumpkin.calendar.item.DayItem
import ru.cleverpumpkin.calendar.item.EmptyItem
import ru.cleverpumpkin.calendar.item.MonthItem
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val onDateClick: (LocalDate, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            Log.d("View", "onCreateViewHolder")
            val dayView = CalendarDayView(parent.context)
            val dayItemViewHolder = DayItemViewHolder(dayView)

            dayItemViewHolder.dayView.setOnClickListener {
                val adapterPosition = dayItemViewHolder.adapterPosition
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                val dayItem = items[adapterPosition] as DayItem
                onDateClick.invoke(dayItem.localDate, adapterPosition)
            }

            dayItemViewHolder
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

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is DayItemViewHolder) {
            Log.d("View", "onViewRecycled")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DAY_VIEW_TYPE -> {
                val dayItem = items[position] as DayItem
                val dayItemViewHolder = holder as DayItemViewHolder

                dayItemViewHolder.dayView.text = dayFormatter.format(dayItem.localDate.toDate())
                dayItemViewHolder.localDate = dayItem.localDate
            }

            MONTH_VIEW_TYPE -> {
                val monthItem = items[position] as MonthItem
                val monthItemViewHolder = holder as MonthItemViewHolder

                monthItemViewHolder.textView.text =
                        monthFormatter.format(monthItem.localDate.toDate())
            }
        }
    }

    fun findMonthItemPosition(year: Int, month: Int): Int {
        return items.indexOfFirst { item ->
            if (item is MonthItem) {
                if (item.localDate.year == year && item.localDate.monthOfYear == month) {
                    return@indexOfFirst true
                }
            }

            return@indexOfFirst false
        }
    }

    fun setItems(calendarItems: List<CalendarItem>) {
        items.clear()
        items.addAll(calendarItems)
        notifyDataSetChanged()
    }

    fun addNextCalendarItems(nextCalendarItems: List<CalendarItem>) {
        items.addAll(nextCalendarItems)
        notifyItemRangeInserted(items.size - nextCalendarItems.size, nextCalendarItems.size)
    }

    fun addPrevCalendarItems(prevCalendarItems: List<CalendarItem>) {
        items.addAll(0, prevCalendarItems)
        notifyItemRangeInserted(0, prevCalendarItems.size)
    }


    class DayItemViewHolder(
        val dayView: CalendarDayView,
        var localDate: LocalDate? = null
    ) : RecyclerView.ViewHolder(dayView)

    class MonthItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    class EmptyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}