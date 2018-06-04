package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate

class LimitDateRange(val minDate: LocalDate?, val maxDate: LocalDate?) : Parcelable {

    companion object {
        private const val UNDEFINED_DATE = -1L

        private fun Long.toLocaleDate(): LocalDate? {
            return if (this != UNDEFINED_DATE) {
                LocalDate(this)
            } else {
                null
            }
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<LimitDateRange> {
            override fun createFromParcel(parcel: Parcel) = LimitDateRange(parcel)

            override fun newArray(size: Int) = arrayOfNulls<LimitDateRange>(size)
        }
    }

    constructor(parcel: Parcel) : this(
        minDate = parcel.readLong().toLocaleDate(),
        maxDate = parcel.readLong().toLocaleDate()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(minDate?.toDate()?.time ?: UNDEFINED_DATE)
        dest.writeLong(maxDate?.toDate()?.time ?: UNDEFINED_DATE)
    }

    override fun describeContents() = 0
}