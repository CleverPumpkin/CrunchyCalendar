package ru.cleverpumpkin.calendar

import org.joda.time.LocalDate

class SelectedDates {

    val dates: MutableCollection<LocalDate> = linkedSetOf()

    fun restoreSelectedDatesFromLongArray(longArray: LongArray) {
        longArray.mapTo(dates) { LocalDate(it) }
    }

    fun mapSelectedDatesToLongArray(): LongArray {
        val longArray = LongArray(dates.size)

        dates.forEachIndexed { i, localDate ->
            longArray[i] = localDate.toDate().time
        }

        return longArray
    }
}