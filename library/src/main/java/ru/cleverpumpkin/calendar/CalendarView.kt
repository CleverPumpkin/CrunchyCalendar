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
import ru.cleverpumpkin.calendar.utils.getColorInt
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class allow displaying calendar grid, selecting dates
 * and handling date selection with custom action.
 *
 * Calendar must be initialize with a [setupCalendar] method where you can specify
 * parameters for calendar.
 *
 * This class overrides [onSaveInstanceState] and [onRestoreInstanceState], so it is able
 * to save and restore its state.
 */
class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Interface to be notified when a date is clicked.
     */
    interface OnDateClickListener {

        fun onDateClick(date: CalendarDate)
    }

    /**
     * Interface for internal needs that provide required information for specific calendar date.
     */
    interface DateInfoProvider {

        fun isToday(date: CalendarDate): Boolean

        fun isDateSelected(date: CalendarDate): Boolean

        fun isDateDisabled(date: CalendarDate): Boolean
    }

    companion object {
        private const val DAY_OF_WEEK_FORMAT = "EE"
        private const val DAYS_IN_WEEK = 7
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MAX_RECYCLED_EMPTY_VIEWS = 20
        private const val MONTHS_PER_PAGE = 6

        private const val BUNDLE_SUPER_STATE = "ru.cleverpumpkin.calendar.super_state"
        private const val BUNDLE_DISPLAY_DATE_RANGE = "ru.cleverpumpkin.calendar.display_date_range"
        private const val BUNDLE_LIMIT_DATE_RANGE = "ru.cleverpumpkin.calendar.limit_date_range"
        private const val BUNDLE_SELECTION_MODE = "ru.cleverpumpkin.calendar.selection_mode"
    }

    /**
     * This enum class represent available selection modes for dates selecting
     */
    enum class SelectionMode {
        /**
         * Selection is unavailable. No dates will be selectable.
         */
        NON,

        /**
         * Only one date will be selectable. If there is already a selected date and
         * you select a new one, the old date will be unselected.
         */
        SINGLE,

        /**
         * Multiple dates will be selectable. Selecting an already-selected date will un-select it.
         */
        MULTIPLE,

        /**
         * Allows you to select a date range. Previous selections are cleared when you either:
         * 1. Have a range selected and select another date (even if it's in the current range).
         * 2. Have one date selected and then select an earlier date.
         */
        RANGE
    }

    private var gridColor = context.getColorInt(R.color.calendar_grid_color)
    private var daysBarBackground = context.getColorInt(R.color.calendar_days_bar_background)
    private var daysBarTextColor = context.getColorInt(R.color.calendar_days_bar_text_color)
    private var monthTextColor = context.getColorInt(R.color.calendar_month_text_color)
    private var calendarDateBackgroundResId = R.drawable.calendar_date_bg_selector
    private var calendarDateTextColorResId = R.color.calendar_date_text_selector

    private val daysBarView: ViewGroup
    private val recyclerView: RecyclerView
    private val calendarAdapter: CalendarAdapter

    /**
     * Flag, that indicates whether the [setupCalendar] method was called or not
     */
    private var initializedWithSetup = false

    private var displayDatesRange = DatesRange.emptyRange()
    private var minMaxDatesRange = NullableDatesRange()

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

    /**
     * Listener that will be be notified when a date is clicked.
     */
    var onDateClickListener: OnDateClickListener? = null

    /**
     * Returns selected dates according to [selectionMode]. When selection mode is:
     * 1. [SelectionMode.NON] returns empty list
     * 2. [SelectionMode.SINGLE] returns list with a single selected date
     * 3. [SelectionMode.MULTIPLE] returns all selected dates in order they were added
     * 4. [SelectionMode.RANGE] returns all dates in selected range
     */
    val selectedDates: List<CalendarDate>
        get() = dateSelectionStrategy.getSelectedDates()

    /**
     * Returns selected date or null, when selection mode is [SelectionMode.SINGLE],
     * otherwise returns first date or null from the list of selected dates.
     */
    val selectedDate: CalendarDate?
        get() = dateSelectionStrategy.getSelectedDates()
            .firstOrNull()

    /**
     * Init block where we read custom attributes and setting up internal views
     */
    init {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)

            try {
                gridColor = typedArray.getColor(
                    R.styleable.CalendarView_cpcalendar_grid_color,
                    gridColor
                )

                daysBarBackground = typedArray.getColor(
                    R.styleable.CalendarView_cpcalendar_day_bar_background,
                    daysBarBackground
                )

                daysBarTextColor = typedArray.getColor(
                    R.styleable.CalendarView_cpcalendar_day_bar_text_color,
                    daysBarTextColor
                )

                monthTextColor = typedArray.getColor(
                    R.styleable.CalendarView_cpcalendar_month_text_color,
                    monthTextColor
                )

                calendarDateBackgroundResId = typedArray.getResourceId(
                    R.styleable.CalendarView_cpcalendar_date_background,
                    calendarDateBackgroundResId
                )

                calendarDateTextColorResId = typedArray.getResourceId(
                    R.styleable.CalendarView_cpcalendar_date_text_color,
                    calendarDateTextColorResId
                )
            } finally {
                typedArray.recycle()
            }
        }

        val itemsAttributes = CalendarAdapter.ItemsAttributes(
            monthTextColor = monthTextColor,
            calendarDateBackgroundResId = calendarDateBackgroundResId,
            calendarDateTextColorResId = calendarDateTextColorResId
        )

        calendarAdapter = CalendarAdapter(
            itemsAttributes = itemsAttributes,
            dateInfoProvider = DateInfoProviderImpl(),
            onDateClickHandler = { calendarDate ->
                dateSelectionStrategy.onDateSelected(calendarDate)
                onDateClickListener?.onDateClick(calendarDate)
            }
        )

        daysBarView = findViewById(R.id.days_container)
        recyclerView = findViewById(R.id.recycler_view)

        setupRecyclerView(recyclerView)
        setupDaysBar(daysBarView)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        val gridLayoutManager = object : GridLayoutManager(context, DAYS_IN_WEEK) {
            override fun onRestoreInstanceState(state: Parcelable?) {
                if (initializedWithSetup.not()) {
                    super.onRestoreInstanceState(state)
                }
            }
        }

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (recyclerView.adapter.getItemViewType(position)) {
                    CalendarAdapter.MONTH_VIEW_TYPE -> DAYS_IN_WEEK
                    else -> 1
                }
            }
        }

        recyclerView.run {
            adapter = calendarAdapter
            layoutManager = gridLayoutManager
            itemAnimator = null

            recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.DATE_VIEW_TYPE,
                MAX_RECYCLED_DAY_VIEWS
            )

            recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.EMPTY_VIEW_TYPE,
                MAX_RECYCLED_EMPTY_VIEWS
            )

            setHasFixedSize(true)
            addItemDecoration(GridDividerItemDecoration(context, gridColor))
            addOnScrollListener(CalendarScrollListener())
        }
    }

    private fun setupDaysBar(daysBarView: ViewGroup) {
        if (daysBarView.childCount != DAYS_IN_WEEK) {
            throw IllegalStateException("Days container has incorrect number of child views")
        }

        daysBarView.setBackgroundColor(daysBarBackground)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())

        for (dayPosition in 0 until DAYS_IN_WEEK) {
            val dayView = daysBarView.getChildAt(dayPosition) as TextView
            dayView.setTextColor(daysBarTextColor)
            dayView.text = dayOfWeekFormatter.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    /**
     * Add custom [RecyclerView.ItemDecoration] that will be used for calendar view decoration.
     */
    fun addCustomItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(itemDecoration)
    }

    /**
     * Remove specific [RecyclerView.ItemDecoration] that previously was added.
     */
    fun removeCustomItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration)
    }

    /**
     * Method for initial calendar set up. All parameters have default values.
     *
     * [initialDate] the date that will be displayed initially.
     * Default value - today date
     *
     * [minDate] minimum date for calendar grid, inclusive.
     * If null, calendar will display all available dates before [initialDate]
     * Default value - null
     *
     * [maxDate] maximum date for calendar grid, inclusive.
     * If null, calendar will display all available dates after [initialDate]
     * Default value - null
     *
     * [selectionMode] mode for dates selecting.
     * Default value - [SelectionMode.NON]
     */
    fun setupCalendar(
        initialDate: CalendarDate = CalendarDate(Date()),
        minDate: CalendarDate? = null,
        maxDate: CalendarDate? = null,
        selectionMode: SelectionMode = SelectionMode.NON
    ) {
        if (minDate != null && maxDate != null && minDate > maxDate) {
            throw IllegalStateException("minDate must be before maxDate: $minDate, maxDate: $maxDate")
        }

        val displayDatesFrom: CalendarDate
        val displayDatesTo: CalendarDate

        when {
            minDate == null && maxDate == null -> {
                displayDatesFrom = initialDate.minusMonths(MONTHS_PER_PAGE)
                displayDatesTo = initialDate.plusMonths(MONTHS_PER_PAGE)
            }

            minDate != null && maxDate == null -> {
                displayDatesFrom = minDate
                displayDatesTo = displayDatesFrom.plusMonths(MONTHS_PER_PAGE)
            }

            minDate == null && maxDate != null -> {
                displayDatesFrom = maxDate.minusMonths(MONTHS_PER_PAGE)
                displayDatesTo = maxDate
            }

            minDate != null && maxDate != null -> {
                if (initialDate.isBetween(minDate, maxDate)) {
                    var monthsBetween = minDate.monthsBetween(initialDate)
                    displayDatesFrom = if (monthsBetween > MONTHS_PER_PAGE) {
                        initialDate.minusMonths(MONTHS_PER_PAGE)
                    } else {
                        minDate
                    }

                    monthsBetween = initialDate.monthsBetween(maxDate)
                    displayDatesTo = if (monthsBetween > MONTHS_PER_PAGE) {
                        initialDate.plusMonths(MONTHS_PER_PAGE)
                    } else {
                        maxDate
                    }
                } else {
                    displayDatesFrom = minDate

                    val monthBetween = minDate.monthsBetween(maxDate)
                    displayDatesTo = if (monthBetween > MONTHS_PER_PAGE) {
                        minDate.plusMonths(MONTHS_PER_PAGE)
                    } else {
                        maxDate
                    }
                }
            }

            else -> throw IllegalStateException() // unreachable branch
        }

        this.selectionMode = selectionMode
        minMaxDatesRange = NullableDatesRange(dateFrom = minDate, dateTo = maxDate)
        displayDatesRange = DatesRange(dateFrom = displayDatesFrom, dateTo = displayDatesTo)

        generateCalendarItems(displayDatesRange)

        val initialMonthPosition = calendarAdapter.findMonthPosition(initialDate)
        if (initialMonthPosition != -1) {
            recyclerView.scrollToPosition(initialMonthPosition)
        }

        initializedWithSetup = true
    }

    private fun generateCalendarItems(datesRange: DatesRange) {
        if (datesRange.isEmptyRange()) {
            return
        }

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = datesRange.dateFrom,
            dateTo = datesRange.dateTo
        )

        calendarAdapter.setCalendarItems(calendarItems)
    }

    private fun generatePrevCalendarItems() {
        val minDate = minMaxDatesRange.dateFrom
        if (minDate != null && minDate.monthsBetween(displayDatesRange.dateFrom) == 0) {
            return
        }

        val generateDatesFrom: CalendarDate
        val generateDatesTo = displayDatesRange.dateFrom.minusMonths(1)

        generateDatesFrom = if (minDate != null) {
            val monthBetween = minDate.monthsBetween(generateDatesTo)

            if (monthBetween > MONTHS_PER_PAGE) {
                generateDatesTo.minusMonths(MONTHS_PER_PAGE)
            } else {
                generateDatesTo.minusMonths(monthBetween)
            }

        } else {
            generateDatesTo.minusMonths(MONTHS_PER_PAGE)
        }

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = generateDatesFrom,
            dateTo = generateDatesTo
        )

        calendarAdapter.addPrevCalendarItems(calendarItems)
        displayDatesRange = displayDatesRange.copy(dateFrom = generateDatesFrom)
    }

    private fun generateNextCalendarItems() {
        val maxDate = minMaxDatesRange.dateTo
        if (maxDate != null && displayDatesRange.dateTo.monthsBetween(maxDate) == 0) {
            return
        }

        val generateDatesFrom = displayDatesRange.dateTo.plusMonths(1)
        val generateDatesTo: CalendarDate

        generateDatesTo = if (maxDate != null) {
            val monthBetween = generateDatesFrom.monthsBetween(maxDate)

            if (monthBetween > MONTHS_PER_PAGE) {
                generateDatesFrom.plusMonths(MONTHS_PER_PAGE)
            } else {
                generateDatesFrom.plusMonths(monthBetween)
            }
        } else {
            generateDatesFrom.plusMonths(MONTHS_PER_PAGE)
        }

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = generateDatesFrom,
            dateTo = generateDatesTo
        )

        calendarAdapter.addNextCalendarItems(calendarItems)
        displayDatesRange = displayDatesRange.copy(dateTo = generateDatesTo)
    }

    /**
     * Save internal calendar state: displayed dates, min-max dates,
     * selection mode, selected dates and view super state.
     */
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

    /**
     * Restore internal calendar state.
     *
     * Note: If Calendar was initialized with [setupCalendar] method before [onRestoreInstanceState],
     * restoring of internal calendar state wont'n performed, because new state already set up.
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState: Parcelable? = state.getParcelable(BUNDLE_SUPER_STATE)
            super.onRestoreInstanceState(superState)

            if (initializedWithSetup.not()) {
                val modeName = state.getString(BUNDLE_SELECTION_MODE, SelectionMode.NON.name)
                selectionMode = SelectionMode.valueOf(modeName)
                displayDatesRange = state.getParcelable(BUNDLE_DISPLAY_DATE_RANGE)
                minMaxDatesRange = state.getParcelable(BUNDLE_LIMIT_DATE_RANGE)
                dateSelectionStrategy.restoreSelectedDates(state)

                generateCalendarItems(displayDatesRange)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Inner implementation of [DateInfoProvider] interface that provide required information
     * for specific calendar date.
     */
    private inner class DateInfoProviderImpl : DateInfoProvider {

        private val todayCalendarDate = CalendarDate(Date())

        override fun isToday(date: CalendarDate): Boolean {
            return date == todayCalendarDate
        }

        override fun isDateSelected(date: CalendarDate): Boolean {
            return dateSelectionStrategy.isDateSelected(date)
        }

        override fun isDateDisabled(date: CalendarDate): Boolean {
            val minDate = minMaxDatesRange.dateFrom
            val maxDate = minMaxDatesRange.dateTo

            return when {
                minDate == null && maxDate != null -> date > maxDate
                minDate != null && maxDate == null -> date < minDate
                minDate != null && maxDate != null -> date < minDate || date > maxDate
                minDate == null && maxDate == null -> false
                else -> false
            }
        }
    }

    /**
     * Inner class that monitor scroll events and perform generating next/previous
     * calendar items when needed.
     */
    private inner class CalendarScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val lastChildIndex = recyclerView.layoutManager.childCount
            val lastChild = recyclerView.layoutManager.getChildAt(lastChildIndex - 1)
            val lastChildAdapterPosition = recyclerView.getChildAdapterPosition(lastChild) + 1

            if (recyclerView.adapter.itemCount == lastChildAdapterPosition) {
                recyclerView.post { generateNextCalendarItems() }
            }

            val firstChild = recyclerView.layoutManager.getChildAt(0)
            val firstChildAdapterPosition = recyclerView.getChildAdapterPosition(firstChild)

            if (firstChildAdapterPosition == 0) {
                recyclerView.post { generatePrevCalendarItems() }
            }
        }
    }
}