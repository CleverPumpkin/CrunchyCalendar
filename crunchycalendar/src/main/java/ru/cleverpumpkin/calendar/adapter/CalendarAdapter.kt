package ru.cleverpumpkin.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarDateView
import ru.cleverpumpkin.calendar.R
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter.Companion.DATE_VIEW_TYPE
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter.Companion.EMPTY_VIEW_TYPE
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter.Companion.MONTH_VIEW_TYPE
import ru.cleverpumpkin.calendar.adapter.item.CalendarItem
import ru.cleverpumpkin.calendar.adapter.item.DateItem
import ru.cleverpumpkin.calendar.adapter.item.EmptyItem
import ru.cleverpumpkin.calendar.adapter.item.MonthItem
import ru.cleverpumpkin.calendar.style.CalendarStyleAttributes
import ru.cleverpumpkin.calendar.utils.DateInfoProvider
import java.text.SimpleDateFormat
import java.util.*

/**
 * This internal class provides view items for the Calendar.
 *
 * There are three types of items:
 *
 * [DATE_VIEW_TYPE] - single date cell.
 * [MONTH_VIEW_TYPE] - name of month.
 * [EMPTY_VIEW_TYPE] - empty view that represents start and end offset for each month
 */
internal class CalendarAdapter(
    private val styleAttributes: CalendarStyleAttributes,
    private val dateInfoProvider: DateInfoProvider,
    private val onDateClickListener: (CalendarDate, Boolean) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MONTH_FORMAT = "LLLL"
        const val YEAR_FORMAT = "yyyy"
        const val DAY_FORMAT = "d"
        const val DATE_VIEW_TYPE = 0
        const val MONTH_VIEW_TYPE = 1
        const val EMPTY_VIEW_TYPE = 2
    }

    private val calendarItems = mutableListOf<CalendarItem>()

    override fun getItemViewType(position: Int): Int {
        return when (calendarItems[position]) {
            is DateItem -> DATE_VIEW_TYPE
            is MonthItem -> MONTH_VIEW_TYPE
            is EmptyItem -> EMPTY_VIEW_TYPE
            else -> throw IllegalStateException("Unknown item at position $position")
        }
    }

    override fun getItemCount(): Int {
        return calendarItems.size
    }


    // region View Holders creation

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATE_VIEW_TYPE -> createDateItemViewHolder(parent.context)
            MONTH_VIEW_TYPE -> createMonthItemViewHolder(parent)
            EMPTY_VIEW_TYPE -> createEmptyItemViewHolder(parent.context)
            else -> throw IllegalStateException("Unknown view type: $viewType")
        }
    }

    private fun createDateItemViewHolder(context: Context): DateItemViewHolder {
        val dateView = CalendarDateView(context)
        val dayItemViewHolder = DateItemViewHolder(dateView)

        dateView.setOnClickListener {
            val adapterPosition = dayItemViewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val dateItem = calendarItems[adapterPosition] as DateItem
                onDateClickListener.invoke(dateItem.date, false)
            }
        }

        dateView.setOnLongClickListener {
            val adapterPosition = dayItemViewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val dateItem = calendarItems[adapterPosition] as DateItem
                onDateClickListener.invoke(dateItem.date, true)
            }

            return@setOnLongClickListener true
        }

        return dayItemViewHolder
    }

    private fun createMonthItemViewHolder(parent: ViewGroup): MonthItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.calendar_item_month, parent, false)
        return MonthItemViewHolder(view as TextView)
    }

    private fun createEmptyItemViewHolder(context: Context): EmptyItemViewHolder {
        val view = View(context)
        return EmptyItemViewHolder(view)
    }

    // endregion View Holders creation


    // region View Binding

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DATE_VIEW_TYPE -> {
                val dateItemViewHolder = holder as DateItemViewHolder
                val dateItem = calendarItems[position] as DateItem
                bindDateItemViewHolder(dateItemViewHolder, dateItem)
            }

            MONTH_VIEW_TYPE -> {
                val monthItemViewHolder = holder as MonthItemViewHolder
                val monthItem = calendarItems[position] as MonthItem
                bindMonthItemViewHolder(monthItemViewHolder, monthItem)
            }
        }
    }

    private fun bindDateItemViewHolder(holder: DateItemViewHolder, dateItem: DateItem) {
        val date = dateItem.date
        val dateView = holder.dateView

        dateView.isToday = dateInfoProvider.isToday(date)
        dateView.cellSelectionState = dateInfoProvider.getDateCellSelectedState(date)

        dateView.isDateDisabled =
            dateInfoProvider.isDateOutOfRange(date) || dateInfoProvider.isDateSelectable(date).not()

        dateView.isWeekend = dateInfoProvider.isWeekend(date)
        dateView.dateIndicators = dateInfoProvider.getDateIndicators(date)
        dateView.additionalTexts = dateInfoProvider.getDateAdditionalTexts(date)

        val digitsLocale = if (styleAttributes.useRootLocale) Locale.ROOT else Locale.getDefault()
        val dayFormatter = SimpleDateFormat(DAY_FORMAT, digitsLocale)
        dateView.dayNumber = dayFormatter.format(date.date)

        dateView.textColorStateList = styleAttributes.dateCellTextColorStateList
        dateView.indicatorAreaColor = styleAttributes.eventIndicatorsAreaColor
        dateView.setBackgroundResource(styleAttributes.dateCellBackgroundShapeForm)
        styleAttributes.dateCellBackgroundColorRes?.let {
            val bgColorStateList = ContextCompat.getColorStateList(dateView.context, it)
            ViewCompat.setBackgroundTintList(dateView, bgColorStateList)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindMonthItemViewHolder(holder: MonthItemViewHolder, monthItem: MonthItem) {
        val digitsLocale = if (styleAttributes.useRootLocale) Locale.ROOT else Locale.getDefault()
        val monthFormatter = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
        val yearFormatter = SimpleDateFormat(YEAR_FORMAT, digitsLocale)

        val month = monthFormatter.format(monthItem.date.date)
        val monthName = month.replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(digitsLocale)
            } else {
                it.toString()
            }
        }
        val yearName = yearFormatter.format(monthItem.date.date)

        holder.textView.text = "$monthName $yearName"
        holder.textView.setTextColor(styleAttributes.monthTextColor)
        holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, styleAttributes.monthTextSize)
        holder.textView.setTypeface(Typeface.DEFAULT, styleAttributes.monthTextStyle)
    }

    // endregion View Binding


    fun findMonthPosition(date: CalendarDate): Int {
        val year = date.year
        val month = date.month

        return calendarItems.indexOfFirst { item ->
            if (item is MonthItem) {
                if (item.date.year == year && item.date.month == month) {
                    return@indexOfFirst true
                }
            }

            return@indexOfFirst false
        }
    }

    fun findDatePosition(date: CalendarDate): Int {
        return calendarItems.indexOfFirst { item ->
            if (item is DateItem) {
                if (item.date == date) {
                    return@indexOfFirst true
                }
            }

            return@indexOfFirst false
        }
    }

    fun getCalendarItemAt(position: Int): CalendarItem {
        return calendarItems[position]
    }

    fun getDatesRange(dateFrom: CalendarDate, dateTo: CalendarDate): List<CalendarDate> {
        return calendarItems
            .mapNotNull { item ->
                if (item !is DateItem) {
                    return@mapNotNull null
                }
                if (item.date in dateFrom..dateTo) {
                    return@mapNotNull item.date
                } else {
                    null
                }
            }
    }

    fun setCalendarItems(calendarItems: List<CalendarItem>) {
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

}