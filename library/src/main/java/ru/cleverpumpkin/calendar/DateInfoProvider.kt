package ru.cleverpumpkin.calendar

interface DateInfoProvider {

    fun isToday(date: SimpleLocalDate): Boolean

    fun isDateSelected(date: SimpleLocalDate): Boolean

    fun isDateEnabled(date: SimpleLocalDate): Boolean
}