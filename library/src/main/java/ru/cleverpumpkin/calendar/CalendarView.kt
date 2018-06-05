package ru.cleverpumpkin.calendar

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.AttrRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.adapter.CalendarItemsGenerator
import ru.cleverpumpkin.calendar.decorations.GridDividerItemDecoration
import ru.cleverpumpkin.calendar.selection.*
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : FrameLayout(context, attrs, defStyleAttr) {

    enum class SelectionMode {
        NON,
        SINGLE,
        MULTIPLE,
        RANGE
    }

    companion object {
        private const val DAY_OF_WEEK_FORMAT = "EE"
        private const val DAYS_IN_WEEK = 7
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MONTHS_PER_PAGE = 6

        private const val BUNDLE_SUPER_STATE = "ru.cleverpumpkin.calendar.super_state"
        private const val BUNDLE_DISPLAY_DATE_RANGE = "ru.cleverpumpkin.calendar.display_date_range"
        private const val BUNDLE_LIMIT_DATE_RANGE = "ru.cleverpumpkin.calendar.limit_date_range"
        private const val BUNDLE_SELECTION_MODE = "ru.cleverpumpkin.calendar.selection_mode"
    }

    private val daysContainer: ViewGroup
    private val recyclerView: RecyclerView

    private val calendarAdapter: CalendarAdapter
    private var calendarInitialized = false

    private lateinit var displayDatesRange: DatesRange
    private lateinit var minMaxDatesRange: NullableDatesRange

    private val calendarItemsGenerator = CalendarItemsGenerator()

    private var dateSelectionStrategy: DateSelectionStrategy = NoDateSelectionStrategy()

    private var selectionMode: SelectionMode = SelectionMode.NON
        set(value) {
            field = value

            dateSelectionStrategy = when (value) {
                SelectionMode.NON -> NoDateSelectionStrategy()
                SelectionMode.SINGLE -> SingleDateSelectionStrategy(calendarAdapter)
                SelectionMode.MULTIPLE -> MultipleDateSelectionStrategy(calendarAdapter)
                SelectionMode.RANGE -> RangeDateSelectionStrategy(calendarAdapter)
            }
        }

    val selectedDates: List<Date>
        get() = dateSelectionStrategy.getSelectedDates()
            .map { it.toDate() }

    val selectedDate: Date?
        get() = dateSelectionStrategy.getSelectedDates()
            .firstOrNull()
            ?.toDate()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)

        daysContainer = findViewById(R.id.days_container)
        recyclerView = findViewById(R.id.recycler_view)

        val dateInfoProvider = object : DateInfoProvider {
            val todayDate = SimpleLocalDate(Date())

            override fun isToday(date: SimpleLocalDate): Boolean {
                return date == todayDate
            }

            override fun isDateSelected(date: SimpleLocalDate): Boolean {
                return dateSelectionStrategy.isDateSelected(date)
            }

            override fun isDateEnabled(date: SimpleLocalDate): Boolean {
                val minDate = minMaxDatesRange.dateFrom
                val maxDate = minMaxDatesRange.dateTo

                return when {
                    minDate == null && maxDate != null -> date <= maxDate
                    minDate != null && maxDate == null -> date >= minDate
                    minDate != null && maxDate != null -> date >= minDate && date <= maxDate
                    minDate == null && maxDate == null -> true
                    else -> false
                }
            }
        }

        val onDateClickHandler: (SimpleLocalDate, Int) -> Unit = { date, position ->
            dateSelectionStrategy.onDateSelected(date, position)
        }

        calendarAdapter = CalendarAdapter(dateInfoProvider, onDateClickHandler)

        setupRecyclerView(recyclerView, calendarAdapter)
        setupDaysContainer(daysContainer)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: CalendarAdapter) {
        val layoutManager = GridLayoutManager(context, DAYS_IN_WEEK)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (recyclerView.adapter.getItemViewType(position)) {
                    CalendarAdapter.MONTH_VIEW_TYPE -> DAYS_IN_WEEK
                    else -> 1
                }
            }
        }

        recyclerView.run {
            this.adapter = adapter
            this.layoutManager = layoutManager

            this.recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.DAY_VIEW_TYPE,
                MAX_RECYCLED_DAY_VIEWS
            )

            this.addItemDecoration(GridDividerItemDecoration())

            val calendarScrollListener = CalendarScrollListener(
                generatePrevItems = Runnable { generatePrevCalendarItems() },
                generateNextItems = Runnable { generateNextCalendarItems() }
            )

            this.addOnScrollListener(calendarScrollListener)
        }
    }

    private fun setupDaysContainer(weekDaysContainer: ViewGroup) {
        if (weekDaysContainer.childCount != DAYS_IN_WEEK) {
            throw IllegalStateException("Days container has incorrect number of child views")
        }

        val calendar = Calendar.getInstance()
        val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())
        val positionedDaysOfWeek = calendarItemsGenerator.positionedDaysOfWeek

        for (position in 0 until DAYS_IN_WEEK) {
            val dayView = weekDaysContainer.getChildAt(position) as TextView
            val dayOfWeek = positionedDaysOfWeek[position]

            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            dayView.text = dayOfWeekFormatter.format(calendar.time)
        }
    }

    fun setup(
        initialDate: Date = Date(),
        minDate: Date? = null,
        maxDate: Date? = null,
        selectionMode: SelectionMode = SelectionMode.NON
    ) {
        when {
            minDate == null && maxDate == null -> {
                val initialLocalDate = SimpleLocalDate(initialDate)
                val dateFrom = initialLocalDate.minusMonths(MONTHS_PER_PAGE)
                val dateTo = initialLocalDate.plusMonths(MONTHS_PER_PAGE)

                generateInitialCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange()
            }

            minDate != null && maxDate != null -> {
                val dateFrom = SimpleLocalDate(minDate)
                val dateTo = SimpleLocalDate(maxDate)

                generateInitialCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange(dateFrom = dateFrom, dateTo = dateTo)
            }

            minDate != null && maxDate == null -> {
                val dateFrom = SimpleLocalDate(minDate)
                val dateTo = dateFrom.plusMonths(MONTHS_PER_PAGE)

                generateInitialCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange(dateFrom = dateFrom)
            }

            minDate == null && maxDate != null -> {
                val dateTo = SimpleLocalDate(maxDate)
                val dateFrom = dateTo.minusMonths(MONTHS_PER_PAGE)

                generateInitialCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange(dateTo = dateTo)
            }
        }

        val initialLocalDate = SimpleLocalDate(initialDate)
        val initialMonthPosition = calendarAdapter.findMonthItemPosition(initialLocalDate)

        if (initialMonthPosition != -1) {
            recyclerView.scrollToPosition(initialMonthPosition)
        }

        this.selectionMode = selectionMode

        calendarInitialized = true
    }

    private fun generateInitialCalendarItems(dateFrom: SimpleLocalDate, dateTo: SimpleLocalDate) {
        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = dateFrom.toDate(),
            dateTo = dateTo.toDate()
        )

        calendarAdapter.setItems(calendarItems)
    }

    private fun generatePrevCalendarItems() {
        if (this.minMaxDatesRange.dateFrom != null) {
            return
        }

        val dateTo = displayDatesRange.dateFrom.minusMonths(1)
        val dateFrom = dateTo.minusMonths(MONTHS_PER_PAGE)

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = dateFrom.toDate(),
            dateTo = dateTo.toDate()
        )

        calendarAdapter.addPrevCalendarItems(calendarItems)
        displayDatesRange = displayDatesRange.copy(dateFrom = dateFrom)
    }

    private fun generateNextCalendarItems() {
        if (this.minMaxDatesRange.dateTo != null) {
            return
        }

        val fromDate = displayDatesRange.dateTo.plusMonths(1)
        val toDate = fromDate.plusMonths(MONTHS_PER_PAGE)

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = fromDate.toDate(),
            dateTo = toDate.toDate()
        )

        calendarAdapter.addNextCalendarItems(calendarItems)
        displayDatesRange = displayDatesRange.copy(dateTo = toDate)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return Bundle().apply {
            putString(BUNDLE_SELECTION_MODE, selectionMode.name)
            putParcelable(BUNDLE_DISPLAY_DATE_RANGE, displayDatesRange)
            putParcelable(BUNDLE_LIMIT_DATE_RANGE, minMaxDatesRange)
            putParcelable(BUNDLE_SUPER_STATE, superState)
            dateSelectionStrategy.saveSelectedDates(this)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val name = state.getString(BUNDLE_SELECTION_MODE, SelectionMode.NON.name)
            selectionMode = SelectionMode.valueOf(name)
            displayDatesRange = state.getParcelable(BUNDLE_DISPLAY_DATE_RANGE)
            minMaxDatesRange = state.getParcelable(BUNDLE_LIMIT_DATE_RANGE)
            dateSelectionStrategy.restoreSelectedDates(state)

            val superState: Parcelable? = state.getParcelable(BUNDLE_SUPER_STATE)
            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(state)
        }

        generateInitialCalendarItems(
            dateFrom = displayDatesRange.dateFrom,
            dateTo = displayDatesRange.dateTo
        )
    }
}