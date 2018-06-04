package ru.cleverpumpkin.calendar.decorations

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v7.widget.RecyclerView
import org.joda.time.LocalDate
import ru.cleverpumpkin.calendar.CalendarAdapter

class ItemSelectionDecoration(
    private val selectedDatesSet: Collection<LocalDate>
) : RecyclerView.ItemDecoration() {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    private val lineRect = RectF()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)

            val dayItemViewHolder = parent.findViewHolderForAdapterPosition(adapterPosition)
                    as? CalendarAdapter.DayItemViewHolder
                    ?: continue

            if (selectedDatesSet.contains(dayItemViewHolder.localDate)) {
                lineRect.set(
                    child.left.toFloat(),
                    child.top.toFloat(),
                    child.right.toFloat(),
                    child.bottom.toFloat()
                )

                c.drawRect(lineRect, fillPaint)
            }
        }
    }
}