package ru.cleverpumpkin.calendar

interface DateInfoProvider {

    fun isDateSelected(localDate: SimpleLocalDate): Boolean

    fun isDateEnabled(localDate: SimpleLocalDate): Boolean
}