package ru.cleverpumpkin.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import ru.cleverpumpkin.calendar.style.CalendarStyleAttributes
import java.text.SimpleDateFormat
import java.util.*

/**
 * This internal view class represents a bar with week days.
 * It is used as a part of the [CalendarView].
 */
internal class DaysBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DAY_OF_WEEK_FORMAT = "EE"
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.calendar_days_bar_view, this, true)
    }

    fun applyStyle(styleAttributes: CalendarStyleAttributes) {
        setBackgroundColor(styleAttributes.daysBarBackground)

        for (i in 0..childCount) {
            val dayView = getChildAt(i) as? TextView
            if (i > 4) {
                dayView?.setTextColor(styleAttributes.daysBarWeekendTextColor)
            } else {
                dayView?.setTextColor(styleAttributes.daysBarTextColor)
            }
        }
    }

    fun setupDaysBarView(firstDayOfWeek: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek)

        val dayOfWeekFormatter = SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault())

        for (i in 0 until childCount) {
            val dayView = getChildAt(i) as TextView
            dayView.text = dayOfWeekFormatter.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

}