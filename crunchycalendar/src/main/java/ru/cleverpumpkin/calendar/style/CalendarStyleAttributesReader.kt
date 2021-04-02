package ru.cleverpumpkin.calendar.style

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import ru.cleverpumpkin.calendar.R

/**
 * This class responsible for reading Calendar's style XML attributes.
 */
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

                daysBarWeekendTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_day_bar_weekend_text_color,
                    daysBarWeekendTextColor
                )

                monthTextColor = typedArray.getColor(
                    R.styleable.CalendarView_calendar_month_text_color,
                    monthTextColor
                )

                monthTextSize = typedArray.getDimension(
                    R.styleable.CalendarView_calendar_month_text_size,
                    monthTextSize
                )

                monthTextStyle = typedArray.getInt(
                    R.styleable.CalendarView_calendar_month_text_style,
                    monthTextStyle
                )

                dateCellBackgroundShapeForm = typedArray.getResourceId(
                    R.styleable.CalendarView_calendar_date_background,
                    dateCellBackgroundShapeForm
                )

                dateCellBackgroundColorRes = typedArray.getResourceId(
                    R.styleable.CalendarView_calendar_date_background_tint,
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