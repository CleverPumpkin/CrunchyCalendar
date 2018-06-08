package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * TODO Describe class
 */
class CalendarDate(date: Date) : Parcelable, Comparable<CalendarDate> {

    constructor(dateInMillis: Long) : this(Date(dateInMillis))

    constructor(parcel: Parcel) : this(parcel.readLong())

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<CalendarDate> {
            override fun createFromParcel(parcel: Parcel) = CalendarDate(parcel)

            override fun newArray(size: Int) = arrayOfNulls<CalendarDate>(size)
        }
    }

    private val calendar = Calendar.getInstance().apply { this.time = date }

    val year: Int
        get() = calendar.get(Calendar.YEAR)

    val month: Int
        get() = calendar.get(Calendar.MONTH)

    val dayOfMonth: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)

    val date: Date
        get() = calendar.time


    override fun compareTo(other: CalendarDate): Int {
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

        other as CalendarDate

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

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(calendar.timeInMillis)
    }

    override fun describeContents() = 0

    fun minusMonths(monthsCount: Int): CalendarDate {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.time = calendar.time
        tmpCalendar.add(Calendar.MONTH, monthsCount.unaryMinus())

        return CalendarDate(tmpCalendar.time)
    }

    fun plusMonths(monthsCount: Int): CalendarDate {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.time = calendar.time
        tmpCalendar.add(Calendar.MONTH, monthsCount)

        return CalendarDate(tmpCalendar.time)
    }
}