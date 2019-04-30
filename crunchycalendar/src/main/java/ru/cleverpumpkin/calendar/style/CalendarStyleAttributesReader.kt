package ru.cleverpumpkin.calendar.style

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import ru.cleverpumpkin.calendar.R

internal object CalendarStyleAttributesReader {

    fun readStyleAttributes(
        context: Context,
        attrs: AttributeSet,
        @AttrRes defStyleAttr: Int,
        styleAttributes: CalendarStyleAttributes
    ) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, 0)

        try {
            with(styleAttributes) {
                drawGridOnSelectedDates = typedArray.getBoolean(
                    R.styleable.CalendarView_calendar_grid_on_selected_dates,
                    drawGridOnSelectedDates
                )

                gridColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_grid_color,
                    gridColor
                )

                yearSelectionBackground = typedArray.getColor(
                    R.styleable.CalendarView_calendar_year_selection_background,
                    yearSelectionBackground
                )

                yearSelectionArrowsColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_year_selection_arrows_color,
                    yearSelectionArrowsColor
                )

                yearSelectionTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_year_selection_text_color,
                    yearSelectionTextColor
                )

                daysBarBackground = typedArray.getColor(
                    R.styleable.CalendarView_calendar_day_bar_background,
                    daysBarBackground
                )

                daysBarTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_day_bar_text_color,
                    daysBarTextColor
                )

                monthTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_month_text_color,
                    monthTextColor
                )

                dateCellBackgroundColorRes = typedArray.getResourceId(
                    R.styleable.CalendarView_calendar_date_background,
                    dateCellBackgroundColorRes
                )

                dateCellTextColorStateList = typedArray.getColorStateList(
                    R.styleable.CalendarView_calendar_date_text_color
                ) ?: dateCellTextColorStateList
            }
        } finally {
            typedArray.recycle()
        }
    }

}