package ru.cleverpumpkin.crunchycalendar.calendarcompose.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.MonthItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider

@Composable
fun MonthContent(
    modifier: Modifier = Modifier,
    dateInfoProvider: DateInfoProvider,
    item: MonthItem,
    selectedItems: List<CalendarDate>,
    onClick: (CalendarDate) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        MonthTitleContent(date = item.monthTitle.date)

        val list = item.dates.chunked(7)

        list.forEach { week ->
            WeekContent(
                dateInfoProvider = dateInfoProvider,
                week = week,
                selectedItems = selectedItems,
                onClick = onClick
            )
        }
    }

}