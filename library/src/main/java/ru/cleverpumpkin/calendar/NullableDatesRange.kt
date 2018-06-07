package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

data class NullableDatesRange(
    val dateFrom: CalendarDate? = null,
    val dateTo: CalendarDate? = null

) : Parcelable {

    constructor(parcel: Parcel) : this(
        dateFrom = parcel.readParcelable(CalendarDate::class.java.classLoader),
        dateTo = parcel.readParcelable(CalendarDate::class.java.classLoader)
    )

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<NullableDatesRange> {
            override fun createFromParcel(parcel: Parcel) = NullableDatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<NullableDatesRange>(size)
        }

    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(dateFrom, flags)
        dest.writeParcelable(dateTo, flags)
    }

    override fun describeContents() = 0
}