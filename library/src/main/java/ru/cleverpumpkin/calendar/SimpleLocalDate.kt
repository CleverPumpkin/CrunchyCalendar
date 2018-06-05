package ru.cleverpumpkin.calendar

import java.util.*

class SimpleLocalDate(date: Date) : Comparable<SimpleLocalDate> {

    constructor(dateInMillis: Long) : this(Date(dateInMillis))

    private val calendar = Calendar.getInstance().apply { this.time = date }

    val year: Int
        get() = calendar.get(Calendar.YEAR)

    val month: Int
        get() = calendar.get(Calendar.MONTH)

    val dayOfMonth: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)

    override fun compareTo(other: SimpleLocalDate): Int {
        var result = year - other.year
        if (result == 0) {
            result = month - other.month
            if (result == 0) {
                result = dayOfMonth - other.dayOfMonth
            }
        }

        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleLocalDate

        if (year != other.year) return false
        if (month != other.month) return false
        if (dayOfMonth != other.dayOfMonth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + dayOfMonth
        return result
    }

    fun minusMonths(monthsCount: Int): SimpleLocalDate {
        val tmpCalendar = Calendar.getInstance().apply {
            time = calendar.time
            add(Calendar.MONTH, monthsCount.unaryMinus())
        }

        return SimpleLocalDate(tmpCalendar.time)
    }

    fun plusMonths(monthsCount: Int): SimpleLocalDate {
        val tmpCalendar = Calendar.getInstance().apply {
            time = calendar.time
            add(Calendar.MONTH, monthsCount)
        }

        return SimpleLocalDate(tmpCalendar.time)
    }

    fun toDate(): Date = calendar.time

    fun toMillis(): Long = calendar.timeInMillis
}