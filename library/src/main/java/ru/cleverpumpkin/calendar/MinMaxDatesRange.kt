package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

class MinMaxDatesRange(val minDate: SimpleLocalDate?, val maxDate: SimpleLocalDate?) : Parcelable {

    companion object {
        private const val UNDEFINED_DATE = -1L

        private fun Long.toDateItem(): SimpleLocalDate? {
            return if (this != UNDEFINED_DATE) {
                SimpleLocalDate(this)
            } else {
                null
            }
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<MinMaxDatesRange> {
            override fun createFromParcel(parcel: Parcel) = MinMaxDatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<MinMaxDatesRange>(size)
        }
    }

    constructor(parcel: Parcel) : this(
        minDate = parcel.readLong().toDateItem(),
        maxDate = parcel.readLong().toDateItem()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(minDate?.toDate()?.time ?: UNDEFINED_DATE)
        dest.writeLong(maxDate?.toDate()?.time ?: UNDEFINED_DATE)
    }

    override fun describeContents() = 0
}