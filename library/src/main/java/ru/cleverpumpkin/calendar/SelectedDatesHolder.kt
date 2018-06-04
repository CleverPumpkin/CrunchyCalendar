package ru.cleverpumpkin.calendar

class SelectedDatesHolder {

    val selectedDates: MutableCollection<SimpleLocalDate> = linkedSetOf()

    fun restoreSelectedDatesFromLongArray(longArray: LongArray) {
        selectedDates.clear()
        longArray.mapTo(selectedDates) { SimpleLocalDate(it) }
    }

    fun mapSelectedDatesToLongArray(): LongArray {
        val longArray = LongArray(selectedDates.size)

        selectedDates.forEachIndexed { i, localDate ->
            longArray[i] = localDate.toDate().time
        }

        return longArray
    }
}