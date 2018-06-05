package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable

data class NullableDatesRange(
    val dateFrom: SimpleLocalDate? = null,
    val dateTo: SimpleLocalDate? = null
) : Parcelable {

    companion object {
        private const val UNDEFINED_DATE = -1L

        @JvmField
        val CREATOR = object : Parcelable.Creator<NullableDatesRange> {
            override fun createFromParcel(parcel: Parcel) = NullableDatesRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<NullableDatesRange>(size)
        }

        private fun Long.toDateItem(): SimpleLocalDate? {
            return if (this != UNDEFINED_DATE) {
                SimpleLocalDate(this)
            } else {
                null
            }
        }
    }

    constructor(parcel: Parcel) : this(
        dateFrom = parcel.readLong().toDateItem(),
        dateTo = parcel.readLong().toDateItem()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(dateFrom?.toDate()?.time ?: UNDEFINED_DATE)
        dest.writeLong(dateTo?.toDate()?.time ?: UNDEFINED_DATE)
    }

    override fun describeContents() = 0
}