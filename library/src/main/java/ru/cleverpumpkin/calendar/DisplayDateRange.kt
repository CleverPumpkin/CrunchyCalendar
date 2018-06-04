package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate

data class DisplayDateRange(val dateFrom: LocalDate, val dateTo: LocalDate) : Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<DisplayDateRange> {
            override fun createFromParcel(parcel: Parcel) = DisplayDateRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<DisplayDateRange>(size)
        }
    }

    constructor(parcel: Parcel) : this(
        dateFrom = LocalDate(parcel.readLong()),
        dateTo = LocalDate(parcel.readLong())
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(dateFrom.toDate().time)
        dest.writeLong(dateTo.toDate().time)
    }

    override fun describeContents() = 0
}