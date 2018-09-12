package ru.cleverpumpkin.calendar.adapter.manager

import ru.cleverpumpkin.calendar.CalendarDate

internal interface AdapterDataManager {

    fun findDatePosition(date: CalendarDate): Int

    fun getDatesRange(dateFrom: CalendarDate, dateTo: CalendarDate): List<CalendarDate>

    fun notifyDateItemChanged(position: Int)

    fun notifyDateItemsChanged()
}