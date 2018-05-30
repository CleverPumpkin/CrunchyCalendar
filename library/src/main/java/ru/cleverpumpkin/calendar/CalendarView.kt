package ru.cleverpumpkin.calendar

import android.content.Context
import android.support.annotation.AttrRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import ru.cleverpumpkin.calendar.decorations.GridDividerItemDecoration
import ru.cleverpumpkin.calendar.item.CalendarItem
import ru.cleverpumpkin.calendar.item.DayItem
import ru.cleverpumpkin.calendar.item.EmptyItem
import ru.cleverpumpkin.calendar.item.MonthItem
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DAY_OF_WEEK_FORMAT = "EE"

        private const val DAYS_IN_WEEK = 7
        private const val MONTHS_IN_YEAR = 12

        private const val MAX_SPAN_COUNT = 7
        private const val MAX_RECYCLED_DAY_VIEWS = 90
    }

    private val positionedDaysOfWeek: List<Int> = mutableListOf<Int>().apply {
        var currentDay = Calendar.getInstance().firstDayOfWeek

        (0 until DAYS_IN_WEEK).forEach {
            if (currentDay > DAYS_IN_WEEK) {
                currentDay = 1
            }

            this += currentDay
            currentDay++
        }
    }

    private val daysOfWeekContainer: ViewGroup
    private val recyclerView: RecyclerView

    private val calendarAdapter = CalendarAdapter()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)

        daysOfWeekContainer = findViewById(R.id.week_days_container)
        recyclerView = findViewById(R.id.recycler_view)

        setupRecyclerView(recyclerView)
        setupDaysOfWeekContainer(daysOfWeekContainer)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = GridLayoutManager(context, 7)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (recyclerView.adapter.getItemViewType(position)) {
                    CalendarAdapter.MONTH_VIEW_TYPE -> MAX_SPAN_COUNT
                    else -> 1
                }
            }
        }

        recyclerView.run {
            this.adapter = calendarAdapter
            this.layoutManager = layoutManager

            this.recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.DAY_VIEW_TYPE,
                MAX_RECYCLED_DAY_VIEWS
            )

            this.addItemDecoration(GridDividerItemDecoration())
        }
    }

    private fun setupDaysOfWeekContainer(weekDaysContainer: ViewGroup) {
        val daysCount = weekDaysContainer.childCount
        if (daysCount != DAYS_IN_WEEK) {
            throw IllegalStateException("Days container has incorrect number of child views")
        }

        val calendar = Calendar.getInstance()
        val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())

        (0 until DAYS_IN_WEEK).forEach { position ->
            val dayView = weekDaysContainer.getChildAt(position) as TextView
            val dayOfWeek = positionedDaysOfWeek[position]

            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            dayView.text = dayOfWeekFormatter.format(calendar.time)
        }
    }

    fun init() {
        val calendarItems = generateCalendarItemsForYear(year = 2018)
        calendarAdapter.setItems(calendarItems)
    }

    private fun generateCalendarItemsForYear(year: Int): List<CalendarItem> {
        val calendar = Calendar.getInstance()
        val calendarItems = mutableListOf<CalendarItem>()

        (0 until MONTHS_IN_YEAR).forEach { month ->
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            val monthItem = MonthItem(calendar.time)
            val itemsForMonth = generateCalendarItemsForMonth(year, month)

            calendarItems += monthItem
            calendarItems += itemsForMonth
        }

        return calendarItems
    }

    @Suppress("UnnecessaryVariable")
    private fun generateCalendarItemsForMonth(year: Int, month: Int): List<CalendarItem> {
        val calendar = Calendar.getInstance().apply {
            set(year, month, 1)
        }

        // First day of month - MONDAY, TUESDAY, e.t.c
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val positionOfFirstDayOfMonth = positionedDaysOfWeek.indexOf(firstDayOfMonth)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)

        // Last day of month - MONDAY, TUESDAY, e.t.c
        val lastDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val positionOfLastDayOfMonth = positionedDaysOfWeek.indexOf(lastDayOfMonth)

        val startOffset = positionOfFirstDayOfMonth
        val endOffset = (DAYS_IN_WEEK - positionOfLastDayOfMonth) - 1

        // Populate items for month
        val itemsForMonth = mutableListOf<CalendarItem>().apply {

            calendar.set(Calendar.DAY_OF_MONTH, 1)

            // Populate empty items for start offset
            (1..startOffset).forEach { this += EmptyItem }

            // Populate day items
            (1..daysInMonth).forEach {
                val date = calendar.time
                this += DayItem(date)
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            // Populate empty items for end offset
            (1..endOffset).forEach { this += EmptyItem }
        }

        return itemsForMonth
    }
}