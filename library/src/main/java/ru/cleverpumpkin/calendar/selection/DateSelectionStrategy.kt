package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.CalendarDate

interface DateSelectionStrategy {

    fun onDateSelected(date: CalendarDate)

    fun isDateSelected(date: CalendarDate): Boolean

    fun getSelectedDates(): List<CalendarDate>

    fun saveSelectedDates(bundle: Bundle)

    fun restoreSelectedDates(bundle: Bundle)
}