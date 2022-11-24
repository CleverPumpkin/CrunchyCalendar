package ru.cleverpumpkin.crunchycalendar.calendarcompose.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.CalendarItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.DateItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.EmptyItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider

@Composable
fun WeekContent(
    modifier: Modifier = Modifier,
    dateInfoProvider: DateInfoProvider,
    week: List<CalendarItem>,
    selectedItems: List<CalendarDate>,
    onClick: (CalendarDate) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        week.forEachIndexed { index, day ->
            Box(
                modifier = Modifier.fillMaxWidth(1f / (7 - index))
            ) {
                when (day) {
                    is EmptyItem -> {
                        EmptyDateContent()
                    }
                    is DateItem -> {
                        DateContent(
                            dateInfoProvider = dateInfoProvider,
                            item = day,
                            selectedItems =selectedItems,
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}