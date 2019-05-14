package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

/**
 * This internal class represents a range of dates from [dateFrom] to [dateTo].
 */
internal data class DatesRange(
    val dateFrom: CalendarDate,
    val dateTo: CalendarDate

) : Parcelable {

    constructor(parcel: Parcel) : this(
        dateFrom = requireNotNull(parcel.readParcelable(CalendarDate::class.java.classLoader)),
        dateTo = requireNotNull(parcel.readParcelable(CalendarDate::class.java.classLoader))
    )

    companion object {

        fun emptyRange(): DatesRange {
            val todayCalendarDate = CalendarDate.today
            return DatesRange(todayCalendarDate, todayCalendarDate)
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<DatesRange> {
            override fun createFromParcel(parcel: Parcel) = DatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<DatesRange>(size)
        }
    }

    val isEmptyRange: Boolean get() = dateFrom == dateTo

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(dateFrom, flags)
        dest.writeParcelable(dateTo, flags)
    }

    override fun describeContents() = 0

}