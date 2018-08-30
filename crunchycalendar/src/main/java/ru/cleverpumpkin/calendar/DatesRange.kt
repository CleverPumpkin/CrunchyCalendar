package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

/**
 * This internal class represents a range of dates from [dateFrom] to [dateTo].
 *
 * This class implements [Parcelable] interface so instances of the class
 * can be stored in a [Parcel] object.
 */
internal data class DatesRange(
    val dateFrom: CalendarDate,
    val dateTo: CalendarDate

) : Parcelable {

    constructor(parcel: Parcel) : this(
        dateFrom = parcel.readParcelable(CalendarDate::class.java.classLoader),
        dateTo = parcel.readParcelable(CalendarDate::class.java.classLoader)
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