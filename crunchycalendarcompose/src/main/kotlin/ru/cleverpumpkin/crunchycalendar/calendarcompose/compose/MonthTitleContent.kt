package ru.cleverpumpkin.crunchycalendar.calendarcompose.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonthTitleContent(
    date: CalendarDate, modifier: Modifier = Modifier
) {
    val monthFormat = "LLLL yyyy"
    val monthFormatter = SimpleDateFormat(monthFormat, Locale.getDefault())
    val monthName = monthFormatter.format(date.date)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Text(
            modifier = Modifier.padding(all = 16.dp),
            fontSize = 21.sp,
            text = monthName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
        )
    }
}