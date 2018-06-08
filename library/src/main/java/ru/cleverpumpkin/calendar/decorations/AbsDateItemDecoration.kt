package ru.cleverpumpkin.calendar.decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.adapter.item.DateItem

/**
 * TODO Describe class
 */
abstract class AbsDateItemDecoration : RecyclerView.ItemDecoration() {

    private val dateViewRect = Rect()

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as CalendarAdapter

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)
            val calendarItem = adapter.getCalendarItemAt(adapterPosition)

            if (calendarItem is DateItem) {
                val date = calendarItem.date

                dateViewRect.set(
                    child.left,
                    child.top,
                    child.right,
                    child.bottom
                )

                decorateDateItem(canvas, date, dateViewRect)
            }
        }
    }

    abstract fun decorateDateItem(canvas: Canvas, date: CalendarDate, dateViewRect: Rect)
}