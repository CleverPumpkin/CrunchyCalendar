package ru.cleverpumpkin.calendar.utils

import org.joda.time.LocalDate
import java.util.*

fun monthsBetweenTwoDates(startDate: Date, endDate: Date): Int {
    val startLocalDate = LocalDate.fromDateFields(startDate)
    val endLocalDate = LocalDate.fromDateFields(endDate)

    val diffYear = endLocalDate.year - startLocalDate.year
    val diffMonth = endLocalDate.monthOfYear - startLocalDate.monthOfYear

    return diffYear * 12 + diffMonth
}