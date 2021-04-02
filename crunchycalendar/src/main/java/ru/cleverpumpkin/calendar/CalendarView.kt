package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.adapter.CalendarItemsGenerator
import ru.cleverpumpkin.calendar.adapter.item.DateItem
import ru.cleverpumpkin.calendar.adapter.item.MonthItem
import ru.cleverpumpkin.calendar.adapter.manager.AdapterDataManager
import ru.cleverpumpkin.calendar.adapter.manager.CalendarAdapterDataManager
import ru.cleverpumpkin.calendar.decorations.GridDividerItemDecoration
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.selection.*
import ru.cleverpumpkin.calendar.style.CalendarStyleAttributes
import ru.cleverpumpkin.calendar.style.CalendarStyleAttributesReader
import ru.cleverpumpkin.calendar.utils.DateInfoProvider
import ru.cleverpumpkin.calendar.utils.DisplayedDatesRangeFactory
import java.util.*

/**
 * This class represents a calendar widget that allows to display vertical scrollable calendar grid,
 * selecting dates, displaying color indicators for the specific dates, handling date selection
 * and so on.
 *
 * The Calendar must be initialized with the [setupCalendar] method where you can specify
 * Calendar parameters.
 *
 * The Calendar UI open for customization: you can define grid divider color, date cell selectors etc.
 * Using standard [RecyclerView.ItemDecoration] you can define special drawing logic for the calendar
 * items.
 *
 * This class overrides [onSaveInstanceState] and [onRestoreInstanceState], so it is able
 * to save and restore its internal state.
 */
class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.calendarViewStyle

) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * This interface represents a color indicator for the specific date that will be displayed
     * on the Calendar.
     */
    interface DateIndicator {
        val date: CalendarDate
        val color: Int
    }

    companion object {
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MAX_RECYCLED_EMPTY_VIEWS = 20

        private const val BUNDLE_SUPER_STATE = "ru.cleverpumpkin.calendar.super_state"
        private const val BUNDLE_DISPLAYED_DATE = "ru.cleverpumpkin.calendar.displayed_date"
        private const val BUNDLE_DISPLAY_DATE_RANGE = "ru.cleverpumpkin.calendar.display_date_range"
        private const val BUNDLE_LIMIT_DATE_RANGE = "ru.cleverpumpkin.calendar.limit_date_range"
        private const val BUNDLE_SELECTION_MODE = "ru.cleverpumpkin.calendar.selection_mode"
        private const val BUNDLE_FIRST_DAY_OF_WEEK = "ru.cleverpumpkin.calendar.first_day_of_week"
        private const val BUNDLE_SHOW_YEAR_SELECTION_VIEW =
            "ru.cleverpumpkin.calendar.show_year_selection_view"
    }

    /**
     * This enum class represents available selection modes for dates selecting.
     */
    enum class SelectionMode {
        /**
         * Selection is unavailable. No dates will be selectable.
         */
        NONE,

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

    /**
     * Internal flag, that indicates whether the Calendar has been initialized
     * with [setupCalendar] method or not.
     */
    private var hasBeenInitializedWithSetup = false

    private val yearSelectionView: YearSelectionView
    private val daysBarView: DaysBarView
    private val recyclerView: RecyclerView
    private val calendarAdapter: CalendarAdapter

    private var displayedDatesRange = DatesRange.emptyRange()
    private var minMaxDatesRange = NullableDatesRange()
    private var dateSelectionStrategy: DateSelectionStrategy = NoDateSelectionStrategy()
    private val displayedYearUpdateListener = DisplayedYearUpdateListener()
    private val dateInfoProvider = DefaultDateInfoProvider()
    private val adapterDataManager: AdapterDataManager
    private lateinit var calendarItemsGenerator: CalendarItemsGenerator

    private val calendarStyleAttributes = CalendarStyleAttributes(context)

    private var selectionMode: SelectionMode = SelectionMode.NONE
        set(value) {
            field = value

            dateSelectionStrategy = when (value) {
                SelectionMode.NONE -> {
                    NoDateSelectionStrategy()
                }

                SelectionMode.SINGLE -> {
                    SingleDateSelectionStrategy(adapterDataManager, dateInfoProvider)
                }

                SelectionMode.MULTIPLE -> {
                    MultipleDateSelectionStrategy(adapterDataManager, dateInfoProvider)
                }

                SelectionMode.RANGE -> {
                    RangeDateSelectionStrategy(adapterDataManager, dateInfoProvider)
                }
            }
        }

    private var showYearSelectionView = true
        set(value) {
            field = value
            recyclerView.removeOnScrollListener(displayedYearUpdateListener)

            if (showYearSelectionView) {
                yearSelectionView.visibility = View.VISIBLE
                recyclerView.addOnScrollListener(displayedYearUpdateListener)
            } else {
                yearSelectionView.visibility = View.GONE
            }
        }

    private val defaultFirstDayOfWeek: Int
        get() = Calendar.getInstance().firstDayOfWeek

    /**
     * The first day of the week (e.g [Calendar.SUNDAY], [Calendar.MONDAY], etc.)
     * that has been set for the Calendar.
     *
     * If null, [defaultFirstDayOfWeek] will be used.
     */
    private var firstDayOfWeek: Int? = null
        set(value) {
            field = value
            daysBarView.setupDaysBarView(firstDayOfWeek ?: defaultFirstDayOfWeek)
            calendarItemsGenerator = CalendarItemsGenerator(firstDayOfWeek ?: defaultFirstDayOfWeek)
        }

    /**
     * Grouped by date color indicators that are displayed on the Calendar.
     */
    private val groupedDatesIndicators = ArrayMap<CalendarDate, MutableList<DateIndicator>>()

    /**
     * List of indicators that are displayed on the Calendar.
     */
    var datesIndicators: List<DateIndicator> = emptyList()
        set(value) {
            field = value
            groupedDatesIndicators.clear()
            value.groupByTo(groupedDatesIndicators) { it.date }
            recyclerView.adapter?.notifyDataSetChanged()
        }

    /**
     * Listener that will be notified when a date cell is clicked.
     */
    var onDateClickListener: ((CalendarDate) -> Unit)? = null

    /**
     * Listener that will be notified when a date cell is long clicked.
     */
    var onDateLongClickListener: ((CalendarDate) -> Unit)? = null

    /**
     * Listener that will be notified when a year view is clicked.
     */
    var onYearClickListener: ((Int) -> Unit)? = null
        set(value) {
            field = value
            yearSelectionView.onYearClickListener = value
        }

    /**
     * Filter that is used to determine whether a date available for selection or not.
     */
    var dateSelectionFilter: ((CalendarDate) -> Boolean)? = null

    /**
     * Filter that is used to determine whether a date is a weekend or not.
     */
    var weekendFilter: (CalendarDate) -> Boolean = { date ->
        date.dayOfWeek == Calendar.SUNDAY || date.dayOfWeek == Calendar.SATURDAY
    }

    /**
     * Returns selected dates according to the [selectionMode].
     *
     * When selection mode is:
     * [SelectionMode.NONE] returns empty list.
     * [SelectionMode.SINGLE] returns a list with a single selected date.
     * [SelectionMode.MULTIPLE] returns a list with all selected dates in order they were added.
     * [SelectionMode.RANGE] returns a list with all dates in the selected range.
     */
    val selectedDates: List<CalendarDate>
        get() = dateSelectionStrategy.getSelectedDates()

    /**
     * Returns selected date or null according to the [selectionMode].
     */
    val selectedDate: CalendarDate?
        get() = dateSelectionStrategy.getSelectedDates()
            .firstOrNull()

    /**
     * Init block where we read custom attributes and set up internal views.
     */
    init {
        LayoutInflater.from(context).inflate(R.layout.calendar_view, this, true)

        yearSelectionView = findViewById(R.id.calendar_year_selection_view)
        daysBarView = findViewById(R.id.calendar_days_bar_view)
        recyclerView = findViewById(R.id.calendar_recycler_view)

        if (attrs != null) {
            CalendarStyleAttributesReader.readStyleAttributes(
                context = context,
                attrs = attrs,
                defStyleAttr = defStyleAttr,
                styleAttributes = calendarStyleAttributes
            )
        }

        calendarAdapter = CalendarAdapter(
            styleAttributes = calendarStyleAttributes,
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

        adapterDataManager = CalendarAdapterDataManager(calendarAdapter)

        daysBarView.applyStyle(calendarStyleAttributes)
        yearSelectionView.applyStyle(calendarStyleAttributes)

        yearSelectionView.onYearChangeListener = { displayedDate ->
            moveToDate(displayedDate)
        }

        setupRecyclerView(recyclerView)
    }

    /**
     * Method for the initial Calendar set up. All parameters have default values.
     *
     * [initialDate] the date that will be displayed initially.
     * Default value - today date.
     *
     * [minDate] minimum date for the Calendar grid, inclusive.
     * If null, the Calendar will display all available dates before [initialDate]
     * Default value - null.
     *
     * [maxDate] maximum date for the Calendar grid, inclusive.
     * If null, the Calendar will display all available dates after [initialDate]
     * Default value - null.
     *
     * [selectionMode] mode for dates selecting.
     * Default value - [SelectionMode.NONE].
     *
     * [selectedDates] list of the initially selected dates.
     * Default value - empty list.
     *
     * When selection mode is:
     * [SelectionMode.SINGLE], [selectedDates] can contains only single date.
     * [SelectionMode.MULTIPLE], [selectedDates] can contains multiple dates.
     * [SelectionMode.RANGE], [selectedDates] can contains two dates that represent selected range.
     *
     * [firstDayOfWeek] the first day of the week: [Calendar.SUNDAY], [Calendar.MONDAY], etc.
     * If null, the Calendar will be initialized with the [defaultFirstDayOfWeek].
     * Default value - null.
     *
     * [showYearSelectionView] flag that indicates whether year selection view will be displayed or not.
     * Default value - true.
     */
    fun setupCalendar(
        initialDate: CalendarDate = CalendarDate.today,
        minDate: CalendarDate? = null,
        maxDate: CalendarDate? = null,
        selectionMode: SelectionMode = SelectionMode.NONE,
        selectedDates: List<CalendarDate> = emptyList(),
        firstDayOfWeek: Int? = null,
        showYearSelectionView: Boolean = true
    ) {
        if (minDate != null && maxDate != null && minDate > maxDate) {
            throw IllegalArgumentException("minDate must be before maxDate: $minDate, maxDate: $maxDate")
        }

        if (firstDayOfWeek != null) {
            if (firstDayOfWeek < Calendar.SUNDAY || firstDayOfWeek > Calendar.SATURDAY) {
                throw IllegalArgumentException("Incorrect value of firstDayOfWeek: $firstDayOfWeek")
            }
        }

        this.selectionMode = selectionMode
        this.firstDayOfWeek = firstDayOfWeek
        this.showYearSelectionView = showYearSelectionView
        minMaxDatesRange = NullableDatesRange(dateFrom = minDate, dateTo = maxDate)

        yearSelectionView.setupYearSelectionView(
            displayedDate = initialDate,
            minMaxDatesRange = minMaxDatesRange
        )

        updateSelectedDatesInternal(selectedDates)

        displayedDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate
        )

        generateCalendarItems(displayedDatesRange)
        moveToDate(initialDate)

        hasBeenInitializedWithSetup = true
    }

    /**
     * Move to the specific calendar date.
     */
    fun moveToDate(date: CalendarDate) {
        val (minDate, maxDate) = minMaxDatesRange

        if ((minDate != null && date < minDate.monthBeginning()) ||
            (maxDate != null && date > maxDate.monthEnd())) {
            return
        }

        val (displayDatesFrom, displayDatesTo) = displayedDatesRange

        if (date.isBetween(dateFrom = displayDatesFrom, dateTo = displayDatesTo).not()) {
            displayedDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
                initialDate = date,
                minDate = minDate,
                maxDate = maxDate
            )

            generateCalendarItems(displayedDatesRange)
        }

        val dateMonthPosition = calendarAdapter.findMonthPosition(date)
        if (dateMonthPosition != -1) {
            val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
            gridLayoutManager.scrollToPositionWithOffset(dateMonthPosition, 0)
            recyclerView.stopScroll()
        }
    }

    /**
     * Sets whether the calendar grid will be drawn over selected dates or not.
     */
    fun setDrawGridOnSelectedDates(drawGrid: Boolean) {
        calendarStyleAttributes.drawGridOnSelectedDates = drawGrid
        adapterDataManager.notifyDateItemsChanged()
    }

    /**
     * Sets the calendar grid color.
     */
    fun setGridColor(@ColorInt color: Int) {
        calendarStyleAttributes.gridColor = color
        adapterDataManager.notifyDateItemsChanged()
    }

    /**
     * Sets the calendar grid color resource.
     */
    fun setGridColorRes(@ColorRes colorRes: Int) {
        setGridColor(getColorInt(colorRes))
    }

    /**
     * Sets the year selection bar background color.
     */
    fun setYearSelectionBarBackgroundColor(@ColorInt color: Int) {
        calendarStyleAttributes.yearSelectionBackground = color
        yearSelectionView.applyStyle(calendarStyleAttributes)
    }

    /**
     * Sets the year selection bar background color resource.
     */
    fun setYearSelectionBarBackgroundColorRes(@ColorRes colorRes: Int) {
        setYearSelectionBarBackgroundColor(getColorInt(colorRes))
    }

    /**
     * Sets the year selection bar arrows color.
     */
    fun setYearSelectionBarArrowsColor(@ColorInt color: Int) {
        calendarStyleAttributes.yearSelectionArrowsColor = color
        yearSelectionView.applyStyle(calendarStyleAttributes)
    }

    /**
     * Sets the year selection bar arrows color resource.
     */
    fun setYearSelectionBarArrowsColorRes(@ColorRes colorRes: Int) {
        setYearSelectionBarArrowsColor(getColorInt(colorRes))
    }

    /**
     * Sets the year selection bar text color.
     */
    fun setYearSelectionBarTextColor(@ColorInt color: Int) {
        calendarStyleAttributes.yearSelectionTextColor = color
        yearSelectionView.applyStyle(calendarStyleAttributes)
    }

    /**
     * Sets the year selection bar text color resource.
     */
    fun setYearSelectionBarTextColorRes(@ColorRes colorRes: Int) {
        setYearSelectionBarTextColor(getColorInt(colorRes))
    }

    /**
     * Sets the days of week bar background color.
     */
    fun setDaysBarBackgroundColor(@ColorInt color: Int) {
        calendarStyleAttributes.daysBarBackground = color
        daysBarView.applyStyle(calendarStyleAttributes)
    }

    /**
     * Sets the days of week bar background color resource.
     */
    fun setDaysBarBackgroundColorRes(@ColorRes colorRes: Int) {
        setDaysBarBackgroundColor(getColorInt(colorRes))
    }

    /**
     * Set text color of days on week bar.
     */
    fun setDaysBarTextColor(@ColorInt color: Int) {
        calendarStyleAttributes.daysBarTextColor = color
        daysBarView.applyStyle(calendarStyleAttributes)
    }

    /**
     * Set text color of the weekend days on week bar.
     */
    fun setDaysBarWeekendTextColor(@ColorInt color: Int) {
        calendarStyleAttributes.daysBarWeekendTextColor = color
        daysBarView.applyStyle(calendarStyleAttributes)
    }

    /**
     * Set text color of days on week bar.
     */
    fun setDaysBarTextColorRes(@ColorRes colorRes: Int) {
        setDaysBarTextColor(getColorInt(colorRes))
    }

    /**
     * Set text color of the weekend days on week bar.
     */
    fun setDaysBarWeekendTextColorRes(@ColorRes colorRes: Int) {
        setDaysBarWeekendTextColor(getColorInt(colorRes))
    }

    /**
     * Sets the month text color.
     */
    fun setMonthTextColor(@ColorInt color: Int) {
        calendarStyleAttributes.monthTextColor = color
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * Sets the month text color resource.
     */
    fun setMonthTextColorRes(@ColorRes colorRes: Int) {
        setMonthTextColor(getColorInt(colorRes))
    }

    /**
     * Sets the month text size in SP.
     */
    fun setMonthTextSize(size: Int) {
        calendarStyleAttributes.monthTextSize = resources.getDimension(size)
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * Sets the month text style typeface.
     */
    fun setMonthTextStyle(style: Int) {
        calendarStyleAttributes.monthTextStyle = style
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * Sets a date cell background resource.
     */
    fun setDateCellBackgroundRes(@DrawableRes drawableRes: Int) {
        calendarStyleAttributes.dateCellBackgroundShapeForm = drawableRes
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * Sets a date cell background tint.
     */
    fun setDateCellBackgroundTintRes(@ColorRes colorRes: Int) {
        calendarStyleAttributes.dateCellBackgroundColorRes = colorRes
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * Sets a date cell text color.
     */
    fun setDateCellTextColor(colorStateList: ColorStateList) {
        calendarStyleAttributes.dateCellTextColorStateList = colorStateList
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * Sets a date cell text color.
     */
    fun setDateCellTextColor(@ColorInt color: Int) {
        val colorStateList = ColorStateList.valueOf(color)
        setDateCellTextColor(colorStateList)
    }

    /**
     * Sets a date cell text color resource.
     */
    fun setDateCellTextColorRes(@ColorRes colorRes: Int) {
        val colorStateList = requireNotNull(ContextCompat.getColorStateList(context, colorRes))
        setDateCellTextColor(colorStateList)
    }

    /**
     * Add custom [RecyclerView.ItemDecoration] that will be used for the Calendar view decoration.
     */
    fun addCustomItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(itemDecoration)
    }

    /**
     * Remove [RecyclerView.ItemDecoration] that has been added.
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

    /**
     * Update currently selected dates.
     */
    fun updateSelectedDates(selectedDates: List<CalendarDate>) {
        dateSelectionStrategy.clear()
        updateSelectedDatesInternal(selectedDates)
    }

    private fun updateSelectedDatesInternal(selectedDates: List<CalendarDate>) {
        if (selectedDates.isEmpty()) {
            return
        }

        when {
            selectionMode == SelectionMode.NONE -> {
                throw IllegalArgumentException(
                    "You cannot define selected dates when the SelectionMode is NONE"
                )
            }

            selectionMode == SelectionMode.SINGLE && selectedDates.size > 1 -> {
                throw IllegalArgumentException(
                    "You cannot define more than one selected dates when the SelectionMode is SINGLE"
                )
            }

            selectionMode == SelectionMode.RANGE && selectedDates.size != 2 -> {
                throw IllegalArgumentException(
                    "You must define two selected dates (start and end) when the SelectionMode is RANGE"
                )
            }
        }

        selectedDates.forEach { date ->
            if (dateInfoProvider.isDateOutOfRange(date).not() &&
                dateInfoProvider.isDateSelectable(date)
            ) {
                dateSelectionStrategy.onDateSelected(date)
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val gridLayoutManager = object : GridLayoutManager(context, CalendarConst.DAYS_IN_WEEK) {
            override fun onRestoreInstanceState(state: Parcelable?) {
                if (hasBeenInitializedWithSetup.not()) {
                    super.onRestoreInstanceState(state)
                }
            }
        }

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (recyclerView.adapter?.getItemViewType(position)) {
                    CalendarAdapter.MONTH_VIEW_TYPE -> CalendarConst.DAYS_IN_WEEK
                    else -> 1
                }
            }
        }

        with(recyclerView) {
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

            addItemDecoration(GridDividerItemDecoration(context, calendarStyleAttributes))
            addOnScrollListener(CalendarItemsGenerationListener())
        }
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
        if (minDate != null && minDate.monthsBetween(displayedDatesRange.dateFrom) == 0) {
            return
        }

        val generateDatesFrom: CalendarDate
        val generateDatesTo = displayedDatesRange.dateFrom.minusMonths(1)

        generateDatesFrom = if (minDate != null) {
            val monthBetween = minDate.monthsBetween(generateDatesTo)

            if (monthBetween > CalendarConst.MONTHS_PER_PAGE) {
                generateDatesTo.minusMonths(CalendarConst.MONTHS_PER_PAGE)
            } else {
                generateDatesTo.minusMonths(monthBetween)
            }

        } else {
            generateDatesTo.minusMonths(CalendarConst.MONTHS_PER_PAGE)
        }

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = generateDatesFrom,
            dateTo = generateDatesTo
        )

        calendarAdapter.addPrevCalendarItems(calendarItems)
        displayedDatesRange = displayedDatesRange.copy(dateFrom = generateDatesFrom)
    }

    private fun generateNextCalendarItems() {
        val maxDate = minMaxDatesRange.dateTo
        if (maxDate != null && displayedDatesRange.dateTo.monthsBetween(maxDate) == 0) {
            return
        }

        val generateDatesFrom = displayedDatesRange.dateTo.plusMonths(1)
        val generateDatesTo: CalendarDate

        generateDatesTo = if (maxDate != null) {
            val monthBetween = generateDatesFrom.monthsBetween(maxDate)

            if (monthBetween > CalendarConst.MONTHS_PER_PAGE) {
                generateDatesFrom.plusMonths(CalendarConst.MONTHS_PER_PAGE)
            } else {
                generateDatesFrom.plusMonths(monthBetween)
            }
        } else {
            generateDatesFrom.plusMonths(CalendarConst.MONTHS_PER_PAGE)
        }

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = generateDatesFrom,
            dateTo = generateDatesTo
        )

        calendarAdapter.addNextCalendarItems(calendarItems)
        displayedDatesRange = displayedDatesRange.copy(dateTo = generateDatesTo)
    }

    /**
     * Save internal Calendar state.
     */
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return Bundle().apply {
            putSerializable(BUNDLE_SELECTION_MODE, selectionMode)
            putParcelable(BUNDLE_DISPLAY_DATE_RANGE, displayedDatesRange)
            putParcelable(BUNDLE_LIMIT_DATE_RANGE, minMaxDatesRange)
            putParcelable(BUNDLE_SUPER_STATE, superState)
            putParcelable(BUNDLE_DISPLAYED_DATE, yearSelectionView.displayedDate)
            putBoolean(BUNDLE_SHOW_YEAR_SELECTION_VIEW, showYearSelectionView)
            firstDayOfWeek?.let { putInt(BUNDLE_FIRST_DAY_OF_WEEK, it) }
            dateSelectionStrategy.saveSelectedDates(this)
        }
    }

    /**
     * Restore internal calendar state.
     *
     * Note: If Calendar was initialized with [setupCalendar] method before [onRestoreInstanceState],
     * restoring of internal calendar state won't be performed, because the new state has been set up.
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState: Parcelable? = state.getParcelable(BUNDLE_SUPER_STATE)
            super.onRestoreInstanceState(superState)

            if (hasBeenInitializedWithSetup) {
                return
            }

            selectionMode = state.getSerializable(BUNDLE_SELECTION_MODE) as SelectionMode
            displayedDatesRange = state.getParcelable(BUNDLE_DISPLAY_DATE_RANGE) ?: DatesRange.emptyRange()
            minMaxDatesRange = state.getParcelable(BUNDLE_LIMIT_DATE_RANGE) ?: NullableDatesRange()
            showYearSelectionView = state.getBoolean(BUNDLE_SHOW_YEAR_SELECTION_VIEW)
            firstDayOfWeek = state.getInt(BUNDLE_FIRST_DAY_OF_WEEK, -1).takeIf { it != -1 }
            dateSelectionStrategy.restoreSelectedDates(state)

            val displayedDate: CalendarDate? = state.getParcelable(BUNDLE_DISPLAYED_DATE)
            if (displayedDate != null) {
                yearSelectionView.setupYearSelectionView(
                    displayedDate = displayedDate,
                    minMaxDatesRange = minMaxDatesRange
                )
            }

            generateCalendarItems(displayedDatesRange)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private inner class DefaultDateInfoProvider : DateInfoProvider {
        private val todayCalendarDate = CalendarDate.today

        override fun isToday(date: CalendarDate): Boolean {
            return date == todayCalendarDate
        }

        override fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState {
            return dateSelectionStrategy.getDateCellSelectedState(date)
        }

        override fun isDateOutOfRange(date: CalendarDate): Boolean {
            return minMaxDatesRange.isDateOutOfRange(date)
        }

        override fun isDateSelectable(date: CalendarDate): Boolean {
            return dateSelectionFilter?.invoke(date) ?: true
        }

        override fun isWeekend(date: CalendarDate): Boolean {
            return weekendFilter.invoke(date)
        }

        override fun getDateIndicators(date: CalendarDate): List<DateIndicator> {
            return this@CalendarView.getDateIndicators(date)
        }
    }

    private inner class CalendarItemsGenerationListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val gridLayoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return

            when {
                calendarAdapter.itemCount == gridLayoutManager.findLastVisibleItemPosition().inc() -> {
                    recyclerView.post { generateNextCalendarItems() }
                }

                gridLayoutManager.findFirstVisibleItemPosition() == 0 -> {
                    recyclerView.post { generatePrevCalendarItems() }
                }
            }
        }
    }

    private inner class DisplayedYearUpdateListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val gridLayoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return

            val firstChildAdapterPosition = gridLayoutManager.findFirstVisibleItemPosition()
            val calendarItem = calendarAdapter.getCalendarItemAt(firstChildAdapterPosition)

            when (calendarItem) {
                is DateItem -> yearSelectionView.displayedDate = calendarItem.date
                is MonthItem -> yearSelectionView.displayedDate = calendarItem.date
            }
        }
    }

}