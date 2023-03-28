package ru.cleverpumpkin.calendar.sample.additionaltexts

import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView

class AdditionalTextsItem(
    override val date: CalendarDate,
    override val text: String,
    override val color: Int

) : CalendarView.AdditionalText