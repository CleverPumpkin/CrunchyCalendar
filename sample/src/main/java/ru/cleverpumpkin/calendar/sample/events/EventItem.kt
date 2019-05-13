package ru.cleverpumpkin.calendar.sample.events

import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView

/**
 * Created by Alexander Surinov on 2019-05-13.
 */
class EventItem(
    override val date: CalendarDate,
    override val color: Int,
    val eventName: String

) : CalendarView.DateIndicator