package ru.cleverpumpkin.calendar.selection

import android.os.Bundle
import ru.cleverpumpkin.calendar.SimpleLocalDate

interface DateSelectionStrategy {

    fun onDateSelected(date: SimpleLocalDate, datePosition: Int)

    fun isDateSelected(date: SimpleLocalDate): Boolean

    fun saveSelectionState(bundle: Bundle)

    fun restoreSelectionState(bundle: Bundle)
}