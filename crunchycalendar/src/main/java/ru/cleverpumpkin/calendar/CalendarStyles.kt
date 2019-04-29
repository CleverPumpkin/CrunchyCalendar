package ru.cleverpumpkin.calendar

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.cleverpumpkin.calendar.extension.getColorInt

/**
 * Created by Alexander Surinov on 29/04/2019.
 */
internal class CalendarStyles(
    context: Context,

    var drawGridOnSelectedDates: Boolean = true,

    @ColorInt
    var gridColor: Int =
        context.getColorInt(R.color.calendar_grid_color),

    @ColorInt
    var yearSelectionBackground: Int =
        context.getColorInt(R.color.calendar_year_selection_background),

    @ColorInt
    var yearSelectionArrowsColor: Int =
        context.getColorInt(R.color.calendar_year_selection_arrows_color),

    @ColorInt
    var yearSelectionTextColor: Int =
        context.getColorInt(R.color.calendar_year_selection_text_color),

    @ColorInt
    var daysBarBackground: Int =
        context.getColorInt(R.color.calendar_days_bar_background),

    @ColorInt
    var daysBarTextColor: Int =
        context.getColorInt(R.color.calendar_days_bar_text_color),

    @ColorInt
    var monthTextColor: Int =
        context.getColorInt(R.color.calendar_month_text_color),

    @DrawableRes
    var dateCellBackgroundColorRes: Int = R.drawable.calendar_date_bg_selector,

    @ColorRes
    var dateTextColorRes: Int = R.color.calendar_date_text_selector

)