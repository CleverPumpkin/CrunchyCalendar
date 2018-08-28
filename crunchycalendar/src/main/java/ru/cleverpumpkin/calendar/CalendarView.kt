package ru.cleverpumpkin.calendar

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.AttrRes
import android.support.v4.util.ArrayMap
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.adapter.CalendarItemsGenerator
import ru.cleverpumpkin.calendar.adapter.item.DateItem
import ru.cleverpumpkin.calendar.adapter.item.MonthItem
import ru.cleverpumpkin.calendar.decorations.GridDividerItemDecoration
import ru.cleverpumpkin.calendar.selection.*
import ru.cleverpumpkin.calendar.utils.getColorInt
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

/**
 * This class represents a Calendar Widget that allow displaying calendar grid, selecting dates,
 * displaying color indicators for the specific dates and handling date selection with a custom action.
 *
 * The Calendar must be initialized with the [setupCalendar] method where you can specify
 * parameters for the calendar.
 *
 * The Calendar UI open for customization.
 * Using XML attributes you can define grid divider color, date cell selectors etc.
 * Using standard [RecyclerView.ItemDecoration] you can define special drawing for the calendar items.
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
     * This interface represents a colored indicator for the specific date that will be displayed
     * on the calendar.
     */
    interface DateIndicator {
        val date: CalendarDate
        val color: Int
    }

    /**
     * Interface for internal needs that provides required information for the specific calendar date.
     */
    interface DateInfoProvider {

        fun isToday(date: CalendarDate): Boolean

        fun isDateSelected(date: CalendarDate): Boolean

        fun isDateOutOfRange(date: CalendarDate): Boolean

        fun isDateSelectable(date: CalendarDate): Boolean

        fun isWeekend(date: CalendarDate): Boolean

        fun getDateIndicators(date: CalendarDate): List<DateIndicator>
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MAX_RECYCLED_EMPTY_VIEWS = 20
        private const val MONTHS_PER_PAGE = 6

        private const val BUNDLE_SUPER_STATE = "ru.cleverpumpkin.calendar.super_state"
        private const val BUNDLE_DISPLAYED_YEAR = "ru.cleverpumpkin.calendar.displayed_year"
        private const val BUNDLE_DISPLAY_DATE_RANGE = "ru.cleverpumpkin.calendar.display_date_range"
        private const val BUNDLE_LIMIT_DATE_RANGE = "ru.cleverpumpkin.calendar.limit_date_range"
        private const val BUNDLE_SELECTION_MODE = "ru.cleverpumpkin.calendar.selection_mode"
        private const val BUNDLE_FIRST_DAY_OF_WEEK = "ru.cleverpumpkin.calendar.first_day_of_week"
    }

    /**
     * This enum class represents available selection modes for dates selecting
     */
    enum class SelectionMode {
        /**
         * Selection is unavailable. No dates will be selectable.
         */
        NON,

        /**
         * Only one date can be selected at a time.
         */
        SINGLE,

        /**
         * A number of dates can be selected. Pressing an already selected date will unselect it.
         */
        MULTIPLE,

        /**
         * Allows you to select a date range. Previous selected range is cleared when you select another one.
         */
        RANGE
    }

    private var drawGridOnSelectedDates = true
    private var gridColor = getColorInt(R.color.calendar_grid_color)
    private var yearSelectionBackground = getColorInt(R.color.calendar_year_selection_background)
    private var yearSelectionArrowsColor = getColorInt(R.color.calendar_year_selection_arrows_color)
    private var yearSelectionTextColor = getColorInt(R.color.calendar_year_selection_text_color)
    private var daysBarBackground = getColorInt(R.color.calendar_days_bar_background)
    private var daysBarTextColor = getColorInt(R.color.calendar_days_bar_text_color)
    private var monthTextColor = getColorInt(R.color.calendar_month_text_color)
    private var calendarDateBackgroundResId = R.drawable.calendar_date_bg_selector
    private var calendarDateTextColorResId = R.color.calendar_date_text_selector

    private val yearSelectionView: YearSelectionView
    private val daysBarView: DaysBarView
    private val recyclerView: RecyclerView
    private val calendarAdapter: CalendarAdapter

    /**
     * Internal flag, that indicates whether the [setupCalendar] method was called or not
     */
    private var initializedWithSetup = false

    private var displayDatesRange = DatesRange.emptyRange()
    private var minMaxDatesRange = NullableDatesRange()

    private var dateSelectionStrategy: DateSelectionStrategy = NoDateSelectionStrategy()

    private lateinit var calendarItemsGenerator: CalendarItemsGenerator

    private var firstDayOfWeek: Int? = null
        set(value) {
            field = value

            val firstDayOfWeek = value ?: Calendar.getInstance().firstDayOfWeek
            daysBarView.setupDaysBarView(firstDayOfWeek)
            calendarItemsGenerator = CalendarItemsGenerator(firstDayOfWeek)
        }

    private val dateInfoProvider = object : DateInfoProvider {

        private val todayCalendarDate = CalendarDate.today

        override fun isToday(date: CalendarDate): Boolean {
            return date == todayCalendarDate
        }

        override fun isDateSelected(date: CalendarDate): Boolean {
            return dateSelectionStrategy.isDateSelected(date)
        }

        override fun isDateOutOfRange(date: CalendarDate): Boolean {
            val (minDate, maxDate) = minMaxDatesRange
            return (minDate != null && date < minDate) || (maxDate != null && date > maxDate)
        }

        override fun isDateSelectable(date: CalendarDate): Boolean {
            return dateSelectionFilter?.invoke(date) ?: true
        }

        override fun isWeekend(date: CalendarDate): Boolean {
            return date.dayOfWeek == Calendar.SUNDAY || date.dayOfWeek == Calendar.SATURDAY
        }

        override fun getDateIndicators(date: CalendarDate): List<DateIndicator> {
            return this@CalendarView.getDateIndicators(date)
        }
    }

    private var selectionMode: SelectionMode = SelectionMode.NON
        set(value) {
            field = value

            dateSelectionStrategy = when (value) {
                SelectionMode.NON -> NoDateSelectionStrategy()
                SelectionMode.SINGLE -> SingleDateSelectionStrategy(calendarAdapter, dateInfoProvider)
                SelectionMode.MULTIPLE -> MultipleDateSelectionStrategy(calendarAdapter, dateInfoProvider)
                SelectionMode.RANGE -> RangeDateSelectionStrategy(calendarAdapter, dateInfoProvider)
            }
        }

    /**
     * Grouped by date indicators that will be displayed on the calendar.
     */
    private val groupedDatesIndicators = ArrayMap<CalendarDate, MutableList<DateIndicator>>()

    /**
     * List of indicators that will be displayed on the calendar.
     */
    var datesIndicators: List<DateIndicator> = emptyList()
        set(value) {
            field = value
            groupedDatesIndicators.clear()
            value.groupByTo(groupedDatesIndicators) { it.date }
            recyclerView.adapter.notifyDataSetChanged()
        }

    /**
     * Listener that will be notified when a date is clicked.
     */
    var onDateClickListener: ((CalendarDate) -> Unit)? = null

    /**
     * Listener that will be notified when a date is long clicked.
     */
    var onDateLongClickListener: ((CalendarDate) -> Unit)? = null

    /**
     * Date selection filter that indicates whether a date available for selection or not.
     */
    var dateSelectionFilter: ((CalendarDate) -> Boolean)? = null

    /**
     * Returns selected dates according to [selectionMode]. When selection mode is:
     * [SelectionMode.NON] returns empty list
     * [SelectionMode.SINGLE] returns list with a single selected date
     * [SelectionMode.MULTIPLE] returns all selected dates in order they were added
     * [SelectionMode.RANGE] returns all dates in selected range
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

        yearSelectionView = findViewById(R.id.year_selection_view)
        daysBarView = findViewById(R.id.days_bar_view)
        recyclerView = findViewById(R.id.recycler_view)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)

            try {
                drawGridOnSelectedDates = typedArray.getBoolean(
                    R.styleable.CalendarView_calendar_grid_on_selected_dates,
                    drawGridOnSelectedDates
                )

                gridColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_grid_color,
                    gridColor
                )

                yearSelectionBackground = typedArray.getColor(
                    R.styleable.CalendarView_calendar_year_selection_background,
                    yearSelectionBackground
                )

                yearSelectionArrowsColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_year_selection_arrows_color,
                    yearSelectionArrowsColor
                )

                yearSelectionTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_year_selection_text_color,
                    yearSelectionTextColor
                )

                daysBarBackground = typedArray.getColor(
                    R.styleable.CalendarView_calendar_day_bar_background,
                    daysBarBackground
                )

                daysBarTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_day_bar_text_color,
                    daysBarTextColor
                )

                monthTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_month_text_color,
                    monthTextColor
                )

                calendarDateBackgroundResId = typedArray.getResourceId(
                    R.styleable.CalendarView_calendar_date_background,
                    calendarDateBackgroundResId
                )

                calendarDateTextColorResId = typedArray.getResourceId(
                    R.styleable.CalendarView_calendar_date_text_color,
                    calendarDateTextColorResId
                )
            } finally {
                typedArray.recycle()
            }
        }

        val adapterItemsStyle = CalendarAdapter.AdapterItemsStyle(
            monthTextColor = monthTextColor,
            dateBackgroundResId = calendarDateBackgroundResId,
            dateTextColorResId = calendarDateTextColorResId
        )

        calendarAdapter = CalendarAdapter(
            adapterItemsStyle = adapterItemsStyle,
            dateInfoProvider = dateInfoProvider,
            onDateClickListener = { date, longClick ->
                if (longClick) {
                    onDateLongClickListener?.invoke(date)
                } else {
                    dateSelectionStrategy.onDateSelected(date)
                    onDateClickListener?.invoke(date)
                }
            }
        )

        val daysBarStyle = DaysBarView.DaysBarStyle(
            background = daysBarBackground,
            textColor = daysBarTextColor
        )
        daysBarView.applyStyle(daysBarStyle)

        val yearSelectionStyle = YearSelectionView.YearSelectionStyle(
            background = yearSelectionBackground,
            arrowsColor = yearSelectionArrowsColor,
            yearTextColor = yearSelectionTextColor
        )
        yearSelectionView.applyStyle(yearSelectionStyle)

        setupRecyclerView(recyclerView)
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

            val divider = GridDividerItemDecoration(context, gridColor, drawGridOnSelectedDates)
            addItemDecoration(divider)

            addOnScrollListener(CalendarItemsGenerationListener())
            addOnScrollListener(YearTitleUpdateListener())
        }
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
     *
     * When selection mode is:
     * [SelectionMode.SINGLE], [selectedDates] should contains only one date.
     * [SelectionMode.MULTIPLE], [selectedDates] can contains multiple date.
     * [SelectionMode.RANGE], [selectedDates] should contains two dates that represent selected range.
     *
     * [selectedDates] list of initially selected dates.
     * Default value - empty list
     *
     * [firstDayOfWeek] the first day of week: [Calendar.SUNDAY], [Calendar.MONDAY], etc.
     * Default value - null, will be used the first day of week from default Locale
     */
    fun setupCalendar(
        initialDate: CalendarDate = CalendarDate.today,
        minDate: CalendarDate? = null,
        maxDate: CalendarDate? = null,
        selectionMode: SelectionMode = SelectionMode.NON,
        selectedDates: List<CalendarDate> = emptyList(),
        firstDayOfWeek: Int? = null
    ) {
        if (minDate != null && maxDate != null && minDate > maxDate) {
            throw IllegalArgumentException("minDate must be before maxDate: $minDate, maxDate: $maxDate")
        }

        if (firstDayOfWeek != null &&
            (firstDayOfWeek < Calendar.SUNDAY || firstDayOfWeek > Calendar.SATURDAY)) {
            throw IllegalArgumentException("Incorrect value of firstDayOfWeek: $firstDayOfWeek")
        }

        this.firstDayOfWeek = firstDayOfWeek
        this.selectionMode = selectionMode
        minMaxDatesRange = NullableDatesRange(dateFrom = minDate, dateTo = maxDate)
        yearSelectionView.displayedYear = initialDate.year

        if (selectedDates.isNotEmpty()) {
            when {
                selectionMode == SelectionMode.NON -> {
                    throw IllegalStateException("NON mode can't be used with selected dates")
                }

                selectionMode == SelectionMode.SINGLE && selectedDates.size > 1 -> {
                    throw IllegalStateException("SINGLE mode can't be used with multiple selected dates")
                }

                selectionMode == SelectionMode.RANGE && selectedDates.size > 2 -> {
                    throw IllegalStateException("RANGE mode only allows two selected dates")
                }

                else -> {
                    selectedDates.forEach { date ->
                        if (dateInfoProvider.isDateOutOfRange(date)) {
                            throw IllegalStateException(
                                "Selected date must be between minDate and maxDate. " +
                                        "Selected date: $date, minDate: $minDate, maxDate: $maxDate"
                            )
                        }
                        dateSelectionStrategy.onDateSelected(date)
                    }
                }
            }
        }

        displayDatesRange = prepareDisplayDatesRange(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate
        )

        generateCalendarItems(displayDatesRange)
        moveToDate(initialDate)

        initializedWithSetup = true
    }

    /**
     * Fast moving to the specific calendar date.
     * If [date] is out of min-max date boundaries, moving won't be performed.
     */
    fun moveToDate(date: CalendarDate) {
        val (minDate, maxDate) = minMaxDatesRange

        if ((minDate != null && date < minDate.monthBeginning()) ||
            (maxDate != null && date > maxDate.monthEnd())) {
            return
        }

        val (displayDatesFrom, displayDatesTo) = displayDatesRange

        if (date.isBetween(dateFrom = displayDatesFrom, dateTo = displayDatesTo).not()) {
            displayDatesRange = prepareDisplayDatesRange(
                initialDate = date,
                minDate = minDate,
                maxDate = maxDate
            )

            generateCalendarItems(displayDatesRange)
        }

        val dateMonthPosition = calendarAdapter.findMonthPosition(date)
        if (dateMonthPosition != -1) {
            val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
            gridLayoutManager.scrollToPositionWithOffset(dateMonthPosition, 0)
            recyclerView.stopScroll()
        }
    }

    /**
     * Add custom [RecyclerView.ItemDecoration] that will be used for the calendar view decoration.
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
     * Returns list of indicators for the specific date.
     */
    fun getDateIndicators(date: CalendarDate): List<DateIndicator> {
        return groupedDatesIndicators[date] ?: emptyList()
    }

    private fun prepareDisplayDatesRange(
        initialDate: CalendarDate,
        minDate: CalendarDate? = null,
        maxDate: CalendarDate? = null

    ): DatesRange {

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

        return DatesRange(dateFrom = displayDatesFrom, dateTo = displayDatesTo)
    }

    private fun generateCalendarItems(datesRange: DatesRange) {
        if (datesRange.isEmptyRange) {
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
     * selection mode, selected dates, first day of week and view super state.
     */
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return Bundle().apply {
            putString(BUNDLE_SELECTION_MODE, selectionMode.name)
            putInt(BUNDLE_DISPLAYED_YEAR, yearSelectionView.displayedYear)
            putParcelable(BUNDLE_DISPLAY_DATE_RANGE, displayDatesRange)
            putParcelable(BUNDLE_LIMIT_DATE_RANGE, minMaxDatesRange)
            putParcelable(BUNDLE_SUPER_STATE, superState)
            dateSelectionStrategy.saveSelectedDates(this)

            val firstDayOfWeek = firstDayOfWeek
            if (firstDayOfWeek != null) {
                putInt(BUNDLE_FIRST_DAY_OF_WEEK, firstDayOfWeek)
            }
        }
    }

    /**
     * Restore internal calendar state.
     *
     * Note: If Calendar was initialized with [setupCalendar] method before [onRestoreInstanceState],
     * restoring of internal calendar state won't be performed, because new state already set up.
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState: Parcelable? = state.getParcelable(BUNDLE_SUPER_STATE)
            super.onRestoreInstanceState(superState)

            if (initializedWithSetup.not()) {
                val modeName = state.getString(BUNDLE_SELECTION_MODE, SelectionMode.NON.name)
                selectionMode = SelectionMode.valueOf(modeName)
                yearSelectionView.displayedYear = state.getInt(BUNDLE_DISPLAYED_YEAR)
                displayDatesRange = state.getParcelable(BUNDLE_DISPLAY_DATE_RANGE)
                minMaxDatesRange = state.getParcelable(BUNDLE_LIMIT_DATE_RANGE)
                dateSelectionStrategy.restoreSelectedDates(state)

                firstDayOfWeek = if (state.containsKey(BUNDLE_FIRST_DAY_OF_WEEK)) {
                    state.getInt(BUNDLE_FIRST_DAY_OF_WEEK)
                } else {
                    null
                }

                generateCalendarItems(displayDatesRange)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private inner class CalendarItemsGenerationListener : RecyclerView.OnScrollListener() {

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

    private inner class YearTitleUpdateListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val firstChild = recyclerView.layoutManager.getChildAt(0)
            val firstChildAdapterPosition = recyclerView.getChildAdapterPosition(firstChild)

            val calendarItem = calendarAdapter.getCalendarItemAt(firstChildAdapterPosition)
            if (calendarItem is DateItem) {
                yearSelectionView.displayedYear = calendarItem.date.year
            } else if (calendarItem is MonthItem) {
                yearSelectionView.displayedYear = calendarItem.date.year
            }
        }
    }
}