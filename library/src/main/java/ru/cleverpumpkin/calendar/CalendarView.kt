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
 * TODO Describe class
 */
class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * TODO Describe interface
     */
    interface OnDateSelectedListener {

        fun onDateSelected(date: Date)

        fun onDateUnselected(date: Date)
    }

    /**
     * TODO Describe interface
     */
    interface DateInfoProvider {

        fun isToday(date: CalendarDate): Boolean

        fun isDateSelected(date: CalendarDate): Boolean

        fun isDateEnabled(date: CalendarDate): Boolean
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
     * TODO Describe selection mode
     */
    enum class SelectionMode {
        NON,
        SINGLE,
        MULTIPLE,
        RANGE
    }

    private var dividerColor = context.getColorInt(R.color.default_divider_color)
    private var dayBarBackground = context.getColorInt(R.color.white_FFFFFF)
    private var dayBarTextColor = context.getColorInt(R.color.grey_AD000000)

    private val daysContainer: ViewGroup
    private val recyclerView: RecyclerView
    private val calendarAdapter: CalendarAdapter

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

    var onDateSelectedListener: OnDateSelectedListener? = null

    val selectedDates: List<CalendarDate>
        get() = dateSelectionStrategy.getSelectedDates()

    val selectedDate: CalendarDate?
        get() = dateSelectionStrategy.getSelectedDates()
            .firstOrNull()

    fun addCustomItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(itemDecoration)
    }

    fun removeCustomItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration)
    }

    /**
     * TODO Describe init section
     */
    init {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)

        var monthTextColor: Int = context.getColorInt(R.color.grey_AD000000)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)

            dividerColor = typedArray.getColor(
                R.styleable.CalendarView_cpcalendar_divider_color,
                context.getColorInt(R.color.default_divider_color)
            )

            dayBarBackground = typedArray.getColor(
                R.styleable.CalendarView_cpcalendar_day_bar_background,
                context.getColorInt(R.color.white_FFFFFF)
            )

            dayBarTextColor = typedArray.getColor(
                R.styleable.CalendarView_cpcalendar_day_bar_text_color,
                context.getColorInt(R.color.grey_AD000000)
            )

            monthTextColor = typedArray.getColor(
                R.styleable.CalendarView_cpcalendar_month_text_color,
                context.getColorInt(R.color.grey_AD000000)
            )

            typedArray.recycle()
        }

        daysContainer = findViewById(R.id.days_container)
        recyclerView = findViewById(R.id.recycler_view)

        val adapterViewAttributes = CalendarAdapter.AdapterViewAttributes(
            monthTextColor = monthTextColor
        )

        calendarAdapter = CalendarAdapter(
            adapterViewAttributes = adapterViewAttributes,
            dateInfoProvider = DateInfoProviderImpl(),
            onDateClickHandler = { calendarDate ->
                dateSelectionStrategy.onDateSelected(calendarDate)

                if (dateSelectionStrategy.isDateSelected(calendarDate)) {
                    onDateSelectedListener?.onDateSelected(calendarDate.date)
                } else {
                    onDateSelectedListener?.onDateUnselected(calendarDate.date)
                }
            }
        )

        setupRecyclerView(recyclerView)
        setupDayBar(daysContainer)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val gridLayoutManager = GridLayoutManager(context, DAYS_IN_WEEK)

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
            setHasFixedSize(true)
            addItemDecoration(GridDividerItemDecoration(context, dividerColor))
            addOnScrollListener(CalendarScrollListener())

            recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.DATE_VIEW_TYPE,
                MAX_RECYCLED_DAY_VIEWS
            )

            recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.EMPTY_VIEW_TYPE,
                MAX_RECYCLED_EMPTY_VIEWS
            )
        }
    }

    private fun setupDayBar(weekDaysContainer: ViewGroup) {
        if (weekDaysContainer.childCount != DAYS_IN_WEEK) {
            throw IllegalStateException("Days container has incorrect number of child views")
        }

        weekDaysContainer.setBackgroundColor(dayBarBackground)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())

        for (dayPosition in 0 until DAYS_IN_WEEK) {
            val dayView = weekDaysContainer.getChildAt(dayPosition) as TextView
            dayView.setTextColor(dayBarTextColor)
            dayView.text = dayOfWeekFormatter.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    /**
     * TODO Describe setup method
     */
    fun setup(
        initialDate: CalendarDate = CalendarDate(Date()),
        minDate: Date? = null,
        maxDate: Date? = null,
        selectionMode: SelectionMode = SelectionMode.NON
    ) {
        when {
            minDate == null && maxDate == null -> {
                val dateFrom = initialDate.minusMonths(MONTHS_PER_PAGE)
                val dateTo = initialDate.plusMonths(MONTHS_PER_PAGE)

                generateCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange()
            }

            minDate != null && maxDate != null -> {
                val dateFrom = CalendarDate(minDate)
                val dateTo = CalendarDate(maxDate)

                generateCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange(dateFrom = dateFrom, dateTo = dateTo)
            }

            minDate != null && maxDate == null -> {
                val dateFrom = CalendarDate(minDate)
                val dateTo = dateFrom.plusMonths(MONTHS_PER_PAGE)

                generateCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange(dateFrom = dateFrom)
            }

            minDate == null && maxDate != null -> {
                val dateTo = CalendarDate(maxDate)
                val dateFrom = dateTo.minusMonths(MONTHS_PER_PAGE)

                generateCalendarItems(dateFrom = dateFrom, dateTo = dateTo)

                displayDatesRange = DatesRange(dateFrom = dateFrom, dateTo = dateTo)
                minMaxDatesRange = NullableDatesRange(dateTo = dateTo)
            }
        }

        val initialMonthPosition = calendarAdapter.findMonthItemPosition(initialDate)
        if (initialMonthPosition != -1) {
            recyclerView.scrollToPosition(initialMonthPosition)
        }

        this.selectionMode = selectionMode
    }


    private fun generateCalendarItems(dateFrom: CalendarDate, dateTo: CalendarDate) {
        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            dateFrom = dateFrom.date,
            dateTo = dateTo.date
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
            dateFrom = dateFrom.date,
            dateTo = dateTo.date
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
            dateFrom = fromDate.date,
            dateTo = toDate.date
        )

        calendarAdapter.addNextCalendarItems(calendarItems)
        displayDatesRange = displayDatesRange.copy(dateTo = toDate)
    }


    /**
     * TODO Describe save/restore logic
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

        generateCalendarItems(
            dateFrom = displayDatesRange.dateFrom,
            dateTo = displayDatesRange.dateTo
        )
    }

    /**
     * TODO Describe class
     */
    private inner class DateInfoProviderImpl : DateInfoProvider {

        private val todayCalendarDate = CalendarDate(Date())

        override fun isToday(date: CalendarDate): Boolean {
            return date == todayCalendarDate
        }

        override fun isDateSelected(date: CalendarDate): Boolean {
            return dateSelectionStrategy.isDateSelected(date)
        }

        override fun isDateEnabled(date: CalendarDate): Boolean {
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

    /**
     * TODO Describe class
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