package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

data class DatesRange(
    val dateFrom: SimpleLocalDate,
    val dateTo: SimpleLocalDate
) : Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<DatesRange> {
            override fun createFromParcel(parcel: Parcel) = DatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<DatesRange>(size)
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