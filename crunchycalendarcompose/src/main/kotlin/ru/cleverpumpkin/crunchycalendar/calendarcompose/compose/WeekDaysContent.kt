package ru.cleverpumpkin.crunchycalendar.calendarcompose.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.cleverpumpkin.crunchycalendar.calendarcompose.R
import java.text.SimpleDateFormat
import java.util.*

private const val DAY_OF_WEEK_FORMAT = "EE"

@Composable
fun WeekDaysContent(
    modifier: Modifier = Modifier,
    firstDayOfWeek: Int
) {
    val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek)

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        (0..6).forEach { _ ->
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeekFormatter.format(calendar.time)
            )

            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    Image(
        painter = rememberAsyncImagePainter(R.drawable.calendar_days_of_week_bar_shadow),
        contentDescription = "",
        modifier = Modifier.fillMaxWidth().height(4.dp)
    )
}