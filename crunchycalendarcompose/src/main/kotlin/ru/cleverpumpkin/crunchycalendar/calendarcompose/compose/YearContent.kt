package ru.cleverpumpkin.crunchycalendar.calendarcompose.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.R
import ru.cleverpumpkin.crunchycalendar.calendarcompose.utils.NullableDatesRange
import java.text.SimpleDateFormat
import java.util.*

private const val YEAR_FORMAT = "yyyy"

@SuppressLint("ConstantLocale")
private val yearFormatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())

@Composable
fun YearContent(
    modifier: Modifier = Modifier,
    displayedDate: CalendarDate,
    minMaxDatesRange: NullableDatesRange,
    onClick: ((CalendarDate) -> Unit)
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .clickable {
                    onArrowClick(
                        isPrev = true,
                        displayedDate,
                        minMaxDatesRange,
                        onClick
                    )
                },
            painter = painterResource(id = R.drawable.calendar_ic_arrow_left),
            contentDescription = ""
        )

        Text(
            modifier = Modifier
                .weight(5f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            text = yearFormatter.format(displayedDate.date)
        )

        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .clickable {
                    onArrowClick(
                        isPrev = false,
                        displayedDate,
                        minMaxDatesRange,
                        onClick
                    )
                },
            painter = painterResource(id = R.drawable.calendar_ic_arrow_right),
            contentDescription = ""
        )
    }
}

private fun onArrowClick(
    isPrev: Boolean,
    displayedDate: CalendarDate,
    minMaxDatesRange: NullableDatesRange,
    onClick: ((CalendarDate) -> Unit)
) {
    val (minDate, maxDate) = minMaxDatesRange

    val newDisplayedDate = if (isPrev) {
        val prevYear = displayedDate.minusMonths(CalendarDate.MONTHS_IN_YEAR)
        if (minDate == null || minDate <= prevYear) {
            prevYear
        } else {
            minDate
        }

    } else {
        val nextYear = displayedDate.plusMonths(CalendarDate.MONTHS_IN_YEAR)
        if (maxDate == null || maxDate >= nextYear) {
            nextYear
        } else {
            maxDate
        }
    }

    if (displayedDate.year != newDisplayedDate.year) {
        onClick(newDisplayedDate)
    }
}