package ru.cleverpumpkin.calendar

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.AttrRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
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
        private const val BUNDLE_SUPER_STATE = "ru.cleverpumpkin.calendar.super_state"
        private const val BUNDLE_INITIAL_DATE = "ru.cleverpumpkin.calendar.initial_date"

        private const val DAY_OF_WEEK_FORMAT = "EE"
        private const val DAYS_IN_WEEK = 7
        private const val MAX_SPAN_COUNT = 7
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MONTHS_PER_PAGE = 6
    }

    private var calendarInitialized = false

    private val daysOfWeekContainer: ViewGroup
    private val recyclerView: RecyclerView

    private val calendarAdapter = CalendarAdapter()

    private val startCalendarPointer = Calendar.getInstance()
    private val endCalendarPointer = Calendar.getInstance()

    /**
     * List of correctly positioned days of week.
     * For example:
     *
     * For US locale:
     * [Calendar.SUNDAY], [Calendar.MONDAY], [Calendar.WEDNESDAY],
     * [Calendar.THURSDAY], [Calendar.FRIDAY], [Calendar.SATURDAY]
     *
     * For RU locale:
     * [Calendar.MONDAY], [Calendar.WEDNESDAY], [Calendar.THURSDAY],
     * [Calendar.FRIDAY], [Calendar.SATURDAY], [Calendar.SUNDAY]
     */
    private val positionedDaysOfWeek = mutableListOf<Int>().apply {
        var dayValue = Calendar.getInstance().firstDayOfWeek

        (0 until DAYS_IN_WEEK).forEach {
            if (dayValue > DAYS_IN_WEEK) {
                dayValue = 1
            }

            this += dayValue
            dayValue++
        }
    }

    init {
        Log.d("CalendarView", "init")
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)

        daysOfWeekContainer = findViewById(R.id.week_days_container)
        recyclerView = findViewById(R.id.recycler_view)

        setupRecyclerView(recyclerView)
        setupDaysOfWeekContainer(daysOfWeekContainer)
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d("CalendarView", "onSaveInstanceState")
        val superState = super.onSaveInstanceState()

        val bundle = Bundle()
        bundle.putParcelable(BUNDLE_SUPER_STATE, superState)

        val layoutManager = recyclerView.layoutManager

        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i)
            val childAdapterPosition = recyclerView.getChildAdapterPosition(child)

            val holder = recyclerView.findViewHolderForAdapterPosition(childAdapterPosition)
            if (holder is CalendarAdapter.DayItemViewHolder) {
                val dayItem = calendarAdapter.getCalendarItemAt(childAdapterPosition) as DayItem
                bundle.putLong(BUNDLE_INITIAL_DATE, dayItem.date.time)
                break
            }
        }

        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d("CalendarView", "onRestoreInstanceState")
        if (state is Bundle) {
            if (calendarInitialized.not()) {
                val initialDate = state.getLong(BUNDLE_INITIAL_DATE, System.currentTimeMillis())

                startCalendarPointer.timeInMillis = initialDate
                endCalendarPointer.timeInMillis = initialDate

                calendarAdapter.clearItems()
                loadNextMonthsItems()
            }

            val superState: Parcelable? = state.getParcelable(BUNDLE_SUPER_STATE)
            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = GridLayoutManager(context, DAYS_IN_WEEK)

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

            val calendarScrollListener = CalendarScrollListener(
                prevMonthsLoadAction = Runnable { loadPrevMonthsItems() },
                nextMonthsLoadAction = Runnable { loadNextMonthsItems() }
            )

            this.addOnScrollListener(calendarScrollListener)
        }
    }

    private fun setupDaysOfWeekContainer(weekDaysContainer: ViewGroup) {
        val daysCount = weekDaysContainer.childCount
        if (daysCount != DAYS_IN_WEEK) {
            throw IllegalStateException("Days container has incorrect number of child views")
        }

        val calendar = Calendar.getInstance()
        val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())

        for (position in 0 until DAYS_IN_WEEK) {
            val dayView = weekDaysContainer.getChildAt(position) as TextView
            val dayOfWeek = positionedDaysOfWeek[position]

            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            dayView.text = dayOfWeekFormatter.format(calendar.time)
        }
    }

    fun init() {
        loadNextMonthsItems()
        calendarInitialized = true
    }

    private fun loadPrevMonthsItems() {
        startCalendarPointer.add(Calendar.MONTH, -MONTHS_PER_PAGE)

        val calendarItems = generateCalendarItemsForMonthsFromDate(
            fromDate = startCalendarPointer.time,
            monthCount = MONTHS_PER_PAGE
        )

        calendarAdapter.addPrevCalendarItems(calendarItems)
    }

    private fun loadNextMonthsItems() {
        val calendarItems = generateCalendarItemsForMonthsFromDate(
            fromDate = endCalendarPointer.time,
            monthCount = MONTHS_PER_PAGE
        )

        calendarAdapter.addNextCalendarItems(calendarItems)

        endCalendarPointer.add(Calendar.MONTH, MONTHS_PER_PAGE)
    }

    private fun generateCalendarItemsForMonthsFromDate(
        fromDate: Date,
        monthCount: Int
    ): List<CalendarItem> {

        val calendar = Calendar.getInstance()
        calendar.time = fromDate

        val calendarItems = mutableListOf<CalendarItem>()

        repeat(monthCount) {

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            val monthItem = MonthItem(calendar.time)
            val itemsForMonth = generateCalendarItemsForMonth(year, month)

            calendarItems += monthItem
            calendarItems += itemsForMonth

            calendar.add(Calendar.MONTH, 1)
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