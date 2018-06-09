package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class DatesRange(
    val dateFrom: CalendarDate,
    val dateTo: CalendarDate

) : Parcelable {

    constructor(parcel: Parcel) : this(
        dateFrom = parcel.readParcelable(CalendarDate::class.java.classLoader),
        dateTo = parcel.readParcelable(CalendarDate::class.java.classLoader)
    )

    companion object {

        fun emptyRange(): DatesRange {
            val nowCalendarDate = CalendarDate(Date())
            return DatesRange(nowCalendarDate, nowCalendarDate)
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<DatesRange> {
            override fun createFromParcel(parcel: Parcel) = DatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<DatesRange>(size)
        }
    }

    fun isEmptyRange(): Boolean {
        return dateFrom == dateTo
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(dateFrom, flags)
        dest.writeParcelable(dateTo, flags)
    }

    override fun describeContents() = 0
}