package ru.cleverpumpkin.calendar.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import ru.cleverpumpkin.calendar.CalendarDateView
import ru.cleverpumpkin.calendar.DateCellSelectedState
import ru.cleverpumpkin.calendar.style.CalendarStyleAttributes
import ru.cleverpumpkin.calendar.extension.dpToPix

internal class GridDividerItemDecoration(
    context: Context,
    private val styleAttributes: CalendarStyleAttributes
) : RecyclerView.ItemDecoration() {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.dpToPix(1.0f)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        linePaint.color = styleAttributes.gridColor

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            if (child is CalendarDateView) {
                if (child.cellSelectionState != DateCellSelectedState.NOT_SELECTED && styleAttributes.drawGridOnSelectedDates.not()) {
                    continue
                }

                canvas.drawRect(
                    child.left.toFloat(),
                    child.top.toFloat(),
                    child.right.toFloat(),
                    child.bottom.toFloat(),
                    linePaint
                )
            }
        }
    }
}