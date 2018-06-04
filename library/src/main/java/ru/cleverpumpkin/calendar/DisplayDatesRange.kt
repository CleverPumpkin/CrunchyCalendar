package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

data class DisplayDatesRange(
    val dateFrom: SimpleLocalDate,
    val dateTo: SimpleLocalDate
) : Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<DisplayDatesRange> {
            override fun createFromParcel(parcel: Parcel) = DisplayDatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<DisplayDatesRange>(size)
        }
    }

    constructor(parcel: Parcel) : this(
        dateFrom = SimpleLocalDate(parcel.readLong()),
        dateTo = SimpleLocalDate(parcel.readLong())
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(dateFrom.toDate().time)
        dest.writeLong(dateTo.toDate().time)
    }

    override fun describeContents() = 0
}