package ru.cleverpumpkin.crunchycalendar.calendarcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarConst.MONTHS_PER_PAGE
import ru.cleverpumpkin.crunchycalendar.calendarcompose.compose.MonthContent
import ru.cleverpumpkin.crunchycalendar.calendarcompose.compose.WeekDaysContent
import ru.cleverpumpkin.crunchycalendar.calendarcompose.compose.YearContent
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.*
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.*
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.selectionStategies.*
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
private var displayedDatesRange = DatesRange.emptyRange()
private var minMaxDatesRange = NullableDatesRange()
private val dateInfoProvider = DefaultDateInfoProvider()
private var dateSelectionStrategy: DateSelectionStrategy = NoDateSelectionStrategy()

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

private var settedSelectionMode: SelectionMode = SelectionMode.NONE
    set(value) {
        field = value

        dateSelectionStrategy = when (value) {
            SelectionMode.NONE -> {
                NoDateSelectionStrategy()
            }

            SelectionMode.SINGLE -> {
                SingleDateSelectionStrategy(dateInfoProvider)
            }

            SelectionMode.MULTIPLE -> {
                MultipleDateSelectionStrategy(dateInfoProvider)
            }

            SelectionMode.RANGE -> {
                RangeDateSelectionStrategy(dateInfoProvider)
            }
        }
    }

private val defaultFirstDayOfWeek: Int
    get() = Calendar.getInstance().firstDayOfWeek

private var firstWeekDay: Int? = null
    set(value) {
        field = value
        calendarItemsGenerator = CalendarItemsGenerator(firstWeekDay ?: defaultFirstDayOfWeek)
    }

val selectedDates: List<CalendarDate>
    get() = dateSelectionStrategy.getSelectedDates()

/**
 * Returns selected date or null according to the [settedSelectionMode].
 */
val selectedDate: CalendarDate?
    get() = dateSelectionStrategy.getSelectedDates()
        .firstOrNull()


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
    settedSelectionMode = selectionMode
    firstWeekDay = firstDayOfWeek
    minMaxDatesRange = NullableDatesRange(dateFrom = minDate, dateTo = maxDate)

    displayedDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
        initialDate = initialDate,
        minDate = minDate,
        maxDate = maxDate
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Calendar(
            listItems = generateCalendarItems(displayedDatesRange),
            initialDate = initialDate,
            initialSelectedDates = selectedDates,
            showYearSelectionView = showYearSelectionView
        )
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Calendar(
    listItems: List<MonthItem>,
    initialDate: CalendarDate,
    initialSelectedDates: List<CalendarDate>,
    showYearSelectionView: Boolean
) {
    val firstElement = listItems.findMonthPosition(initialDate)
    val pagerState = rememberPagerState(firstElement)
    val coroutineScope = rememberCoroutineScope()
    val selectedDates = remember { mutableStateOf(initialSelectedDates) }
    val displayedDate = remember { mutableStateOf(initialDate) }
    var itemsListState by rememberSaveable { mutableStateOf(listItems) }

    Column {
        if (showYearSelectionView) {
            YearContent(
                minMaxDatesRange = minMaxDatesRange,
                displayedDate = displayedDate.value,
                onClick = {
                    displayedDate.value = it
                    val newItems = moveToDate(it)
                    itemsListState = newItems.ifEmpty {
                        itemsListState
                    }

                    val dateMonthPosition = itemsListState.findMonthPosition(it)
                    coroutineScope.launch {
                        pagerState.scrollToPage(dateMonthPosition)
                    }
                }
            )
        }

        WeekDaysContent(firstDayOfWeek = firstWeekDay ?: defaultFirstDayOfWeek)

        VerticalPager(
            count = itemsListState.size,
            state = pagerState,
        ) { page ->
            MonthContent(
                dateInfoProvider = dateInfoProvider,
                item = itemsListState[page],
                selectedItems = selectedDates.value,
                onClick = { date ->
                    dateSelectionStrategy.onDateSelected(date)
                    selectedDates.value = dateSelectionStrategy.getSelectedDates()
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            pagerState.currentPage == 0
        }.filter { it }
            .collect {
                val newItems = generatePrevCalendarItems()
                itemsListState = newItems + itemsListState
                pagerState.scrollToPage(newItems.size)
            }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            pagerState.currentPage == itemsListState.size - 1
        }.filter { it }
            .collect {
                itemsListState = itemsListState + generateNextCalendarItems()
            }
    }

    if (showYearSelectionView) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                displayedDate.value = itemsListState[page].monthTitle.date
            }
        }
    }
}

private fun generateCalendarItems(datesRange: DatesRange): List<MonthItem> {
    if (datesRange.isEmptyRange) {
        return emptyList()
    }

    return calendarItemsGenerator.generateCalendarItems(
        dateFrom = datesRange.dateFrom,
        dateTo = datesRange.dateTo
    )
}

private fun generatePrevCalendarItems(): List<MonthItem> {
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

private fun generateNextCalendarItems(): List<MonthItem> {
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

fun moveToDate(date: CalendarDate): List<MonthItem> {
    val (minDate, maxDate) = minMaxDatesRange

    if ((minDate != null && date < minDate.monthBeginning()) ||
        (maxDate != null && date > maxDate.monthEnd())
    ) {
        return emptyList()
    }

    val (displayDatesFrom, displayDatesTo) = displayedDatesRange

    if (date.isBetween(dateFrom = displayDatesFrom, dateTo = displayDatesTo).not()) {
        displayedDatesRange = DisplayedDatesRangeFactory.getDisplayedDatesRange(
            initialDate = date,
            minDate = minDate,
            maxDate = maxDate
        )

        return generateCalendarItems(displayedDatesRange)
    }

    return emptyList()
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