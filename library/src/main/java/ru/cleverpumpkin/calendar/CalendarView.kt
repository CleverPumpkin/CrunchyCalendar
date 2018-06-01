package ru.cleverpumpkin.calendar

import android.content.Context
import android.os.Parcel
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
import org.joda.time.LocalDate
import ru.cleverpumpkin.calendar.decorations.GridDividerItemDecoration
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val UNDEFINED_VALUE = -1L

        private const val DAY_OF_WEEK_FORMAT = "EE"
        private const val DAYS_IN_WEEK = 7
        private const val MAX_RECYCLED_DAY_VIEWS = 90
        private const val MONTHS_PER_PAGE = 6
    }

    private val daysContainer: ViewGroup
    private val recyclerView: RecyclerView

    private val calendarAdapter = CalendarAdapter()
    private var calendarInitialized = false

    private lateinit var loadedFrom: LocalDate
    private lateinit var loadedTo: LocalDate

    private var minDate: LocalDate? = null
    private var maxDate: LocalDate? = null

    private val calendarItemsGenerator = CalendarItemsGenerator()

    init {
        Log.d("CalendarView", "init")
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)

        daysContainer = findViewById(R.id.days_container)
        recyclerView = findViewById(R.id.recycler_view)

        setupRecyclerView(recyclerView)
        setupDaysContainer(daysContainer)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
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
            this.adapter = calendarAdapter
            this.layoutManager = layoutManager

            this.recycledViewPool.setMaxRecycledViews(
                CalendarAdapter.DAY_VIEW_TYPE,
                MAX_RECYCLED_DAY_VIEWS
            )

            this.addItemDecoration(GridDividerItemDecoration())

            val calendarScrollListener = CalendarScrollListener(
                onTheTop = Runnable { generatePrevMonthsItems() },
                onTheBottom = Runnable { generateNextMonthsItems() }
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

    fun init(initialDate: Date = Date(), minDate: Date? = null, maxDate: Date? = null) {

        when {
            minDate == null && maxDate == null -> {
                val loadedFrom = LocalDate.fromDateFields(initialDate)
                    .minusMonths(MONTHS_PER_PAGE)

                val loadedTo = LocalDate.fromDateFields(initialDate)
                    .plusMonths(MONTHS_PER_PAGE)

                val calendarItems = calendarItemsGenerator.generateCalendarItems(
                    fromDate = loadedFrom.toDate(),
                    toDate = loadedTo.toDate()
                )

                calendarAdapter.setItems(calendarItems)

                this.loadedFrom = loadedFrom
                this.loadedTo = loadedTo
            }

            minDate != null && maxDate != null -> {
                val loadedFrom = LocalDate.fromDateFields(minDate)
                val loadedTo = LocalDate.fromDateFields(maxDate)

                val calendarItems = calendarItemsGenerator.generateCalendarItems(
                    fromDate = loadedFrom.toDate(),
                    toDate = loadedTo.toDate()
                )

                calendarAdapter.setItems(calendarItems)

                this.minDate = loadedFrom
                this.maxDate = loadedTo

                this.loadedFrom = loadedFrom
                this.loadedTo = loadedTo
            }

            minDate != null && maxDate == null -> {
                val loadedFrom = LocalDate.fromDateFields(minDate)

                val loadedTo = LocalDate.fromDateFields(minDate)
                    .plusMonths(MONTHS_PER_PAGE)

                val calendarItems = calendarItemsGenerator.generateCalendarItems(
                    fromDate = loadedFrom.toDate(),
                    toDate = loadedTo.toDate()
                )

                calendarAdapter.setItems(calendarItems)

                this.minDate = loadedFrom

                this.loadedFrom = loadedFrom
                this.loadedTo = loadedTo
            }

            minDate == null && maxDate != null -> {
                val loadedFrom = LocalDate.fromDateFields(maxDate)
                    .minusMonths(MONTHS_PER_PAGE)

                val loadedTo = LocalDate.fromDateFields(maxDate)

                val calendarItems = calendarItemsGenerator.generateCalendarItems(
                    fromDate = loadedFrom.toDate(),
                    toDate = loadedTo.toDate()
                )

                calendarAdapter.setItems(calendarItems)

                this.maxDate = loadedTo

                this.loadedFrom = loadedFrom
                this.loadedTo = loadedTo
            }
        }

        val localInitialDate = LocalDate.fromDateFields(initialDate)
        val initialMonthPosition = calendarAdapter.findMonthItemPosition(
            localInitialDate.year,
            localInitialDate.monthOfYear
        )

        if (initialMonthPosition != -1) {
            recyclerView.scrollToPosition(initialMonthPosition)
        }

        calendarInitialized = true
    }

    private fun generatePrevMonthsItems() {
        if (this.minDate != null) {
            return
        }

        val loadedTo = this.loadedFrom.minusMonths(1)

        val loadedFrom = LocalDate.fromDateFields(loadedTo.toDate())
            .minusMonths(MONTHS_PER_PAGE)

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            fromDate = loadedFrom.toDate(),
            toDate = loadedTo.toDate()
        )

        calendarAdapter.addPrevCalendarItems(calendarItems)
        this.loadedFrom = loadedFrom
    }

    private fun generateNextMonthsItems() {
        if (this.maxDate != null) {
            return
        }

        val loadedFrom = this.loadedTo.plusMonths(1)

        val loadedTo = LocalDate.fromDateFields(loadedFrom.toDate())
            .plusMonths(MONTHS_PER_PAGE)

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            fromDate = loadedFrom.toDate(),
            toDate = loadedTo.toDate()
        )

        calendarAdapter.addNextCalendarItems(calendarItems)
        this.loadedTo = loadedTo
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        val savedState = SavedState(superState)
        savedState.loadedFrom = loadedFrom.toDate().time
        savedState.loadedTo = loadedTo.toDate().time
        savedState.minDate = minDate?.toDate()?.time ?: UNDEFINED_VALUE
        savedState.maxDate = maxDate?.toDate()?.time ?: UNDEFINED_VALUE

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        if (calendarInitialized) {
            return
        }

        loadedFrom = LocalDate(savedState.loadedFrom)
        loadedTo = LocalDate(savedState.loadedTo)

        minDate = if (savedState.minDate == UNDEFINED_VALUE) {
            null
        } else {
            LocalDate(savedState.minDate)
        }

        maxDate = if (savedState.maxDate == UNDEFINED_VALUE) {
            null
        } else {
            LocalDate(savedState.maxDate)
        }

        val calendarItems = calendarItemsGenerator.generateCalendarItems(
            fromDate = loadedFrom.toDate(),
            toDate = loadedTo.toDate()
        )

        calendarAdapter.setItems(calendarItems)
    }

    class SavedState : BaseSavedState {
        var loadedFrom: Long = 0
        var loadedTo: Long = 0
        var minDate: Long = 0
        var maxDate: Long = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(inParcel: Parcel) : super(inParcel) {
            loadedFrom = inParcel.readLong()
            loadedTo = inParcel.readLong()
            minDate = inParcel.readLong()
            maxDate = inParcel.readLong()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeLong(loadedFrom)
            out.writeLong(loadedTo)
            out.writeLong(minDate)
            out.writeLong(maxDate)
        }

        @Suppress("PropertyName")
        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

            override fun createFromParcel(`in`: Parcel) = SavedState(`in`)

            override fun newArray(size: Int) = arrayOfNulls<SavedState>(size)
        }
    }
}