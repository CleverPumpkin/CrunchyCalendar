package ru.cleverpumpkin.calendar

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * This internal view class represents a bar with week days.
 * It is used as a part of the Calendar Widget.
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
        LayoutInflater.from(context).inflate(R.layout.view_days_bar, this, true)
    }

    fun applyStyle(style: DaysBarStyle) {
        setBackgroundColor(style.background)

        for (i in 0..childCount) {
            val dayView = getChildAt(i) as? TextView
            dayView?.setTextColor(style.textColor)
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

    class DaysBarStyle(
        @ColorInt val background: Int,
        @ColorInt val textColor: Int
    )
}