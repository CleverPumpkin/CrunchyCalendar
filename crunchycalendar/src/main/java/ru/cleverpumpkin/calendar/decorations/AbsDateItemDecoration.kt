package ru.cleverpumpkin.calendar.decorations

import android.graphics.Canvas
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.adapter.item.DateItem

/**
 * This class designed for calendar date cells decorating.
 *
 * Clients can extend this class and implement their custom decoration logic for date cell views
 * in [decorateDateView] method.
 */
abstract class AbsDateItemDecoration : RecyclerView.ItemDecoration() {

    private val dateViewRect = Rect()

    /**
     * Method for decorating date view.
     *
     * [canvas] canvas supplied to the RecyclerView
     * [date] specific calendar date for decorating
     * [dateViewRect] rect, that contains positions of date view relative to its parent.
     * This positions can be used to draw on the [canvas].
     */
    abstract fun decorateDateView(canvas: Canvas, date: CalendarDate, dateViewRect: Rect)

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

                decorateDateView(canvas, date, dateViewRect)
            }
        }
    }

}