package ru.cleverpumpkin.crunchycalendar.calendarcompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.DateInfoProvider
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeCalendarDateView(
    modifier: Modifier = Modifier,
    dateInfoProvider: DateInfoProvider,
    date: CalendarDate,
    onClick: (CalendarDate) -> Unit = {},
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = if (dateInfoProvider.isWeekend(date)) {
            CardDefaults.cardColors(containerColor = Color(android.graphics.Color.parseColor("#05000000")))
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick(date) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = date.dayOfMonth.toString())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyCalendarDateView(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center,
        ) {

        }
    }
}

@Composable
fun MonthDateView(
    date: CalendarDate,
    modifier: Modifier = Modifier
) {
    val MONTH_FORMAT = "LLLL yyyy"
    val monthFormatter = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())

    Row(modifier = modifier) {
        Text(text = monthFormatter.format(date.date))
    }
}