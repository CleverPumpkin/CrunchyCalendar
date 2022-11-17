package ru.cleverpumpkin.crunchycalendar.calendarcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarConst.DAYS_IN_WEEK
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarConst.ELEMENTS_RANGE_FOR_TRIGGER
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarConst.MONTHS_PER_PAGE
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.CalendarItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.DateItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.EmptyItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.MonthTitleItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.*
import java.util.*

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

private lateinit var calendarItemsGenerator: CalendarItemsGenerator

private val defaultFirstDayOfWeek: Int
    get() = Calendar.getInstance().firstDayOfWeek

private var firstDayOfWeeks: Int? = null
    set(value) {
        field = value
        calendarItemsGenerator = CalendarItemsGenerator(firstDayOfWeeks ?: defaultFirstDayOfWeek)
    }

private var displayedDatesRange = DatesRange.emptyRange()
private var minMaxDatesRange = NullableDatesRange()

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


@Composable
fun CalendarScreen(
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
    firstDayOfWeeks = firstDayOfWeek
    minMaxDatesRange = NullableDatesRange(dateFrom = minDate, dateTo = maxDate)

    displayedDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
        initialDate = initialDate,
        minDate = minDate,
        maxDate = maxDate
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Calendar(listItems = generateCalendarItems(displayedDatesRange), initialDate)
        }
    }

}

@Composable
fun Calendar(
    listItems: List<CalendarItem>,
    initialDate: CalendarDate
) {
    val firstElement = findMonthPosition(listItems, initialDate)
    var itemsListState by remember { mutableStateOf(listItems) }
    val lazyGridState = rememberLazyGridState(firstElement)

    LazyVerticalGrid(
        columns = GridCells.Fixed(DAYS_IN_WEEK),
        state = lazyGridState,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    ) {
        items(
            itemsListState,
            key = { it.id },
            span = {
                if (it is MonthTitleItem) {
                    GridItemSpan(maxLineSpan)
                } else {
                    GridItemSpan(1)
                }
            }
        ) {
            when (it) {
                is MonthTitleItem -> {
                    MonthDateView(
                        date = it.date
                    )
                }
                is EmptyItem -> {
                    EmptyCalendarDateView()
                }
                is DateItem -> {
                    ComposeCalendarDateView(
                        dateInfoProvider = DefaultDateInfoProvider(),
                        date = it.date
                    )
                }
            }

            if (it == itemsListState[ELEMENTS_RANGE_FOR_TRIGGER]) {
                itemsListState = generatePrevCalendarItems() + itemsListState
            }
            if (it == itemsListState[itemsListState.size - ELEMENTS_RANGE_FOR_TRIGGER]) {
                itemsListState = itemsListState + generateNextCalendarItems()
            }
        }
    }

    /**
     * method with "infiniteList"
     */
//    lazyGridState.OnTopReached(ELEMENTS_RANGE_FOR_TRIGGER) {
//        itemsListState = generatePrevCalendarItems() + itemsListState
//    }
//
//    lazyGridState.OnBottomReached(ELEMENTS_RANGE_FOR_TRIGGER) {
//        itemsListState = itemsListState + generateNextCalendarItems()
//    }

}

@Composable
fun LazyGridState.OnTopReached(
    // tells how many items before we reach the top of the list
    // to call onLoadMore function
    buffer : Int = 0,
    onLoadMore : () -> Unit
) {
    // Buffer must be positive.
    // Or our list will never reach the top.
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

    val shouldLoadMore = remember {
        derivedStateOf {
            val firstVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()
                ?:
                return@derivedStateOf true

            // increase buffer to the total items
            firstVisibleItem.index >= layoutInfo.totalItemsCount + buffer
        }
    }

    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}

@Composable
fun LazyGridState.OnBottomReached(
    // tells how many items before we reach the bottom of the list
    // to call onLoadMore function
    buffer : Int = 0,
    onLoadMore : () -> Unit
) {
    // Buffer must be positive.
    // Or our list will never reach the bottom.
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?:
                return@derivedStateOf true

            // subtract buffer from the total items
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}

fun findMonthPosition(listItems: List<CalendarItem>, date: CalendarDate): Int {
    val year = date.year
    val month = date.month

    return listItems.indexOfFirst { item ->
        if (item is MonthTitleItem) {
            if (item.date.year == year && item.date.month == month) {
                return@indexOfFirst true
            }
        }

        return@indexOfFirst false
    }
}

private fun generateCalendarItems(datesRange: DatesRange): List<CalendarItem> {
    if (datesRange.isEmptyRange) {
        return emptyList()
    }

    return calendarItemsGenerator.generateCalendarItems(
        dateFrom = datesRange.dateFrom,
        dateTo = datesRange.dateTo
    )
}

private fun generatePrevCalendarItems(): List<CalendarItem> {
    val minDate = minMaxDatesRange.dateFrom
    if (minDate != null && minDate.monthsBetween(displayedDatesRange.dateFrom) == 0) {
        return emptyList()
    }

    val generateDatesFrom: CalendarDate
    val generateDatesTo = displayedDatesRange.dateFrom.minusMonths(1)

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

    displayedDatesRange = displayedDatesRange.copy(dateFrom = generateDatesFrom)

    return calendarItemsGenerator.generateCalendarItems(
        dateFrom = generateDatesFrom,
        dateTo = generateDatesTo
    )
}

private fun generateNextCalendarItems(): List<CalendarItem> {
    val maxDate = minMaxDatesRange.dateTo
    if (maxDate != null && displayedDatesRange.dateTo.monthsBetween(maxDate) == 0) {
        return emptyList()
    }

    val generateDatesFrom = displayedDatesRange.dateTo.plusMonths(1)

    val generateDatesTo: CalendarDate = if (maxDate != null) {
        val monthBetween = generateDatesFrom.monthsBetween(maxDate)

        if (monthBetween > MONTHS_PER_PAGE) {
            generateDatesFrom.plusMonths(MONTHS_PER_PAGE)
        } else {
            generateDatesFrom.plusMonths(monthBetween)
        }
    } else {
        generateDatesFrom.plusMonths(MONTHS_PER_PAGE)
    }

    displayedDatesRange = displayedDatesRange.copy(dateTo = generateDatesTo)

    return calendarItemsGenerator.generateCalendarItems(
        dateFrom = generateDatesFrom,
        dateTo = generateDatesTo
    )
}

private class DefaultDateInfoProvider : DateInfoProvider {
    private val todayCalendarDate = CalendarDate.today

    override fun isToday(date: CalendarDate): Boolean {
        return date == todayCalendarDate
    }

//    override fun getDateCellSelectedState(date: CalendarDate): DateCellSelectedState {
//        return dateSelectionStrategy.getDateCellSelectedState(date)
//    }

    override fun isDateOutOfRange(date: CalendarDate): Boolean {
        return minMaxDatesRange.isDateOutOfRange(date)
    }

    override fun isDateSelectable(date: CalendarDate): Boolean {
        return dateSelectionFilter?.invoke(date) ?: true
    }

    override fun isWeekend(date: CalendarDate): Boolean {
        return weekendFilter.invoke(date)
    }

//    override fun getDateIndicators(date: CalendarDate): List<DateIndicator> {
//        return this@CalendarView.getDateIndicators(date)
//    }
}