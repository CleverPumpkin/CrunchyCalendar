package ru.cleverpumpkin.calendar.utils

import androidx.recyclerview.widget.DiffUtil
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.adapter.item.CalendarItem
import ru.cleverpumpkin.calendar.adapter.item.DateItem

internal class AdditionalTextsDiffUtilCallback(
    private val oldList: List<CalendarItem>,
    private val newList: List<CalendarItem>,
    private val oldMap: Map<CalendarDate, List<CalendarView.AdditionalText>>,
    private val newMap: Map<CalendarDate, List<CalendarView.AdditionalText>>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val item = newList[newItemPosition]
        return if (item is DateItem) {
            val date = item.date
            val oldTexts = oldMap[date]
            val newTexts = newMap[date]

            oldTexts == newTexts
        } else {
            true
        }
    }
}