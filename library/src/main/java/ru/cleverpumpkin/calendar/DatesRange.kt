package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * This class represents a range of dates from [dateFrom] to [dateTo].
 *
 * This class implements [Parcelable] interface so instances of this class
 * can be stored in [Parcel] object.
 */
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

    //TODO
    //1) лучше было бы конвертнуть в проперти
    //2) можно конвертнуть в expression-body
    fun isEmptyRange(): Boolean {
        return dateFrom == dateTo
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(dateFrom, flags)
        dest.writeParcelable(dateTo, flags)
    }

    override fun describeContents() = 0
}