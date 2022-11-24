package ru.cleverpumpkin.crunchycalendar.calendarcompose.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.item.DateItem
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider

private const val DAY_OFF_BACKGROUND_COLOR = "#05000000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateContent(
    modifier: Modifier = Modifier,
    dateInfoProvider: DateInfoProvider,
    item: DateItem,
    selectedItems: List<CalendarDate>,
    onClick: (CalendarDate) -> Unit = {},
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick(item.date) },
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = if (selectedItems.contains(item.date)) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        } else {
            if (dateInfoProvider.isWeekend(item.date)) {
                CardDefaults.cardColors(
                    containerColor = Color(
                        android.graphics.Color.parseColor(
                            DAY_OFF_BACKGROUND_COLOR
                        )
                    )
                )
            } else {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = item.date.dayOfMonth.toString())
        }
    }
}