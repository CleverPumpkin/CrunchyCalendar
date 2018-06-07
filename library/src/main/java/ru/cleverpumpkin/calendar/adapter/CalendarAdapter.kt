package ru.cleverpumpkin.calendar.adapter

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarDateView
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.R
import ru.cleverpumpkin.calendar.adapter.item.CalendarItem
import ru.cleverpumpkin.calendar.adapter.item.DateItem
import ru.cleverpumpkin.calendar.adapter.item.EmptyItem
import ru.cleverpumpkin.calendar.adapter.item.MonthItem
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    var adapterViewAttributes: AdapterViewAttributes,
    private val dateInfoProvider: CalendarView.DateInfoProvider,
    private val onDateClickHandler: (CalendarDate) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MONTH_FORMAT = "LLLL yyyy"
        const val DAY_FORMAT = "d"

        const val DATE_VIEW_TYPE = 0
        const val MONTH_VIEW_TYPE = 1
        const val EMPTY_VIEW_TYPE = 2
    }

    private val calendarItems = mutableListOf<CalendarItem>()

    private val monthFormatter = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
    private val dayFormatter = SimpleDateFormat(DAY_FORMAT, Locale.getDefault())

    override fun getItemViewType(position: Int) = when (calendarItems[position]) {
        is DateItem -> DATE_VIEW_TYPE
        is MonthItem -> MONTH_VIEW_TYPE
        is EmptyItem -> EMPTY_VIEW_TYPE
        else -> throw IllegalStateException("Unknown item at position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATE_VIEW_TYPE -> {
                val dayView = CalendarDateView(parent.context)
                val dayItemViewHolder = DateItemViewHolder(dayView)

                dayView.setOnClickListener {
                    val adapterPosition = dayItemViewHolder.adapterPosition

                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val dateItem = calendarItems[adapterPosition] as DateItem
                        onDateClickHandler.invoke(dateItem.date)
                    }
                }

                dayItemViewHolder
            }

            MONTH_VIEW_TYPE -> {
                val v =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
                MonthItemViewHolder(v as TextView)
            }

            EMPTY_VIEW_TYPE -> {
                val v = View(parent.context)
                EmptyItemViewHolder(v)
            }

            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        when (viewType) {
            DATE_VIEW_TYPE -> {
                val calendarDate = (calendarItems[position] as DateItem).date
                val dayView = (holder as DateItemViewHolder).dateView

                dayView.text = dayFormatter.format(calendarDate.date)

                if (dateInfoProvider.isDateEnabled(calendarDate)) {
                    if (dateInfoProvider.isDateSelected(calendarDate)) {
                        dayView.setBackgroundColor(Color.BLUE)
                    } else {
                        dayView.setBackgroundColor(Color.WHITE)
                    }
                } else {
                    dayView.setBackgroundColor(Color.GRAY)
                }
            }

            MONTH_VIEW_TYPE -> {
                val monthItem = calendarItems[position] as MonthItem
                val monthItemViewHolder = holder as MonthItemViewHolder
                val monthName = monthFormatter.format(monthItem.calendarDate.date)

                monthItemViewHolder.textView.text = monthName.capitalize()
                monthItemViewHolder.textView.setTextColor(adapterViewAttributes.monthTextColor)
            }
        }
    }

    override fun getItemCount() = calendarItems.size

    fun findMonthItemPosition(calendarDate: CalendarDate): Int {
        val year = calendarDate.year
        val month = calendarDate.month

        return calendarItems.indexOfFirst { item ->
            if (item is MonthItem) {
                if (item.calendarDate.year == year && item.calendarDate.month == month) {
                    return@indexOfFirst true
                }
            }

            return@indexOfFirst false
        }
    }

    fun getCalendarItemAt(position: Int): CalendarItem {
        return calendarItems[position]
    }

    fun findDateItemPosition(calendarDate: CalendarDate): Int {
        return calendarItems.indexOfFirst { item ->
            if (item is DateItem) {
                if (item.date == calendarDate) {
                    return@indexOfFirst true
                }
            }

            return@indexOfFirst false
        }
    }

    fun getDateItemsRange(
        dateFrom: CalendarDate,
        dateTo: CalendarDate
    ): List<CalendarDate> {

        return calendarItems
            .filterIsInstance<DateItem>()
            .filter { dateItem ->
                if (dateItem.date in dateFrom..dateTo) {
                    return@filter true
                } else {
                    false
                }
            }
            .map { it.date }
    }

    fun setItems(calendarItems: List<CalendarItem>) {
        this.calendarItems.clear()
        this.calendarItems.addAll(calendarItems)
        notifyDataSetChanged()
    }

    fun addNextCalendarItems(nextCalendarItems: List<CalendarItem>) {
        calendarItems.addAll(nextCalendarItems)
        notifyItemRangeInserted(calendarItems.size - nextCalendarItems.size, nextCalendarItems.size)
    }

    fun addPrevCalendarItems(prevCalendarItems: List<CalendarItem>) {
        calendarItems.addAll(0, prevCalendarItems)
        notifyItemRangeInserted(0, prevCalendarItems.size)
    }


    class DateItemViewHolder(val dateView: CalendarDateView) : RecyclerView.ViewHolder(dateView)

    class MonthItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    class EmptyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    data class AdapterViewAttributes(
        @ColorInt val monthTextColor: Int
    )
}