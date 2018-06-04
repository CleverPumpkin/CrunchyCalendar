package ru.cleverpumpkin.calendar.utils

import java.util.*

fun monthsBetweenTwoDates(startDate: Date, endDate: Date): Int {
    val startCalendar = Calendar.getInstance()
    startCalendar.time = startDate

    val endCalendar = Calendar.getInstance()
    endCalendar.time = endDate

    val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
    val diffMonth = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)

    return diffYear * 12 + diffMonth
}